package me.fmeng.limiter.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Json序列化，基于Jackson实现
 *
 * @author fmeng
 * @since 2019/01/09
 */
public class JsonUtils {

    /**
     * json序列化
     */
    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * 安全转换成Json,吞没异常,用于非敏感业务
     * 例如:日志打印
     */
    public static String objectToJsonQuietly(Object o) {
        try {
            return MAPPER.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return "";
    }
}
