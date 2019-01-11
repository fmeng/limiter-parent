package me.fmeng.limiter.configure;

import me.fmeng.limiter.constant.LimiterConstant;
import me.fmeng.limiter.spring.mvc.LimiterInterceptor;
import me.fmeng.limiter.spring.mvc.LimiterTranslator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * web mvc 配置
 *
 * @author fmeng
 * @since 2018/08/04
 */
@Configuration
@Import(LimiterAutoConfiguration.class)
@ConditionalOnWebApplication
@ConditionalOnProperty(prefix = LimiterConstant.LIMITER_KEY_PREFIX, name = "enable", havingValue = "true")
public class LimiterMvcAutoConfiguration {

    /**
     * 异常处理器
     */
    @ConditionalOnMissingBean(LimiterTranslator.class)
    @Bean("limiterTranslator")
    public LimiterTranslator limiterTranslator() {
        return new LimiterTranslator();
    }

    /**
     * 拦截器
     */
    @ConditionalOnMissingBean(LimiterInterceptor.class)
    @Bean("limiterInterceptor")
    public LimiterInterceptor limiterInterceptor() {
        return new LimiterInterceptor();
    }

    /***
     * 添加到mvc容器
     */
    @ConditionalOnBean(LimiterInterceptor.class)
    @Bean
    public WebMvcConfigurer webMvcConfigurer(@Autowired @Qualifier("limiterInterceptor") LimiterInterceptor limiterInterceptor) {
        return new WebMvcConfigurerAdapter() {
            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                registry.addInterceptor(limiterInterceptor);
            }
        };
    }

}
