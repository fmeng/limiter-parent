package me.fmeng.limiter;

import me.fmeng.limiter.spring.mvc.LimiterInterceptor;
import me.fmeng.limiter.spring.mvc.LimiterTranslator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author fmeng
 * @since 2019/01/10
 */
@ConditionalOnWebApplication
@Configuration
public class LimiterConfig {

    /**
     * 异常处理器
     */
    @Bean("limiterTranslator")
    public LimiterTranslator limiterTranslator() {
        return new CustomLimiterTranslator();
    }

    /**
     * 拦截器
     */
    @Bean("limiterInterceptor")
    public LimiterInterceptor limiterInterceptor() {
        return new CustomLimiterInterceptor();
    }
}
