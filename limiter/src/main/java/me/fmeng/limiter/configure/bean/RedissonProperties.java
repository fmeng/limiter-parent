package me.fmeng.limiter.configure.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import me.fmeng.limiter.constant.LimiterConstant;
import org.redisson.client.codec.Codec;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.TransportMode;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * Redisson 配置
 *
 * @author fmeng
 * @see <a href="https://github.com/redisson/redisson/wiki/2.-%E9%85%8D%E7%BD%AE%E6%96%B9%E6%B3%95#261-%E5%8D%95%E8%8A%82%E7%82%B9%E8%AE%BE%E7%BD%AE">配置方法-单节点设置</a>
 * @see <a href="http://www.redis.cn/topics/config.html">Redis官网配置</a>
 * @since 2018/07/31
 */
@Data
@ConfigurationProperties(prefix = LimiterConstant.LIMITER_KEY_PREFIX + ".redisson")
public class RedissonProperties {

    /**
     * redis单节点配置
     * https://github.com/redisson/redisson/wiki/2.-%E9%85%8D%E7%BD%AE%E6%96%B9%E6%B3%95#261-%E5%8D%95%E8%8A%82%E7%82%B9%E8%AE%BE%E7%BD%AE
     * http://www.redis.cn/topics/config.html
     */
    @NotNull
    @Valid
    private SingleServerConfigProperties singleServerConfig;

    /**
     * 线程池数量
     * 默认值: 当前处理核数量 * 2
     * <p>
     * 这个线程池数量被所有RTopic对象监听器，RRemoteService调用者和RExecutorService任务共同共享
     */
    @NotNull
    private Integer threads = Runtime.getRuntime().availableProcessors() * 2;

    /**
     * Netty线程池数量
     * 默认值: 当前处理核数量 * 2
     * <p>
     * 这个线程池数量是在一个Redisson实例内，被其创建的所有分布式数据类型和服务，以及底层客户端所一同共享的线程池里保存的线程数量。
     */
    @NotNull
    private Integer nettyThreads = Runtime.getRuntime().availableProcessors() * 2;

    /**
     * 传输模式
     * 默认值：TransportMode.NIO
     * 可选参数：
     * TransportMode.NIO,
     * TransportMode.EPOLL - 需要依赖里有netty-transport-native-epoll包（Linux）
     * TransportMode.KQUEUE - 需要依赖里有 netty-transport-native-kqueue包（macOS）
     */
    @NotNull
    private TransportMode transportMode = TransportMode.NIO;

    /**
     * 编码
     * 默认值: org.redisson.codec.JsonJacksonCodec
     * Redisson的对象编码类是用于将对象进行序列化和反序列化，以实现对该对象在Redis里的读取和存储。Redisson提供了以下几种的对象编码应用，以供大家选择
     */
    @NotEmpty
    private Class<? extends Codec> codec = JsonJacksonCodec.class;

    /**
     * 编码器实例
     */
    @JsonIgnore
    private Codec codecInstance;

    @Data
    @ConfigurationProperties(prefix = LimiterConstant.LIMITER_KEY_PREFIX + ".redisson" + ".singleServerConfig")
    public static class SingleServerConfigProperties {

        /**
         * 连接空闲超时，单位：毫秒
         * 默认值：10000
         * 如果当前连接池里的连接数量超过了最小空闲连接数，而同时有连接空闲时间超过了该数值，那么这些连接将会自动被关闭，并从连接池里去掉。时间单位是毫秒
         */
        @NotNull
        private Integer idleConnectionTimeout = 10000;

        /**
         * ping节点超时,单位：毫秒,默认1000
         */
        @NotNull
        private Integer pingTimeout = 1000;

        /**
         * 连接超时，单位：毫秒
         * 默认值：10000
         * 同任何节点建立连接时的等待超时。时间单位是毫秒。
         */
        @NotNull
        private Integer connectTimeout = 10000;

        /**
         * 命令等待超时，单位：毫秒
         * 默认值：3000
         * 等待节点回复命令的时间。该时间从命令发送成功时开始计时
         */
        @NotNull
        private Integer timeout = 3000;

        /**
         * 命令失败重试次数
         * 默认值：3
         * 如果尝试达到 retryAttempts（命令失败重试次数） 仍然不能将命令发送至某个指定的节点时，将抛出错误。如果尝试在此限制之内发送成功，则开始启用 timeout（命令等待超时） 计时
         */
        @NotNull
        private Integer retryAttempts = 3;

        /**
         * 命令重试发送时间间隔，单位：毫秒
         * 默认值：1500
         * 在一条命令发送失败以后，等待重试发送的时间间隔。时间单位是毫秒
         */
        @NotNull
        private Integer retryInterval = 1500;

        /**
         * 重新连接时间间隔，单位：毫秒
         * 默认值：3000
         * 当与某个节点的连接断开时，等待与其重新建立连接的时间间隔。时间单位是毫秒
         */
        @NotNull
        private Integer reconnectionTimeout = 3000;

        /**
         * 执行失败最大次数
         * 默认值：3
         * 在某个节点执行相同或不同命令时，连续 失败 failedAttempts（执行失败最大次数） 时，该节点将被从可用节点列表里清除，直到 reconnectionTimeout（重新连接时间间隔） 超时以后再次尝试
         */
        @NotNull
        private Integer failedAttempts = 3;

        /**
         * 单个连接最大订阅数量
         * 默认值：5
         * 每个连接的最大订阅数量
         */
        @NotNull
        private Integer subscriptionsPerConnection = 5;

        /**
         * 客户端名称
         * 默认值：null
         * <p>
         * 在Redis节点里显示的客户端名称
         */
        @NotEmpty
        private String clientName = LimiterConstant.LIMITER_KEY_PREFIX;

        /**
         * 从节点发布和订阅连接的最小空闲连接数
         * 默认值：1
         * 多从节点的环境里，每个 从服务节点里用于发布和订阅连接的最小保持连接数（长连接）。Redisson内部经常通过发布和订阅来实现许多功能。长期保持一定数量的发布订阅连接是必须的
         */
        @NotNull
        private Integer subscriptionConnectionMinimumIdleSize = 1;

        /**
         * 从节点发布和订阅连接池大小
         * 默认值：50
         * 多从节点的环境里，每个 从服务节点里用于发布和订阅连接的连接池最大容量。连接池的连接数量自动弹性伸缩
         */
        @NotNull
        private Integer subscriptionConnectionPoolSize = 50;

        /**
         * 最小空闲连接数
         * 默认值：32
         * 最小保持连接数（长连接）。长期保持一定数量的连接有利于提高瞬时写入反应速度。
         */
        @NotNull
        private Integer connectionMinimumIdleSize = 10;

        /**
         * 连接池大小
         * 默认值：64
         * 连接池最大容量。连接池的连接数量自动弹性伸缩
         */
        @NotNull
        private Integer connectionPoolSize = 50;

        /**
         * 数据库编号，默认值：11
         */
        @NotNull
        private Integer database = 11;

        /****************************** 需要设置的配置项 ******************************/

        /**
         * redis链接地址
         */
        @NotEmpty
        private String address;

        /**
         * redis密码
         */
        private String password;
    }
}

