package me.fmeng.limiter.infrastructure.limiter;

import com.google.common.base.Stopwatch;
import me.fmeng.limiter.Limiter;
import me.fmeng.limiter.configure.bean.LimiterItemProperties;
import me.fmeng.limiter.exception.LimiterException;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Redis实现分布式限流器
 *
 * @author fmeng
 * @since 2018/08/29
 */
public class RedisLimiter implements Limiter {

    private final String hitKey;
    private final LimiterItemProperties item;
    private final RRateLimiter limiter;

    public RedisLimiter(String hitKey, RedissonClient redissonClient, LimiterItemProperties item) {
        this.hitKey = hitKey;
        this.item = item;
        RRateLimiter rateLimiter = redissonClient.getRateLimiter(hitKey);
        rateLimiter.trySetRate(RateType.OVERALL, item.getPermits(), item.getRateInterval(), timeUnitToRateIntervalUnit(item.getTimeUnit()));
        limiter = rateLimiter;
    }

    @Override
    public long tryToPass() throws LimiterException {
        Stopwatch stopwatch = Stopwatch.createStarted();
        boolean pass;
        if (Objects.nonNull(item.getTimeoutMilliseconds())) {
            pass = limiter.tryAcquire(1, item.getTimeoutMilliseconds(), TimeUnit.MILLISECONDS);
        } else {
            pass = limiter.tryAcquire(1);
        }
        if (pass) {
            return stopwatch.stop().elapsed(TimeUnit.MILLISECONDS);
        }
        throw new LimiterException(item.getExceptionMessage());
    }

    private RateIntervalUnit timeUnitToRateIntervalUnit(TimeUnit timeUnit) {
        switch (timeUnit) {
            case SECONDS:
                return RateIntervalUnit.SECONDS;
            case MINUTES:
                return RateIntervalUnit.MINUTES;
            case HOURS:
                return RateIntervalUnit.HOURS;
            case DAYS:
                return RateIntervalUnit.DAYS;
            default:
                throw new UnsupportedOperationException("不支持该时间类型");
        }
    }
}
