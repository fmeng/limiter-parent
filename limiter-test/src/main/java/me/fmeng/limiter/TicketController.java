package me.fmeng.limiter;

import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author fmeng
 * @since 2019/01/09
 */
@Slf4j
@Controller
@ResponseBody
@RequestMapping("/ticket")
public class TicketController {

    @GetMapping("/guavaList")
    public Object list(String userId, String startCity, String endCity) {
        return ImmutableMap.of("正常请求", "list正常请求");
    }

    @GetMapping("/redisList")
    public Object redisList(String userId, String startCity, String endCity) {
        return ImmutableMap.of("正常请求", "list正常请求");
    }
}
