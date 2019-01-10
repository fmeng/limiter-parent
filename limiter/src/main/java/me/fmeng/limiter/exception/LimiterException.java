package me.fmeng.limiter.exception;

/**
 * 如果访问被限制会通过异常的形式抛出
 *
 * @author fmeng
 * @since 2018/07/19
 */
public class LimiterException extends Exception implements LimiterExceptionSupport {

    private static final long serialVersionUID = 663164854784480745L;

    /**
     * 异常提示信息
     */
    private String noticeMessage;

    public LimiterException(String noticeMessage) {
        super(noticeMessage, null, false, false);
        this.noticeMessage = noticeMessage;
    }

    public LimiterException(Throwable throwable, String noticeMessage) {
        super(noticeMessage, throwable, false, false);
        this.noticeMessage = noticeMessage;
    }

    @Override
    public String getNoticeMessage() {
        return this.noticeMessage;
    }
}
