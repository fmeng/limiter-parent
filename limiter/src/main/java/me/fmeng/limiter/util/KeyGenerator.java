package me.fmeng.limiter.util;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import me.fmeng.limiter.configure.bean.LimiterItemProperties;
import me.fmeng.limiter.configure.bean.LimiterResourceProperties;
import me.fmeng.limiter.constant.LimiterConstant;
import me.fmeng.limiter.infrastructure.hitter.ResourceBO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * 生成限流器的key
 *
 * @author fmeng
 * @since 2018/07/27
 */
@Slf4j
public class KeyGenerator {

    private static final Joiner KEY_JOINER = Joiner.on(LimiterConstant.LIMITER_KEY_SEPARATOR);

    private KeyGenerator() {
    }


    /**
     * 生成限流器的key
     *
     * @param appId                 前缀
     * @param limiterItemProperties 限流项
     * @return key
     */
    public static String generate(String appId, ResourceBO resourceBO, LimiterItemProperties limiterItemProperties) {
        return KEY_JOINER.join(LimiterConstant.LIMITER_KEY_PREFIX
                , appId
                , limiterItemProperties.getPermits()
                , limiterItemProperties.getTimeUnit()
                , mergeValues(limiterItemProperties)
                , mergeValues(resourceBO, limiterItemProperties.getResource()));

    }

    /**
     * 合并限流项维度的属性
     *
     * @param limiterItemProperties 限流项
     * @return key item
     */
    private static String mergeValues(LimiterItemProperties limiterItemProperties) {
        return KEY_JOINER.join(limiterItemProperties.getName()
                , StringUtils.uncapitalize(ClassUtils.getSimpleName(limiterItemProperties.getLimiterFactoryClass())));
    }

    /**
     * 合并资源维度的属性
     *
     * @param resourceProperties 资源描述
     * @return key item
     */
    private static String mergeValues(ResourceBO resourceBO, LimiterResourceProperties resourceProperties) {
        if (Objects.isNull(resourceBO) || Objects.isNull(resourceProperties)) {
            return "";
        }
        // 请求方法
        String requestMethodStr = "";
        if (CollectionUtils.isNotEmpty(resourceProperties.getRequestMethods())) {
            requestMethodStr = KEY_JOINER.join(resourceProperties.getRequestMethods());
        }
        // 请求参数
        Map<String, String[]> requestParameterMap = resourceBO.getParameterMap();
        // 参数
        List<String> params = Lists.newArrayList();
        if (MapUtils.isNotEmpty(resourceProperties.getParamResources())) {
            for (Map.Entry<String, List<String>> entry : resourceProperties.getParamResources().entrySet()) {
                // 配置参数名
                String key = entry.getKey();
                // 配置参数值
                List<String> configValues = entry.getValue();
                // 请求参数
                String[] requestValues = requestParameterMap.get(key);
                if (StringUtils.isBlank(key)) {
                    continue;
                }
                params.add(key);
                // 配置参数非空
                if (CollectionUtils.isNotEmpty(configValues)) {
                    params.addAll(configValues);
                    continue;
                }
                // 请求参数非空
                if (ArrayUtils.isNotEmpty(requestValues)) {
                    params.addAll(Lists.newArrayList(requestValues));
                    continue;
                }
                // 配置参数为空、请求参数为空、用户属性为空
                log.error("限流器配置异常, 配置参数为空、请求参数为空、用户属性为空, resourceBO={}, resourceProperties={}", resourceBO, resourceProperties);
                throw new RuntimeException("限流器配置异常");
            }
        }
        List<String> res = Lists.newArrayList();
        Optional.of(requestMethodStr).filter(StringUtils::isNotBlank).ifPresent(res::add);
        Optional.ofNullable(resourceProperties.getPathRegex()).filter(StringUtils::isNotBlank).ifPresent(res::add);
        Optional.of(resourceProperties.getReverse()).ifPresent(reverse -> res.add(reverse.toString()));
        Optional.of(params).filter(CollectionUtils::isNotEmpty).ifPresent(res::addAll);
        return KEY_JOINER.join(res);

    }
}
