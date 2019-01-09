package me.fmeng.limiter;

import me.fmeng.limiter.configure.bean.LimiterItemProperties;
import me.fmeng.limiter.infrastructure.hitter.ResourceBO;

/**
 * 命中器, 判断请求是否命中一个限流器的限流策略
 *
 * @author fmeng
 * @since 2018/07/27
 */
public interface Hitter {

    /**
     * 判断是否命中限流策略
     *
     * @param requestBO 请求参数
     * @param item      配置项
     * @return 是否命中限流策略 true:命中限流策略
     */
    boolean hit(ResourceBO requestBO, LimiterItemProperties item);
}
