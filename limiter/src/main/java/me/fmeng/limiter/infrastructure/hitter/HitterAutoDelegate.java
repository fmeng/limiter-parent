package me.fmeng.limiter.infrastructure.hitter;

import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import me.fmeng.limiter.Hitter;
import me.fmeng.limiter.configure.bean.LimiterItemProperties;
import me.fmeng.limiter.util.HitKeyUtils;

import java.util.Objects;

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
        return HitKeyUtils.match(requestBO, item);
    }
}
