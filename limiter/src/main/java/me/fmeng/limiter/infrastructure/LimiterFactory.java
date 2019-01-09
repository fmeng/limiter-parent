package me.fmeng.limiter.infrastructure;

import me.fmeng.limiter.Limiter;
import me.fmeng.limiter.configure.bean.LimiterItemProperties;

/**
 * 限流器工厂
 *
 * @author fmeng
 * @since 2018/07/30
 */
public interface LimiterFactory {

    /**
     * 创建或者共缓存中查找限流器
     *
     * @param hitKey 命中的限流器的唯一标示
     * @param item   限流器配置
     * @return 限流器
     */
    Limiter create(String hitKey, LimiterItemProperties item);
}
