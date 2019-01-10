package me.fmeng.limiter.exception;

/**
 * 请求参数不合法
 *
 * @author fmeng
 * @since 2018/07/19
 */
public class RequestParamException extends RuntimeException implements LimiterExceptionSupport {

    private static final long serialVersionUID = 7552569406974825646L;

    /**
     * 异常提示信息
     */
    private String noticeMessage;

    public RequestParamException(String noticeMessage) {
        super(noticeMessage, null, false, false);
        this.noticeMessage = noticeMessage;
    }

    public RequestParamException(Throwable throwable, String noticeMessage) {
        super(noticeMessage, throwable, false, false);
        this.noticeMessage = noticeMessage;
    }

    @Override
    public String getNoticeMessage() {
        return this.noticeMessage;
    }
}
