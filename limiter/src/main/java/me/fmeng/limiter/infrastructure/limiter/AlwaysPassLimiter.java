package me.fmeng.limiter.infrastructure.limiter;

import me.fmeng.limiter.Limiter;
import me.fmeng.limiter.exception.LimiterException;

/**
 * 不限流的限流器
 *
 * @author fmeng
 * @since 2018/07/30
 */
public class AlwaysPassLimiter implements Limiter {

    private static final Limiter SINGLETON = new AlwaysPassLimiter();

    @Override
    public long tryToPass() throws LimiterException {
        return 0;
    }

    public static Limiter singleton() {
        return SINGLETON;
    }
}
