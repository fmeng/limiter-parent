package me.fmeng.limiter.exception;

import lombok.Getter;

/**
 * 如果访问被限制会通过异常的形式抛出
 *
 * @author fmeng
 * @since 2018/07/19
 */
public class LimiterException extends Exception {

    private static final long serialVersionUID = 663164854784480745L;

    /**
     * 异常提示信息
     */
    @Getter
    private String exceptionMessage;

    public LimiterException(String exceptionMessage) {
        super(exceptionMessage);
        this.exceptionMessage = exceptionMessage;
    }

    public LimiterException(Throwable throwable, String exceptionMessage) {
        super(exceptionMessage, throwable);
        this.exceptionMessage = exceptionMessage;
    }
}
