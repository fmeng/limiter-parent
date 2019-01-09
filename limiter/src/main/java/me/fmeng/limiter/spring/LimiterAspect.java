package me.fmeng.limiter.spring;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import me.fmeng.limiter.annotation.Limiter;
import me.fmeng.limiter.configure.bean.LimiterItemProperties;
import me.fmeng.limiter.constant.LimiterConstant;
import me.fmeng.limiter.infrastructure.hitter.ResourceBO;
import me.fmeng.limiter.infrastructure.hitter.ResourceBoHolder;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;

import java.util.Objects;
import java.util.Set;

/**
 * 处理注解限流器
 *
 * @author fmeng
 * @since 2018/07/24
 */
@Slf4j
@Aspect
@Order(LimiterConstant.LIMITER_ASPECT_ORDER)
public class LimiterAspect extends LimiterDriveSupport {

    @Around(value = "@annotation(limiter)")
    public Object limit(ProceedingJoinPoint pjp, Limiter limiter) throws Throwable {
        // 全局限流开关没有启用
        if (!limiterProperties.getEnable()) {
            return pjp.proceed();
        }
        /****************************** 基础变量、校验合法性 ******************************/
        String methodName = pjp.getTarget().getClass().getName() + "#" + pjp.getSignature().getName();
        String itemName = limiter.value();
        Preconditions.checkState(StringUtils.isNotBlank(itemName), "limiter需要根据名字指定配置项");
        LimiterItemProperties item = nameItemMap.get(itemName);
        Preconditions.checkState(Objects.nonNull(item), "无法根据limiter指定的名字获得配置项");

        /****************************** 没有命中限流策略 ******************************/
        // 限流项没有启用
        if (!item.getEnable()) {
            return pjp.proceed();
        }
        ResourceBO resourceBO = ResourceBoHolder.get();
        if (resourceBO == null) {
            resourceBO = new ResourceBO();
        }
        // 填充额外的资源信息
        resourceBO = fillResourceBO(resourceBO);
        Set<String> limiterNames = resourceBO.getAnnotationLimiterNames();
        if (CollectionUtils.isEmpty(limiterNames)) {
            resourceBO.setAnnotationLimiterNames(Sets.newHashSet(itemName));
        } else {
            limiterNames.add(itemName);
            resourceBO.setAnnotationLimiterNames(limiterNames);
        }

        /****************************** 限流逻辑 ******************************/
        long passMilliseconds = tryToPassAllLimiter(resourceBO);
        if (log.isDebugEnabled()) {
            log.debug("限流器切面, 限流后的执行时间, currentTimeMillis={}, passMilliseconds={}, itemName={} methodName={}", System.currentTimeMillis(), passMilliseconds, itemName, methodName);
        }
        return pjp.proceed();
    }

}
