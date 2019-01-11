package me.fmeng.limiter.configure;

import com.google.common.base.Preconditions;
import me.fmeng.limiter.Hitter;
import me.fmeng.limiter.configure.bean.LimiterProperties;
import me.fmeng.limiter.configure.bean.RedissonProperties;
import me.fmeng.limiter.constant.LimiterConstant;
import me.fmeng.limiter.infrastructure.LimiterFactory;
import me.fmeng.limiter.infrastructure.factory.GuavaLimiterFactory;
import me.fmeng.limiter.infrastructure.factory.LimiterFactoryRouter;
import me.fmeng.limiter.infrastructure.factory.RedisLimiterFactory;
import me.fmeng.limiter.infrastructure.hitter.HitterAutoDelegate;
import me.fmeng.limiter.spring.LimiterAspect;
import me.fmeng.limiter.util.SpringBeanUtils;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;
import java.util.Optional;

/**
 * 自动配置限流器
 *
 * @author fmeng
 * @since 2018/07/27
 */
@Configuration
@ConditionalOnProperty(prefix = LimiterConstant.LIMITER_KEY_PREFIX, name = "enable", havingValue = "true")
public class LimiterAutoConfiguration {

    @Bean
    @ConfigurationProperties(prefix = LimiterConstant.LIMITER_KEY_PREFIX)
    public LimiterProperties limiterProperties() {
        return new LimiterProperties();
    }

    /**
     * 初始化SpringBeanUtils
     */
    @Bean
    public SpringBeanUtils initSpringBeanUtils() {
        return new SpringBeanUtils();
    }

    /**
     * redis配置
     */
    @Bean(name = "redissonClient", destroyMethod = "shutdown")
    public RedissonClient redissonClient(@Autowired @Qualifier("limiterProperties") LimiterProperties limiterProperties) {
        if (limiterProperties.getRedisson() == null) {
            return null;
        }
        return Redisson.create(createRedissonConfig(limiterProperties.getRedisson()));
    }

    /**
     * hitter配置
     */
    @Bean("hitter")
    public Hitter hitterDelegate() {
        return new HitterAutoDelegate();
    }

    /**
     * guava限流器工厂
     */
    @Bean("guavaLimiterFactory")
    public LimiterFactory guavaLimiterFactory() {
        return new GuavaLimiterFactory();
    }

    /**
     * redis限流器工厂
     */
    @ConditionalOnBean(RedissonClient.class)
    @Bean("redisLimiterFactory")
    public LimiterFactory redisLimiterFactory(@Autowired @Qualifier("redissonClient") RedissonClient redissonClient) {
        if (redissonClient == null) {
            return null;
        }
        return new RedisLimiterFactory(redissonClient);
    }

    /**
     * 限流器路由器
     */

    @Bean("limiterFactoryRouter")
    public LimiterFactory limiterFactoryRouter() {
        return new LimiterFactoryRouter();
    }

    /**
     * 限流切面
     */
    @Bean("limiterAspect")
    public LimiterAspect limiterAspect() {
        return new LimiterAspect();
    }

    private Config createRedissonConfig(RedissonProperties redisson) {
        Preconditions.checkArgument(Objects.nonNull(redisson), "redisson不能为空");

        /****************************** 通用配置 ******************************/
        Config config = new Config();
        Optional.ofNullable(redisson.getThreads()).ifPresent(config::setThreads);
        Optional.ofNullable(redisson.getNettyThreads()).ifPresent(config::setNettyThreads);
        Optional.ofNullable(redisson.getTransportMode()).ifPresent(config::setTransportMode);

        /****************************** 单节点配置 ******************************/
        RedissonProperties.SingleServerConfigProperties singleServerConfig = redisson.getSingleServerConfig();
        Preconditions.checkNotNull(singleServerConfig, "singleServerConfig不能为空");
        SingleServerConfig singleServer = config.useSingleServer();
        Optional.ofNullable(singleServerConfig.getIdleConnectionTimeout()).ifPresent(singleServer::setIdleConnectionTimeout);
        Optional.ofNullable(singleServerConfig.getPingTimeout()).ifPresent(singleServer::setPingTimeout);
        Optional.ofNullable(singleServerConfig.getConnectTimeout()).ifPresent(singleServer::setConnectTimeout);
        Optional.ofNullable(singleServerConfig.getTimeout()).ifPresent(singleServer::setTimeout);
        Optional.ofNullable(singleServerConfig.getRetryAttempts()).ifPresent(singleServer::setRetryAttempts);
        Optional.ofNullable(singleServerConfig.getRetryInterval()).ifPresent(singleServer::setRetryInterval);
        Optional.ofNullable(singleServerConfig.getReconnectionTimeout()).ifPresent(singleServer::setReconnectionTimeout);
        Optional.ofNullable(singleServerConfig.getFailedAttempts()).ifPresent(singleServer::setFailedAttempts);
        Optional.ofNullable(singleServerConfig.getSubscriptionsPerConnection()).ifPresent(singleServer::setSubscriptionsPerConnection);
        Optional.ofNullable(singleServerConfig.getSubscriptionConnectionMinimumIdleSize()).ifPresent(singleServer::setSubscriptionConnectionMinimumIdleSize);
        Optional.ofNullable(singleServerConfig.getSubscriptionConnectionPoolSize()).ifPresent(singleServer::setSubscriptionConnectionPoolSize);
        Optional.ofNullable(singleServerConfig.getConnectionMinimumIdleSize()).ifPresent(singleServer::setConnectionMinimumIdleSize);
        Optional.ofNullable(singleServerConfig.getConnectionPoolSize()).ifPresent(singleServer::setConnectionPoolSize);
        // 业务可变配置
        Optional.ofNullable(singleServerConfig.getDatabase()).ifPresent(singleServer::setDatabase);
        Optional.ofNullable(singleServerConfig.getPassword()).ifPresent(singleServer::setPassword);
        Optional.ofNullable(singleServerConfig.getAddress()).ifPresent(singleServer::setAddress);
        return config;
    }
}
