package me.fmeng.limiter;

import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static java.lang.System.out;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author fmeng
 * @since 2019/01/09
 */
public class TicketControllerTest extends AbstractTest {


    @Test
    public void guavaInterceptorTest() {
        parallelLookInvoke(() -> doRequest("/ticket/guavaList?userId=user333"), "Guava拦截器");
    }

    @Test
    public void redisInterceptorTest() {
        parallelLookInvoke(() -> doRequest("/ticket/redisList?userId=user333"), "Redis拦截器");
    }

    private void doRequest(String url) {
        try {
            MockHttpServletRequestBuilder requestBuilder = get(url).servletPath("");
            MockMvcBuilders.webAppContextSetup(webApplicationContext)
                    //.alwaysDo(print())
                    .alwaysExpect(status().isOk())
                    .alwaysExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
                    .build()
                    .perform(requestBuilder)
                    .andExpect(result -> {
                        String contentAsString = result.getResponse().getContentAsString();
                        out.println("url:" + url + ",result:" + contentAsString);
                        if (!contentAsString.contains("正常请求")) {
                            throw new RuntimeException();
                        }
                    });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
