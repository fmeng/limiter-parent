package me.fmeng.limiter.configure.bean;

import com.google.common.base.Preconditions;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import me.fmeng.limiter.constant.LimiterConstant;
import me.fmeng.limiter.constant.LimiterStrategyTypeEnum;
import me.fmeng.limiter.util.JsonUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.redisson.client.codec.Codec;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.annotation.PostConstruct;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;

/**
 * 限流器配置, 每一个Item会创建一个限流器
 *
 * @author fmeng
 * @since 2018/07/27
 */
@Data
@Slf4j
@Validated
public class LimiterProperties {

    /**
     * 是否启用
     */
    @NotNull
    private Boolean enable = Boolean.TRUE;

    /**
     * 应用ID
     */
    @NotBlank
    private String appId;

    /**
     * 所有的限流器不能超时的总时间(毫秒)
     */
    @NotNull
    private Long allLimiterTimeoutMilliseconds;

    /**
     * 限流提示信息
     */
    @NotBlank
    private String exceptionMessage = "服务器繁忙,请稍后再试";

    /**
     * 该节点使用redisson的配置
     */
    @Valid
    @NestedConfigurationProperty
    private RedissonProperties redisson;

    /**
     * 限流配置项目
     */
    @Valid
    @NotEmpty
    private List<LimiterItemProperties> items;

    @PostConstruct
    public void init() throws Exception {
        checkArgument();
        initCodecInstance();
        printLog();
    }

    private void initCodecInstance() {
        if (this.redisson != null) {
            Class<? extends Codec> codec = this.redisson.getCodec();
            Preconditions.checkNotNull(codec, "codec不能为空");
            this.redisson.setCodec(codec);
        }
    }

    private void checkArgument() {
        if (CollectionUtils.isNotEmpty(items)) {
            for (LimiterItemProperties item : items) {
                Preconditions.checkState(Objects.nonNull(item.getPermits()), "permits不能为空");
                Preconditions.checkState(Objects.nonNull(item.getTimeUnit()), "timeUnit不能为空");
                LimiterStrategyTypeEnum strategyType = item.getLimiterStrategyType();
                if (LimiterStrategyTypeEnum.URL.equals(strategyType)) {
                    LimiterResourceProperties resource = item.getResource();
                    Preconditions.checkState(Objects.nonNull(resource), "url匹配策略, resource不能为空");
                    Preconditions.checkState(StringUtils.isNotBlank(resource.getPathRegex()), "url匹配策略, pathRegex不能为空");
                    Preconditions.checkState(CollectionUtils.isNotEmpty(resource.getRequestMethods()), "url匹配策略, 请求方法不能为空");
                }
            }
        }
    }

    /**
     * 日志打印
     */
    private volatile transient boolean logged = false;

    private void printLog() throws Exception {
        if (logged) {
            return;
        }
        log.info("****************************** 😊开始,输出限流器配置😊 ******************************");
        log.info(JsonUtils.objectToJsonQuietly(this));
        log.info("****************************** 😊结束,输出限流器配置😊 ******************************");
        logged = true;
    }
}
