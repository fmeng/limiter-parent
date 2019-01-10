package me.fmeng.limiter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * @author fmeng
 * @since 2018/03/17
 */
@AutoConfigureWebMvc
@EnableAspectJAutoProxy(proxyTargetClass = true)
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class LimiterApplication {
    public static void main(String[] args) {
        SpringApplication.run(LimiterApplication.class, args);
    }
}
