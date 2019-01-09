package me.fmeng.limiter.util;

import com.google.common.base.Preconditions;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * 操作Spring容器, 调用者要保证在Spring容器启动后调用
 *
 * @author fmeng
 * @since 2019/01/08
 */
public class SpringBeanUtils implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    /**
     * 注入context
     *
     * @param context 容器
     */
    @Override
    public void setApplicationContext(ApplicationContext context) {
        applicationContext = context;
    }

    /**
     * 手动设置容器
     *
     * @param context 容器
     */
    public static void setApplication(ApplicationContext context) {
        applicationContext = context;
    }

    /**
     * 得到上下文
     *
     * @return 容器
     */
    public static ApplicationContext getApplicationContext() {
        Preconditions.checkNotNull(applicationContext, "没有设置applicationContext");
        return applicationContext;
    }

    /**
     * 根据名称获取
     *
     * @param beanName 名字
     * @return bean
     */
    public static Object getBean(String beanName) {
        Preconditions.checkNotNull(applicationContext, "没有设置applicationContext");
        return applicationContext.getBean(beanName);
    }

    /**
     * 根据名称和类型
     *
     * @param beanName 名字
     * @param clz      类
     * @return bean
     */
    public static <T> T getBean(String beanName, Class<T> clz) {
        Preconditions.checkNotNull(applicationContext, "没有设置applicationContext");
        return applicationContext.getBean(beanName, clz);
    }

    /**
     * 根据类型获取bean
     *
     * @param clz 类
     * @return bean
     */
    public static <T> T getBean(Class<T> clz) {
        Preconditions.checkNotNull(applicationContext, "没有设置applicationContext");
        return applicationContext.getBean(clz);
    }


    /**
     * 根据类型获取beanName
     *
     * @param clz 类
     * @return 名字
     */
    public static String[] getBeanNamesForType(Class<?> clz) {
        Preconditions.checkNotNull(applicationContext, "没有设置applicationContext");
        return applicationContext.getBeanNamesForType(clz);
    }

    /**
     * 根据类型获取beanMap
     *
     * @param clz 类
     * @return <名字,Bean>
     */
    public static <T> Map<String, T> getBeansOfType(Class<T> clz) {
        Preconditions.checkNotNull(applicationContext, "没有设置applicationContext");
        return applicationContext.getBeansOfType(clz);
    }

    /**
     * 根据bean上的注解获取bean
     *
     * @param clz 类
     * @return <名字,Bean>
     */
    public static Map<String, Object> getBeansWithAnnotation(Class<? extends Annotation> clz) {
        Preconditions.checkNotNull(applicationContext, "没有设置applicationContext");
        return applicationContext.getBeansWithAnnotation(clz);
    }

    /**
     * 获取beanFactory
     *
     * @return 容器
     */
    public static DefaultListableBeanFactory getBeanFactory() {
        Preconditions.checkNotNull(applicationContext, "没有设置applicationContext");
        return (DefaultListableBeanFactory) applicationContext.getAutowireCapableBeanFactory();
    }

    /**
     * 销毁bean
     *
     * @param beanName 名字
     * @return true:销毁成功
     */
    public static boolean destroy(String beanName) {
        Preconditions.checkNotNull(applicationContext, "没有设置applicationContext");
        DefaultListableBeanFactory beanFactory = getBeanFactory();
        if (!beanFactory.containsBean(beanName)) {
            return false;
        }
        beanFactory.destroySingleton(beanName);
        beanFactory.destroyBean(beanName);
        beanFactory.removeBeanDefinition(beanName);
        return true;
    }

    /**
     * 动态注册bean
     *
     * @param beanName 名字
     * @param clz      类
     * @param <T>      类型
     * @return bean
     */
    public static <T> T regist(String beanName, Class<T> clz) {
        Preconditions.checkNotNull(applicationContext, "没有设置applicationContext");
        DefaultListableBeanFactory beanFactory = getBeanFactory();
        if (!beanFactory.containsBean(beanName)) {
            BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.rootBeanDefinition(clz);
            beanFactory.registerBeanDefinition(beanName, beanDefinitionBuilder.getBeanDefinition());
        }
        return getBean(beanName, clz);
    }

    /**
     * 动态注册bean
     *
     * @param beanName              名字
     * @param beanDefinitionBuilder bean构建信息
     * @return bean
     */
    public static Object regist(String beanName, BeanDefinitionBuilder beanDefinitionBuilder) {
        Preconditions.checkNotNull(applicationContext, "没有设置applicationContext");
        DefaultListableBeanFactory beanFactory = getBeanFactory();
        if (!beanFactory.containsBean(beanName)) {
            beanFactory.registerBeanDefinition(beanName, beanDefinitionBuilder.getBeanDefinition());
        }
        return getBean(beanName);
    }
}
