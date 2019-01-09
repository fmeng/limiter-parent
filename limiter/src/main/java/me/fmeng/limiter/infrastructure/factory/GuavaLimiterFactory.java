package me.fmeng.limiter.infrastructure.factory;

import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import me.fmeng.limiter.Limiter;
import me.fmeng.limiter.configure.bean.LimiterItemProperties;
import me.fmeng.limiter.infrastructure.limiter.GuavaLimiter;
import org.apache.commons.lang3.StringUtils;

/**
 * Guava实现限流器的工厂
 *
 * @author fmeng
 * @since 2018/07/30
 */
@Slf4j
public class GuavaLimiterFactory extends BaseCachedLimiterFactory {

    /**
     * 创建或者共缓存中查找限流器
     *
     * @param hitKey 命中的限流器的唯一标示
     * @param item   限流器配置
     * @return 限流器
     */
    @Override
    protected Limiter doCreate(String hitKey, LimiterItemProperties item) {
        // 参数校验
        Preconditions.checkArgument(StringUtils.isNotBlank(hitKey), "hitKey不能为空");
        Preconditions.checkArgument(GuavaLimiterFactory.class.equals(item.getLimiterFactoryClass()), "item匹配异常限流器工厂");
        Preconditions.checkArgument(Boolean.TRUE.equals(item.getEnable()), "Redis限流器没有启用");
        return new GuavaLimiter(item);
    }
}
