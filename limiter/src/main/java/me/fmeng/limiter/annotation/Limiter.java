package me.fmeng.limiter.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 限流器的注解
 *
 * @author fmeng
 * @since 2018/07/24
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Limiter {

    /**
     * 限流项的名称，在配置文件中配置
     */
    String value() default "";
}