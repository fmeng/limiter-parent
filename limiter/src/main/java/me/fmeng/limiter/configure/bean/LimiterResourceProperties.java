package me.fmeng.limiter.configure.bean;

import lombok.Data;
import me.fmeng.limiter.constant.LimiterConstant;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * 被限流的资源
 *
 * @author fmeng
 * @since 2018/07/24
 */
@Data
@ConfigurationProperties(prefix = LimiterConstant.LIMITER_KEY_PREFIX + ".item" + ".resource")
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
     * 其他的业务参数包装到map中
     */
    private Map<String, List<String>> paramResources;
}
