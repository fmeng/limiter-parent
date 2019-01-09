package me.fmeng.limiter;

import me.fmeng.limiter.exception.LimiterException;

/**
 * 控制限流过程
 *
 * @author fmeng
 * @since 2018/07/19
 */
public interface Limiter {

    /**
     * 尝试执行被限流的过程(可能会被阻塞)
     *
     * @return 执行时间
     * @throws LimiterException 1. 正常被限流没有获得许可; 2. 等待超时没有获得执行权
     */
    long tryToPass() throws LimiterException;
}