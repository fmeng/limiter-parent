package me.fmeng.limiter.exception;

/**
 * 异常信息辅助接口
 *
 * @author fmeng
 * @since 2019/01/09
 */
public interface LimiterExceptionSupport {

    /**
     * 获取前端提示信息
     *
     * @return 前端提示信息
     */
    String getNoticeMessage();
}
