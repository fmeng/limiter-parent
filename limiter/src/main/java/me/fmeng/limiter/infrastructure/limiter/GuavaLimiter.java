package me.fmeng.limiter.infrastructure.limiter;

import com.google.common.base.Stopwatch;
import com.google.common.util.concurrent.RateLimiter;
import me.fmeng.limiter.Limiter;
import me.fmeng.limiter.configure.bean.LimiterItemProperties;
import me.fmeng.limiter.exception.LimiterException;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * guava实现单个限流器
 *
 * @author fmeng
 * @since 2018/07/27
 */
public class GuavaLimiter implements Limiter {

    private final LimiterItemProperties item;
    private final RateLimiter limiter;

    public GuavaLimiter(LimiterItemProperties item) {
        this.item = item;
        long permitsPerSecond = TimeUnit.SECONDS.convert(item.getPermits(), item.getTimeUnit());
        if (Objects.isNull(item.getTimeoutMilliseconds())) {
            this.limiter = RateLimiter.create(permitsPerSecond);
        } else {
            this.limiter = RateLimiter.create(permitsPerSecond, item.getTimeoutMilliseconds(), TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public long tryToPass() throws LimiterException {
        Stopwatch stopwatch = Stopwatch.createStarted();
        boolean pass;
        if (Objects.isNull(item.getTimeoutMilliseconds())) {
            pass = limiter.tryAcquire(1);
        } else {
            pass = limiter.tryAcquire(1, item.getTimeoutMilliseconds(), TimeUnit.MILLISECONDS);
        }
        if (pass) {
            return stopwatch.stop().elapsed(TimeUnit.MILLISECONDS);
        }
        throw new LimiterException(item.getExceptionMessage());
    }
}
