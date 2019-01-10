package me.fmeng.limiter;

import me.fmeng.limiter.configure.bean.LimiterItemProperties;
import me.fmeng.limiter.infrastructure.factory.BaseCachedLimiterFactory;
import me.fmeng.limiter.infrastructure.factory.GuavaLimiterFactory;
import org.springframework.stereotype.Component;

/**
 * @author fmeng
 * @since 2019/01/10
 */
@Component
public class CustomLimiterFactory extends BaseCachedLimiterFactory {

    private GuavaLimiterFactory guavaLimiterFactory = new GuavaLimiterFactory();

    /**
     * 创建或者共缓存中查找限流器
     *
     * @param hitKey 命中的限流器的唯一标示
     * @param item   限流器配置
     * @return 限流器
     */
    @Override
    protected Limiter doCreate(String hitKey, LimiterItemProperties item) {
        return guavaLimiterFactory.create(hitKey, item);
    }
}
