package me.fmeng.limiter.constant;

/**
 * 全局常量
 *
 * @author fmeng
 * @since 2018/07/25
 */
public interface LimiterConstant {

    /**
     * 限流器key统一前缀
     */
    String LIMITER_KEY_PREFIX = "limiter";

    /**
     * 限流器key字段分隔符
     */
    String LIMITER_KEY_SEPARATOR = ":";

    /**
     * 注解限流器的ORDER
     */
    int LIMITER_ASPECT_ORDER = 200;

    /**
     * 限流器controller切面ORDER
     */
    int LIMITER_CONTROLLER_ADVICE_ORDER = 20;

    /**
     * 限流器返回错误码
     */
    int DEFAULT_ERROR_CODE = 3000;

}
