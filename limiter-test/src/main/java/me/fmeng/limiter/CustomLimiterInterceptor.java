package me.fmeng.limiter;

import com.google.common.collect.ImmutableMap;
import me.fmeng.limiter.infrastructure.hitter.ResourceBO;
import me.fmeng.limiter.spring.mvc.LimiterInterceptor;
import org.apache.commons.collections.MapUtils;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author fmeng
 * @since 2019/01/10
 */
public class CustomLimiterInterceptor extends LimiterInterceptor {

    /**
     * 填充资源信息
     *
     * @param resourceBO
     * @return 资源信息
     */
    @Override
    protected ResourceBO fillResourceBO(ResourceBO resourceBO) {
        fillIpInfo(resourceBO);
        return resourceBO;
    }

    private void fillIpInfo(ResourceBO resourceBO) {
        if (MapUtils.isEmpty(resourceBO.getParameterMap())) {
            return;
        }
        Map<String, String[]> tempParamMap = resourceBO.getParameterMap().entrySet().parallelStream()
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        tempParamMap.put("ip", new String[]{"127.0.0.1"});
        resourceBO.setParameterMap(ImmutableMap.copyOf(tempParamMap));
    }
}
