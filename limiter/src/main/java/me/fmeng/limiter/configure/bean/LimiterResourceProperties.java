package me.fmeng.limiter.configure.bean;

import lombok.Data;
import me.fmeng.limiter.constant.LimiterConstant;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 被限流的资源
 *
 * @author fmeng
 * @since 2018/07/24
 */
@Data
@ConfigurationProperties(prefix = LimiterConstant.LIMITER_KEY_PREFIX + ".items" + ".resource")
public class LimiterResourceProperties {

    /**
     * 请求方法
     */
    private List<RequestMethod> requestMethods;

    /**
     * 被匹配的内容没有host
     */
    @NotBlank
    private String pathRegex;

    /**
     * 被限流的资源，取反向逻辑
     */
    @NotNull
    private Boolean reverse = Boolean.FALSE;

    /**
     * 请求参数
     */
    private List<ParamProperties> params;

    /**
     * 请求参数
     */
    @Data
    @ConfigurationProperties(prefix = LimiterConstant.LIMITER_KEY_PREFIX + ".items" + ".resource" + ".params")
    public static class ParamProperties {
        /**
         * true:根据请求参数动态生成
         */
        private Boolean dynamic = Boolean.FALSE;

        /**
         * 请求参数的名字
         */
        @NotBlank
        private String paramName;

        /**
         * 请求参数的值
         */
        private List<String> paramValues;
    }
}
