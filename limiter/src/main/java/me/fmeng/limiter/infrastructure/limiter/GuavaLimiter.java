package me.fmeng.limiter.infrastructure.limiter;

import com.google.common.base.Stopwatch;
import com.google.common.util.concurrent.RateLimiter;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class GuavaLimiter implements Limiter {

    private final LimiterItemProperties item;
    private RateLimiter limiter;
    private final String hitKey;

    public GuavaLimiter(String hitKey, LimiterItemProperties item) {
        this.hitKey = hitKey;
        this.item = item;
        // 优化许可是0的情况
        if (item.getPermits() == 0) {
            return;
        }
        long permitsPerSecond = TimeUnit.SECONDS
                .convert(item.getPermits() / item.getRateInterval(), item.getTimeUnit());
        if (Objects.isNull(item.getTimeoutMilliseconds())) {
            this.limiter = RateLimiter.create(permitsPerSecond);
        } else {
            this.limiter = RateLimiter.create(permitsPerSecond, item.getTimeoutMilliseconds(), TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public long tryToPass() throws LimiterException {
        // 优化许可是0的情况
        if (item.getPermits() == 0) {
            throw new LimiterException(item.getExceptionMessage());
        }
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
        if (log.isDebugEnabled()) {
            log.debug("未通过Guava限流器, nowMilliseconds={}, hitKey={}, item={}"
                    , System.currentTimeMillis(), hitKey, item);
        }
        throw new LimiterException(item.getExceptionMessage());
    }
}
