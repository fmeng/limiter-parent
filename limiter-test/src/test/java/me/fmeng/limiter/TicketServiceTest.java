package me.fmeng.limiter;

import org.junit.Test;

import javax.annotation.Resource;

/**
 * @author fmeng
 * @since 2019/01/09
 */
public class TicketServiceTest extends AbstractTest {

    @Resource
    private TicketService ticketService;

    @Test
    public void guavaAnnotationServiceGuava10pTest() {
        parallelLookInvoke(ticketService::guavaList, "guavaList");
    }

    @Test
    public void redisAnnotationServiceRedis10pTest() {
        parallelLookInvoke(ticketService::redisList, "redisList");
    }
}
