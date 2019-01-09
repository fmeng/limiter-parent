package me.fmeng.limiter.infrastructure.hitter;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import me.fmeng.limiter.Hitter;
import me.fmeng.limiter.configure.bean.LimiterItemProperties;
import me.fmeng.limiter.configure.bean.LimiterResourceProperties;
import me.fmeng.limiter.constant.LimiterStrategyTypeEnum;
import me.fmeng.limiter.util.SpringBeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * 限流器的通用实现
 *
 * @author fmeng
 * @since 2018/07/27
 */
@Slf4j
public class HitterAutoDelegate implements Hitter {

    /**
     * 判断是否命中限流策略
     *
     * @param requestBO 请求参数
     * @param item      配置项
     * @return 是否命中限流策略 true:命中限流策略
     */
    @Override
    public boolean hit(ResourceBO requestBO, LimiterItemProperties item) {
        Preconditions.checkArgument(Objects.nonNull(requestBO), "requestBO不能为空");
        Preconditions.checkArgument(Objects.nonNull(item), "item不能为空");
        Preconditions.checkArgument(Boolean.TRUE.equals(item.getEnable()), "item没有启用");
        if (LimiterStrategyTypeEnum.CUSTOM.equals(item.getLimiterStrategyType())) {
            // 委派的命中器
            if (delegateHit(requestBO, item)) {
                return true;
            }
        }
        if (LimiterStrategyTypeEnum.ANNOTATION.equals(item.getLimiterStrategyType())) {
            // 注解限流
            if (doAnnotationHit(requestBO, item)) {
                return true;
            }
        }
        if (LimiterStrategyTypeEnum.URL.equals(item.getLimiterStrategyType())) {
            // 请求限流
            if (doRequestHit(requestBO, item)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否命中限流策略（子类可以复写该方法, 实现自己的命中策略）
     *
     * @param requestBO 请求参数
     * @param item      配置项
     * @return 是否命中限流策略 true:命中限流策略
     */
    private boolean delegateHit(ResourceBO requestBO, LimiterItemProperties item) {
        Class<? extends Hitter> customHitterClass = item.getCustomHitterClass();
        Preconditions.checkNotNull(customHitterClass, "customHitterClass不能为空");
        Hitter hitter = SpringBeanUtils.getBean(customHitterClass);
        return hitter.hit(requestBO, item);
    }

    /**
     * 注解
     *
     * @param requestBO 请求的参数, 不可能为null
     * @param item      配置项, 不可能为null
     * @return 所有参数都匹配成功, true:匹配成功
     */
    private boolean doAnnotationHit(ResourceBO requestBO, LimiterItemProperties item) {
        Set<String> requestNames = requestBO.getAnnotationLimiterNames() == null ? Collections.emptySet() : requestBO.getAnnotationLimiterNames();
        return requestNames.contains(item.getName());
    }

    /**
     * 请求
     *
     * @param requestBO 请求的参数, 不可能为null
     * @param item      配置项, 不可能为null
     * @return 所有参数都匹配成功, true:匹配成功
     */
    private boolean doRequestHit(ResourceBO requestBO, LimiterItemProperties item) {
        boolean hit = match(requestBO, item.getResource());
        if (item.getResource().getReverse()) {
            return !hit;
        }
        return hit;
    }

    /**
     * 判读是否成功匹配所有的资源参数
     *
     * @param requestBO        请求的参数, 不可能为null
     * @param tryToHitResource 配置的参数, 不可能为null
     * @return 所有参数都匹配成功, true:匹配成功
     */
    private boolean match(ResourceBO requestBO, LimiterResourceProperties tryToHitResource) {
        // 请求方法
        if (!tryToHitResource.getRequestMethods().contains(requestBO.getRequestMethod())) {
            return false;
        }
        // 请求url
        String requestUrl = requestBO.getRequestUrl() == null ? "" : requestBO.getRequestUrl();
        if (!Pattern.matches(tryToHitResource.getPathRegex(), requestUrl)) {
            return false;
        }
        // 业务参数
        Map<String, String[]> param = requestBO.getParameterMap();
        Map<String, List<String>> hitsParam = tryToHitResource.getParamResources();
        // 配置的参数为空，需要匹配请求的参数
        if (MapUtils.isEmpty(hitsParam)) {
            return true;
        }
        // 下面为配置参数非空的情况
        // 请求参数为空，未匹配到限流器
        if (MapUtils.isEmpty(param)) {
            return false;
        }
        // 请求参数和配置参数都非空的情况
        if (matchParam(requestBO, hitsParam)) {
            return true;
        }
        // 其他情况，没有匹配到限流器
        return false;
    }

    /**
     * 判断所有非空参数是否匹配成功
     *
     * @param requestBO 请求的参数, 不可能为null或空集合
     * @param hitsParam 配置的参数, 不可能为null或空集合
     * @return 所有参数都匹配成功, true:匹配成功
     */
    private boolean matchParam(ResourceBO requestBO, Map<String, List<String>> hitsParam) {
        // 请求参数
        Map<String, String[]> param = requestBO.getParameterMap();
        Set<String> paramKeys = param.keySet();
        Set<String> hitsKeys = hitsParam.keySet();
        if (paramKeys.containsAll(hitsKeys)) {
            boolean someKeyNotMatch = false;
            for (Map.Entry<String, List<String>> hitEntry : hitsParam.entrySet()) {
                // 请求参数
                String[] paramValues = param.get(hitEntry.getKey());
                // 配置参数
                List<String> hitValues = hitEntry.getValue();
                // 配置参数为空
                if (CollectionUtils.isEmpty(hitValues)) {
                    continue;
                }
                // 配置参数没有包含所有的请求属性
                Set<String> mergedPramValues = Sets.newHashSet(paramValues);
                // 请求参数为空，配置非空
                if (CollectionUtils.isEmpty(mergedPramValues)) {
                    someKeyNotMatch = true;
                    break;
                }
                if (!mergedPramValues.containsAll(hitsKeys)) {
                    someKeyNotMatch = true;
                    break;
                }
            }
            // 所有属性都匹配成功
            return !someKeyNotMatch;
        }
        return false;
    }
}
