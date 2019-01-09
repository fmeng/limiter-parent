package me.fmeng.limiter.infrastructure.hitter;

import lombok.Data;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * 业务请求参数
 *
 * @author fmeng
 * @since 2018/07/27
 */
@Data
public class ResourceBO {

    /**
     * 请求方法
     */
    private RequestMethod requestMethod;

    /**
     * 被匹配的内容没有host
     */
    private String requestUrl;

    /**
     * 其他的业务参数包装到map中
     */
    private Map<String, String[]> parameterMap;

    /**
     * 注解描述的限流器的名字
     */
    private Set<String> annotationLimiterNames = Collections.emptySet();
}
