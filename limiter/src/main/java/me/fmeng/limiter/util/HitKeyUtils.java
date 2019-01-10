package me.fmeng.limiter.util;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import me.fmeng.limiter.Hitter;
import me.fmeng.limiter.configure.bean.LimiterItemProperties;
import me.fmeng.limiter.configure.bean.LimiterResourceProperties;
import me.fmeng.limiter.constant.LimiterConstant;
import me.fmeng.limiter.exception.LimiterConfigException;
import me.fmeng.limiter.exception.RequestParamException;
import me.fmeng.limiter.infrastructure.hitter.ResourceBO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 生成限流器的key
 *
 * @author fmeng
 * @since 2018/07/27
 */
@Slf4j
public class HitKeyUtils {

    private HitKeyUtils() {
    }

    /**
     * 生成限流器的key
     *
     * @param appId                 前缀
     * @param limiterItemProperties 限流项
     * @return key
     */
    public static String generateKey(String appId, ResourceBO resourceBO, LimiterItemProperties limiterItemProperties) {
        List<String> resList = Lists.newArrayListWithCapacity(10);
        fillAppInfo(resList, LimiterConstant.LIMITER_KEY_PREFIX, appId);
        fillConfigInfo(resList, limiterItemProperties);
        if (limiterItemProperties.getResource() != null) {
            fillMergedRequestInfo(resList, limiterItemProperties.getResource(), resourceBO);
        }
        return resList.stream().filter(StringUtils::isNotBlank).collect(Collectors.joining(LimiterConstant.LIMITER_KEY_SEPARATOR));
    }


    /**
     * 判断是否命中配置指定的限流项
     *
     * @param requestBO 请求资源信息
     * @param item      限流配置
     * @return true:命中
     */
    public static boolean match(ResourceBO requestBO, LimiterItemProperties item) {
        switch (item.getLimiterStrategyType()) {
            case CUSTOM:
                // 委派的命中器
                Class<? extends Hitter> customHitterClass = item.getCustomHitterClass();
                Preconditions.checkNotNull(customHitterClass, "customHitterClass不能为空");
                Hitter hitter = SpringBeanUtils.getBean(customHitterClass);
                return hitter.hit(requestBO, item);
            case ANNOTATION:
                // 注解限流
                Set<String> requestNames = requestBO.getAnnotationLimiterNames() == null ? Collections.emptySet() : requestBO.getAnnotationLimiterNames();
                boolean hit = requestNames.contains(item.getName());
                if (item.getResource() != null && item.getResource().getReverse()) {
                    return !hit;
                }
                return hit;
            case URL:
                // 请求限流
                return requestHit(requestBO, item);
            default:
                // 安全控制
                throw new RuntimeException("不支持的限流类型");
        }
    }

    /**
     * 请求
     *
     * @param requestBO 请求的参数, 不可能为null
     * @param item      配置项, 不可能为null
     * @return 所有参数都匹配成功, true:匹配成功
     */
    private static boolean requestHit(ResourceBO requestBO, LimiterItemProperties item) {
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
    private static boolean match(ResourceBO requestBO, LimiterResourceProperties tryToHitResource) {
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
        Map<String, String[]> requestParam = requestBO.getParameterMap() == null ? Collections.emptyMap() : requestBO.getParameterMap();
        List<LimiterResourceProperties.ParamProperties> params = tryToHitResource.getParams() == null ? Collections.emptyList() : tryToHitResource.getParams();
        for (LimiterResourceProperties.ParamProperties configParam : params) {
            String[] requestValues = requestParam.get(configParam.getParamName());
            if (ArrayUtils.isEmpty(requestValues) || requestValues.length > 1) {
                log.error("匹配限流器,请求参数不合法,没有找到参数名或匹配到多个值, key={}, values={}", configParam.getParamName(), requestValues);
                throw new RequestParamException("请求参数不合法");
            }
            if (!configParam.getDynamic()) {
                if (CollectionUtils.isEmpty(configParam.getParamValues())) {
                    log.error("匹配限流器,限流器配置信息异常,匹配到的参数不能为空, param={}", configParam);
                    throw new LimiterConfigException("限流配置错误");
                }
                boolean configContainsRequest = Sets.newHashSet(configParam.getParamValues()).contains(requestValues[0]);
                if (!configContainsRequest) {
                    return false;
                }
            }
        }
        // 限流项匹配成功
        return true;
    }

    /**
     * 填充应用信息
     *
     * @param resList   被填充的容器
     * @param keyPrefix 限流器前缀
     * @param appId     应用ID
     */
    private static void fillAppInfo(List<String> resList, String keyPrefix, String appId) {
        resList.add(keyPrefix);
        resList.add(appId);
    }

    /**
     * 填充配置信息
     *
     * @param resList    被填充的容器
     * @param configInfo 配置信息
     */
    private static void fillConfigInfo(List<String> resList, LimiterItemProperties configInfo) {
        resList.add(configInfo.getName());
        resList.add(configInfo.getPermits().toString());
        resList.add(configInfo.getRateInterval().toString());
        resList.add(configInfo.getTimeUnit().toString());
        resList.add(DigestUtils.md5DigestAsHex(configInfo.toString().getBytes(Charsets.UTF_8)));
    }

    /**
     * 填充动态合并的信息
     *
     * @param resList    被填充的容器
     * @param configInfo 配置信息
     * @param resourceBO 请求信息
     */
    private static void fillMergedRequestInfo(List<String> resList, LimiterResourceProperties configInfo, ResourceBO resourceBO) {
        // 请求信息
        Map<String, String[]> requestParameters = resourceBO.getParameterMap() == null ? Collections.emptyMap() : resourceBO.getParameterMap();
        // 配置信息
        List<LimiterResourceProperties.ParamProperties> params = configInfo.getParams() == null ? Collections.emptyList() : configInfo.getParams();
        // 合并信息
        for (LimiterResourceProperties.ParamProperties param : params) {
            resList.add(param.getParamName());
            resList.add(param.getDynamic().toString());
            if (param.getDynamic()) {
                // 动态构造
                String[] requestValues = requestParameters.get(param.getParamName());
                if (ArrayUtils.isEmpty(requestValues) || requestValues.length > 1) {
                    log.error("生成hitKey,请求参数不合法,没有找到参数名或匹配到多个值, key={}, values={}", param.getParamName(), requestValues);
                    throw new RequestParamException("请求参数不合法");
                }
                resList.add(requestValues[0]);
            } else {
                // 配置信息
                if (CollectionUtils.isEmpty(param.getParamValues())) {
                    log.error("生成hitKey,限流器配置信息异常,匹配到的参数不能为空, param={}", param);
                    throw new LimiterConfigException("限流配置错误");
                }
                resList.add(DigestUtils.md5DigestAsHex(param.getParamValues().toString().getBytes(Charsets.UTF_8)));
            }
        }
    }
}
