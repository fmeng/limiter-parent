package me.fmeng.limiter.constant;

/**
 * 限流控制的策略
 *
 * @author fmeng
 * @since 2018/08/01
 */
public enum LimiterStrategyTypeEnum {
    /**
     * 只通过注解控制
     */
    ANNOTATION,

    /**
     * 只通过url控制
     */
    URL,

    /**
     * 自定义控制策略
     */
    CUSTOM
}
