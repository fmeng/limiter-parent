package me.fmeng.limiter.configure.bean;

import lombok.Data;
import me.fmeng.limiter.Hitter;
import me.fmeng.limiter.constant.LimiterStrategyTypeEnum;
import me.fmeng.limiter.infrastructure.factory.BaseCachedLimiterFactory;
import me.fmeng.limiter.infrastructure.factory.GuavaLimiterFactory;
import me.fmeng.limiter.infrastructure.hitter.HitterAutoDelegate;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.concurrent.TimeUnit;

/**
 * 限流项配置
 *
 * @author fmeng
 * @since 2018/07/27
 */
@Data
public class LimiterItemProperties {

    /**
     * 是否启用
     */
    @NotNull
    private Boolean enable = Boolean.TRUE;

    /**
     * 限流项目的名字会放到redis的key中
     */
    @NotBlank
    private String name;

    /**
     * 业务描述可以扩展限流器维度的配置
     */
    private String bizDescription;

    /**
     * 限流器工厂的内部实现
     */
    @NotNull
    private Class<? extends BaseCachedLimiterFactory> limiterFactoryClass = GuavaLimiterFactory.class;

    /**
     * 限流器控制的策略
     */
    @NotNull
    private LimiterStrategyTypeEnum limiterStrategyType;

    /**
     * 每次获取的许可数量
     */
    @NotNull
    private Integer permits;

    /**
     * 被限流的单位长度, Guava实现的限流器忽略改值
     */
    private Long rateInterval = 1L;

    /**
     * 被限流的时间单位, 只能是: SECONDS,MINUTES,HOURS,DAYS
     */
    @NotNull
    private TimeUnit timeUnit = TimeUnit.SECONDS;

    /**
     * 获取许可的超时时间(毫秒)
     */
    private Long timeoutMilliseconds;

    /**
     * 对要限流的资源描述
     */
    @Valid
    @NestedConfigurationProperty
    private LimiterResourceProperties resource;

    /**
     * 限流提示信息
     */
    @NotBlank
    private String exceptionMessage = "业务限流,请稍后再试";

    /**
     * 命中器的class
     */
    private Class<? extends Hitter> customHitterClass = HitterAutoDelegate.class;

    /**
     * 启用本地缓存Limiter
     */
    private Boolean enableLocalLimiterCache = Boolean.TRUE;
}
