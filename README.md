[![Travis Build Status](https://travis-ci.com/fmeng/limiter-parent.svg?branch=master)](https://travis-ci.com/fmeng/limiter-parent)
[![Coverage Status](https://coveralls.io/repos/github/fmeng/limiter-parent/badge.svg?branch=master)](https://coveralls.io/github/fmeng/limiter-parent?branch=master)
[![Maven Central](https://img.shields.io/maven-central/v/me.fmeng/anstore.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:me.fmeng%20AND%20a:limiter)
[![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)

# Introduction
Limiter 是一个面向具体业务的，轻量级限流器。
Limiter提供的核心能力包括：
   1. Spring Boot支持。自动化配置提供默认功能实现，也可根据业务自定义
   2. 身轻如燕。没有[Sentinel](https://github.com/alibaba/Sentinel)心宽体胖。使用[Guava Limiter](https://github.com/google/guava) 和 [Redisson RRateLimiter](https://github.com/redisson/redisson)实现核心限流逻辑。
   5. 简单的设计。使用`Hitter`(命中器)和`Limiter`(限流器)抽象资源命中过程和限流过程
   3. 配置完全抽离。可以拆分到配置中心，动态加载限流策略
   4. 灵活的配置。功能上，即支持静态配置指定资源，又支持动态匹配请求资源，几乎满足常见的业务限流；使用上，提供注解，URL匹配和自定义配置配限制的资源
   5. 多个限流项可重入。多个限流项同时限制访问频率。
# Quick Start
**源码中[limiter-test](https://github.com/fmeng/limiter-parent/tree/master/limiter-test)模块提供了相关Demo，可快速上手。**

### 1. 添加依赖

**Note:** Limiter需要JDK8以上版本, Spring boot 2.0.

添加maven依赖，其他构建环境参照maven配置
```xml
<dependency>
    <groupId>me.fmeng</groupId>
    <artifactId>limiter</artifactId>
    <version>x.y.z</version>
</dependency>
```

### 2. 配置
1. `application.yaml`配置
    ```yaml
    limiter:
      appId: "appId"
      enable: true
      allLimiterTimeoutMilliseconds: 60000
      items:
        - name: "guavaLimiter"
          permits: 1
          limiterStrategyType: ANNOTATION
    ```
2. 被限制访问方法配置
    ```java
    // 名字和配置文件中保持一致
    @Limiter("guavaLimiter")
    public void limiteMethod() {
        System.out.println("你可以访问");
    }
    ```
### 3. 验证限流结果
```java
// 使用单元测试验证限流效果
TicketServiceTest.guavaAnnotationServiceGuava10pTest
```
# 复杂点的例子
### 1. 业务配置
1. 限制指定用户ID的访问频率
    ```yaml
    resource:
      requestMethods: [GET, POST]
      pathRegex: ".*/test/url/.*"
      params:
      - paramName: "userId"
        paramValues: ["user111", "user222"]
    ```
2. 限制指定用户ID指定请求IP的访问频率
    ```yaml
    resource:
      requestMethods: [GET, POST]
      pathRegex: ".*/test/url/.*"
      params:
      - paramName: "userId"
        paramValues: ["user111", "user222"]
      - paramName: "requestIp"
        paramValues: ["127.0.0.1"]
    ```
3. 限制 除了 指定用户ID的访问频率
    ```yaml
    resource:
      requestMethods: [GET]
      pathRegex: ".*/test/url/.*"
      reverse: true
      params:
      - paramName: "userId"
        paramValues: ["user111", "user222"]
    ```
4. 根据请求动态构建限流,限制用户ID的访问频率
    ```yaml
    resource:
      requestMethods: [GET]
      pathRegex: ".*/test/url/.*"
      params:
      - paramName: "userId"
        dynamic: true
    ```
5. 黑白名单
    ```yaml
    # 访问频率设置为0
    permits: 0
    # 配置被限制的资源
    resource:
      ...
    ```
### 2. 自定义扩展，[请查看单元测试](https://github.com/fmeng/limiter-parent/tree/master/limiter-test)
1. 限流器的实现`me.fmeng.limiter.CustomLimiterFactory`
2. 在获取请求后,被限流前的自定义操作（添加属性,校验参数等）`me.fmeng.limiter.CustomLimiterInterceptor`
4. 重写前端统一返回视图和异常处理策略`me.fmeng.limiter.CustomLimiterTranslator`

# 书难尽言，请君指瑕
1. GitHub Issues
2. Contact me: mail@fmeng.me, fmeng123@gmail.com
