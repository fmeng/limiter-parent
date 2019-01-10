package me.fmeng.limiter;

import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import me.fmeng.limiter.exception.LimiterException;
import me.fmeng.limiter.exception.RequestParamException;
import me.fmeng.limiter.infrastructure.hitter.ResourceBoHolder;
import me.fmeng.limiter.spring.mvc.LimiterTranslator;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author fmeng
 * @since 2019/01/10
 */
@Slf4j
@ControllerAdvice
public class CustomLimiterTranslator extends LimiterTranslator {
    /**
     * 限流异常,自动转换成前端视图
     *
     * @param e 被限流时抛出的异常
     * @return 前端视图
     */
    @ExceptionHandler(LimiterException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @Override
    public Object limiterException(LimiterException e) {
        printLog(e, ResourceBoHolder.get());
        return ImmutableMap.of("message", e.getNoticeMessage());
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
    @Override
    public Object requestParamException(RequestParamException e) {
        return ImmutableMap.of("message", e.getNoticeMessage());
    }
}
