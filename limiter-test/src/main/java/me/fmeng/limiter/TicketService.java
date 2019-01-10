package me.fmeng.limiter;

import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import me.fmeng.limiter.annotation.Limiter;
import org.springframework.stereotype.Service;

/**
 * @author fmeng
 * @since 2019/01/09
 */
@Slf4j
@Service
public class TicketService {

    @Limiter("guava-10")
    public Object guavaList() {
        long currentTimeMillis = System.currentTimeMillis();
        return ImmutableMap.of("currentTimeMillis", currentTimeMillis);
    }

    public Object guavaRemainderInfo() {
        long currentTimeMillis = System.currentTimeMillis();
        return ImmutableMap.of("currentTimeMillis", currentTimeMillis);
    }

    @Limiter("redis-10")
    public Object redisList() {
        log.info("测试时间{}", System.currentTimeMillis());
        return ImmutableMap.of("currentTimeMillis", System.currentTimeMillis());
    }

    public Object redisRemainderInfo() {
        long currentTimeMillis = System.currentTimeMillis();
        return ImmutableMap.of("currentTimeMillis", currentTimeMillis);
    }
}
