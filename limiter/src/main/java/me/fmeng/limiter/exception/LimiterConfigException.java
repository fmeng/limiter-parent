package me.fmeng.limiter.exception;

/**
 * 配置信息
 *
 * @author fmeng
 * @since 2018/07/19
 */
public class LimiterConfigException extends RuntimeException implements LimiterExceptionSupport {

    private static final long serialVersionUID = 4346399205331905231L;

    /**
     * 异常提示信息
     */
    private String noticeMessage;

    public LimiterConfigException(String noticeMessage) {
        super(noticeMessage, null, false, false);
        this.noticeMessage = noticeMessage;
    }

    public LimiterConfigException(Throwable throwable, String noticeMessage) {
        super(noticeMessage, throwable, false, false);
        this.noticeMessage = noticeMessage;
    }

    @Override
    public String getNoticeMessage() {
        return this.noticeMessage;
    }
}
