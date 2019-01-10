package me.fmeng.limiter.spring;

import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import me.fmeng.limiter.Hitter;
import me.fmeng.limiter.Limiter;
import me.fmeng.limiter.configure.bean.LimiterItemProperties;
import me.fmeng.limiter.configure.bean.LimiterProperties;
import me.fmeng.limiter.exception.LimiterException;
import me.fmeng.limiter.infrastructure.LimiterFactory;
import me.fmeng.limiter.infrastructure.hitter.ResourceBO;
import me.fmeng.limiter.util.HitKeyUtils;
import org.springframework.beans.factory.InitializingBean;

import javax.annotation.Resource;
import java.util.stream.Collectors;

/**
 * 驱动限流器执行的组件
 *
 * @author fmeng
 * @since 2018/07/30
 */
@Slf4j
public class LimiterDriveSupport implements InitializingBean {

    /**
     * 配置属性
     */
    @Resource
    protected LimiterProperties limiterProperties;

    /**
     * 命中器判断
     */
    @Resource
    protected Hitter hitter;

    /**
     * 限流工厂获取限流器
     */
    @Resource
    protected LimiterFactory limiterFactoryRouter;

    /**
     * 不可变对象, <name, 限流项>
     */
    protected ImmutableMap<String, LimiterItemProperties> nameItemMap;

    /**
     * 尝试通过所有的限流器
     *
     * @param resourceBO 资源
     * @throws Exception 限流异常或业务异常
     */
    protected long tryToPassAllLimiter(ResourceBO resourceBO) throws Exception {
        long handledLimiterMillisecondsTemp = 0;
        for (LimiterItemProperties item : limiterProperties.getItems()) {
            // 限流器没有启用
            if (!Boolean.TRUE.equals(item.getEnable())) {
                continue;
            }
            boolean hit = hitter.hit(resourceBO, item);
            if (hit) {
                String hitKey = HitKeyUtils.generateKey(limiterProperties.getAppId(), resourceBO, item);
                Limiter limiter = limiterFactoryRouter.create(hitKey, item);
                // tryToPass 会抛出限流异常或业务异常
                long passMilliseconds = limiter.tryToPass();
                // 所有限流器的总时间控制
                handledLimiterMillisecondsTemp += passMilliseconds;
                if (handledLimiterMillisecondsTemp > limiterProperties.getAllLimiterTimeoutMilliseconds()) {
                    if (log.isDebugEnabled()) {
                        log.debug("全局限流生效, resourceBO={}， nowMilliseconds={}, passMilliseconds={}, limiterPassMilliseconds={}"
                                , resourceBO, System.currentTimeMillis(), handledLimiterMillisecondsTemp, limiterProperties.getAllLimiterTimeoutMilliseconds());
                    }
                    throw new LimiterException(limiterProperties.getExceptionMessage());
                }
                if (log.isDebugEnabled()) {
                    log.debug("通过限流器hitKey={}, nowMilliseconds={}, passMilliseconds={}, resourceBO={}"
                            , hitKey, System.currentTimeMillis(), passMilliseconds, resourceBO);
                }
            }
        }
        return handledLimiterMillisecondsTemp;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.nameItemMap = initNameItemMap(limiterProperties);
    }

    /**
     * 初始化nameItemMap
     *
     * @param limiterProperties 配置项
     * @return 不可变对象, <name, 限流项>
     */
    private static ImmutableMap<String, LimiterItemProperties> initNameItemMap(LimiterProperties limiterProperties) {
        return limiterProperties.getItems().stream()
                .collect(Collectors.collectingAndThen(Collectors
                                .toMap(LimiterItemProperties::getName, o -> o)
                        , ImmutableMap::copyOf));
    }

    /**
     * 填充资源信息
     *
     * @return 资源信息
     */
    protected ResourceBO fillResourceBO(ResourceBO resourceBO) {
        return resourceBO;
    }

}
