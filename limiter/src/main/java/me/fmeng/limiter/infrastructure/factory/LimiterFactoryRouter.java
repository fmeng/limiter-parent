package me.fmeng.limiter.infrastructure.factory;

import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import me.fmeng.limiter.Limiter;
import me.fmeng.limiter.configure.bean.LimiterItemProperties;
import me.fmeng.limiter.infrastructure.LimiterFactory;
import me.fmeng.limiter.util.SpringBeanUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * 有路由功能的限流器工厂
 *
 * @author fmeng
 * @since 2018/07/27
 */
@Slf4j
public class LimiterFactoryRouter implements LimiterFactory {

    /**
     * 创建或者共缓存中查找限流器
     *
     * @param hitKey 命中的限流器的唯一标示
     * @param item   限流器配置
     * @return 限流器
     */
    @Override
    public Limiter create(String hitKey, LimiterItemProperties item) {
        // 参数校验
        Preconditions.checkArgument(StringUtils.isNotBlank(hitKey), "hitKey不能为空");
        Preconditions.checkArgument(Objects.nonNull(item), "item不能为空");
        Preconditions.checkArgument(Boolean.TRUE.equals(item.getEnable()), "限流器没有启用");
        LimiterFactory limiterFactory = getLimiterFactory(item.getLimiterFactoryClass());
        return limiterFactory.create(hitKey, item);
    }

    /**
     * 获取限流器工厂Bean
     * 先从Spring容器中获取，没有获取到，手动注册到容器
     *
     * @param limiterFactoryClass 限流器工厂的内部实现
     * @return 限流器工厂
     */
    private <T extends BaseCachedLimiterFactory> T getLimiterFactory(Class<T> limiterFactoryClass) {
        // 从Spring容器中获取
        try {
            return SpringBeanUtils.getBean(limiterFactoryClass);
        } catch (Exception e) {
            throw new ExceptionInInitializerError(limiterFactoryClass + " 初始化失败");
        }
    }
}
