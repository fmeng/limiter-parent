package me.fmeng.limiter.spring.mvc;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import me.fmeng.limiter.constant.LimiterConstant;
import me.fmeng.limiter.exception.LimiterException;
import me.fmeng.limiter.exception.RequestParamException;
import me.fmeng.limiter.infrastructure.hitter.ResourceBO;
import me.fmeng.limiter.infrastructure.hitter.ResourceBoHolder;
import me.fmeng.limiter.util.JsonUtils;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 在ssf框架的之前拦截，主要控制业务
 *
 * @author fmeng
 * @since 2018/07/30
 */
@Slf4j
@ControllerAdvice
@Order(LimiterConstant.LIMITER_CONTROLLER_ADVICE_ORDER)
public class LimiterTranslator {

    /**
     * 限流异常,自动转换成前端视图
     *
     * @param e 被限流时抛出的异常
     * @return 前端视图
     */
    @ExceptionHandler(LimiterException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Object limiterException(LimiterException e) {
        printLog(e, ResourceBoHolder.get());
        return new LimiterResult(e.getNoticeMessage());
    }

    /**
     * 参数请求,自动转换成前端视图
     *
     * @param e 参数请求异常
     * @return 前端视图
     */
    @ExceptionHandler(RequestParamException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Object requestParamException(RequestParamException e) {
        return new LimiterResult(e.getNoticeMessage());
    }

    /**
     * 记录日志
     *
     * @param e          限流器异常
     * @param resourceBO 被限制的资源
     */
    protected void printLog(LimiterException e, ResourceBO resourceBO) {
        log.warn("触发限流策略,限流生效,记录用户请求信息, noticeMessage={}, requestUrl={}, requestMethod={}, parameterMap={}, annotationLimiterNames={}"
                , e.getNoticeMessage()
                , resourceBO.getRequestUrl()
                , resourceBO.getRequestMethod()
                , JsonUtils.objectToJsonQuietly(resourceBO.getParameterMap())
                , JsonUtils.objectToJsonQuietly(resourceBO.getAnnotationLimiterNames()));
    }

    /**
     * 被限流后默认返回的前端视图
     */
    @ToString
    @EqualsAndHashCode
    @Getter
    private static class LimiterResult {

        /**
         * 成功标识
         */
        private Boolean success;

        /**
         * 返回结果状态码
         */
        private Integer code;

        /**
         * 结果信息
         */
        private String message;

        private LimiterResult(String message) {
            this.message = message;
            this.success = false;
            this.code = LimiterConstant.DEFAULT_ERROR_CODE;
        }
    }
}
