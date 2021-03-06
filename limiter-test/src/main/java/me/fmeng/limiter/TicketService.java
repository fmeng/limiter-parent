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

    @Limiter("redis-10")
    public Object redisList() {
        return ImmutableMap.of("currentTimeMillis", System.currentTimeMillis());
    }
}
