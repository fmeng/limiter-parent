package me.fmeng.limiter.infrastructure.limiter;

import com.google.common.base.Stopwatch;
import lombok.extern.slf4j.Slf4j;
import me.fmeng.limiter.Limiter;
import me.fmeng.limiter.configure.bean.LimiterItemProperties;
import me.fmeng.limiter.exception.LimiterConfigException;
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
@Slf4j
public class RedisLimiter implements Limiter {

    private final String hitKey;
    private final LimiterItemProperties item;
    private RRateLimiter limiter;

    public RedisLimiter(String hitKey, RedissonClient redissonClient, LimiterItemProperties item) {
        this.hitKey = hitKey;
        this.item = item;
        // 优化许可是0的情况
        if (item.getPermits() == 0) {
            return;
        }
        RRateLimiter rateLimiter = redissonClient.getRateLimiter(hitKey);
        rateLimiter.trySetRate(RateType.OVERALL, item.getPermits(), item.getRateInterval(), timeUnitToRateIntervalUnit(item.getTimeUnit()));
        limiter = rateLimiter;
    }

    @Override
    public long tryToPass() throws LimiterException {
        // 优化许可是0的情况
        if (item.getPermits() == 0) {
            throw new LimiterException(item.getExceptionMessage());
        }
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
        if (log.isDebugEnabled()) {
            log.debug("未通过Redis限流器, nowMilliseconds={}, hitKey={}, item={}"
                    , System.currentTimeMillis(), hitKey, item);
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
                throw new LimiterConfigException("不支持该时间类型");
        }
    }
}
