package me.fmeng.limiter.infrastructure.factory;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.extern.slf4j.Slf4j;
import me.fmeng.limiter.Limiter;
import me.fmeng.limiter.configure.bean.LimiterItemProperties;
import me.fmeng.limiter.infrastructure.LimiterFactory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * 提供缓存支持的限流器工厂
 *
 * @author fmeng
 * @since 2019/01/08
 */
@Slf4j
public abstract class BaseCachedLimiterFactory implements LimiterFactory {

    /**
     * 根据hitKey缓存限流器<hitKey, 实现的限流器>
     */
    private final Cache<String, Limiter> hitKeyLimiterCache = CacheBuilder.newBuilder()
            .initialCapacity(100)
            .concurrencyLevel(5)
            .expireAfterWrite(1, TimeUnit.HOURS)
            .build();

    /**
     * 创建或者共缓存中查找限流器
     *
     * @param hitKey 命中的限流器的唯一标示
     * @param item   限流器配置
     * @return 限流器
     */
    @Override
    public Limiter create(String hitKey, LimiterItemProperties item) {
        // 没开启缓存，直接创建
        if (!item.getEnableLocalLimiterCache()) {
            return doCreate(hitKey, item);
        }
        // 开启缓存，先从缓存中取
        try {
            return hitKeyLimiterCache.get(hitKey, () -> this.doCreate(hitKey, item));
        } catch (ExecutionException e) {
            // 创建限流器失败
            log.error("创建限流器失败, hitKey={}, item={}", hitKey, item, e);
            throw new RuntimeException("创建限流器失败", e);
        }
    }

    /**
     * 创建或者共缓存中查找限流器
     *
     * @param hitKey 命中的限流器的唯一标示
     * @param item   限流器配置
     * @return 限流器
     */
    protected abstract Limiter doCreate(String hitKey, LimiterItemProperties item);
}
