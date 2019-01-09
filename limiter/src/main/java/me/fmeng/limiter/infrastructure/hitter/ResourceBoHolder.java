package me.fmeng.limiter.infrastructure.hitter;

import com.google.common.base.Preconditions;
import org.springframework.core.NamedThreadLocal;

import java.util.Objects;

/**
 * 线程安全的ResourceBO持有器
 *
 * @author fmeng
 * @since 2018/07/27
 */
public class ResourceBoHolder {

    private static final ThreadLocal<ResourceBO> RESOURCE_BO_THREAD_LOCAL = new NamedThreadLocal<>("资源信息Holder");

    /**
     * 设置resourceBO到当前线程中
     *
     * @param resourceBO 标示到资源对象
     */
    public static void set(ResourceBO resourceBO) {
        Preconditions.checkNotNull(resourceBO);
        RESOURCE_BO_THREAD_LOCAL.set(resourceBO);
    }

    /**
     * 从当前线程中获取resourceBO
     *
     * @return 标示到资源对象
     */
    public static ResourceBO get() {
        return RESOURCE_BO_THREAD_LOCAL.get();
    }

    /**
     * 从当前线程中移除resourceBO
     */
    public static void remove() {
        if (Objects.nonNull(RESOURCE_BO_THREAD_LOCAL.get())) {
            RESOURCE_BO_THREAD_LOCAL.remove();
        }
    }
}
