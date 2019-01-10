package me.fmeng.limiter.spring.mvc;

import lombok.extern.slf4j.Slf4j;
import me.fmeng.limiter.infrastructure.hitter.ResourceBO;
import me.fmeng.limiter.infrastructure.hitter.ResourceBoHolder;
import me.fmeng.limiter.spring.LimiterDriveSupport;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;

/**
 * 通过拦截器的形式, 获取数据, 限流
 *
 * @author fmeng
 * @since 2018/07/27
 */
@Slf4j
@Order(100)
public class LimiterInterceptor extends LimiterDriveSupport implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 全局限流开关没有启用
        if (!limiterProperties.getEnable()) {
            return true;
        }
        /****************************** 从请求中获取数据,放到当前线程 ******************************/
        ResourceBO resourceBO = new ResourceBO();
        resourceBO.setRequestUrl(request.getRequestURI());
        resourceBO.setRequestMethod(RequestMethod.valueOf(request.getMethod()));
        resourceBO.setParameterMap(request.getParameterMap() == null ? Collections.emptyMap() : request.getParameterMap());
        // 填充额外的资源信息
        resourceBO = fillResourceBO(resourceBO);
        ResourceBoHolder.set(resourceBO);
        /****************************** 限流逻辑 ******************************/
        tryToPassAllLimiter(resourceBO);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        ResourceBoHolder.remove();
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        ResourceBoHolder.remove();
    }
}
