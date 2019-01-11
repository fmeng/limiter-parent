package me.fmeng.limiter;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import me.fmeng.limiter.exception.LimiterExceptionSupport;
import org.apache.commons.collections.CollectionUtils;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.context.WebApplicationContext;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * @author fmeng
 * @since 2019/01/09
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("local")
public abstract class AbstractTest {

    @Autowired
    protected WebApplicationContext webApplicationContext;

    /**
     * 并发数
     */
    private static final int REQUEST_MAX_TIMES = 3;

    /**
     * 主线程等待时间
     */
    private static final int MAIN_THREAD_WAIT_MS = 3 * 1000;

    /**
     * 并行调用模版方法
     */
    protected void parallelLookInvoke(Runnable runnable, String bizDescription) {
        ExecutorService threadPool = Executors.newFixedThreadPool(REQUEST_MAX_TIMES);
        Set<Long> execTimeSet = Sets.newConcurrentHashSet();
        int i = 0;
        long utilTimeMillis = System.currentTimeMillis() + MAIN_THREAD_WAIT_MS - 100;
        do {
            threadPool.execute(() -> {
                do {
                    try {
                        runnable.run();
                        execTimeSet.add(System.currentTimeMillis());
                    } catch (Exception e) {
                        if (!(e instanceof LimiterExceptionSupport)
                                && !(e.getCause() instanceof LimiterExceptionSupport)) {
                            e.printStackTrace();
                        }
                    }
                    try {
                        Thread.sleep(30);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } while (System.currentTimeMillis() < utilTimeMillis);
            });
            i++;
        } while (i < REQUEST_MAX_TIMES);
        try {
            Thread.sleep(MAIN_THREAD_WAIT_MS);
        } catch (InterruptedException e) {
            // e.printStackTrace();
        }
        printResult(execTimeSet, bizDescription);
    }

    /**
     * 打印统计结果
     */
    private void printResult(Set<Long> execTimeSet, String bizDescription) {
        // 打印执行时间
        List<Long> sortList = execTimeSet.stream()
                .sorted(Collections.reverseOrder(Long::compare))
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(sortList)) {
            return;
        }
        // 等差
        List<Long> diffList = Lists.newArrayListWithCapacity(execTimeSet.size());
        int i = 0;
        int j = 1;
        do {
            diffList.add(sortList.get(i) - sortList.get(j));
            i++;
            j = i + 1;
        } while (j < sortList.size());

        String diffNums = diffList.stream().map(Object::toString)
                .collect(Collectors.joining(","));
        Long maxTime = sortList.stream().max(Long::compare).orElse(0L);
        Long minTime = sortList.stream().min(Long::compare).orElse(0L);
        double diffAvg = diffList.stream().mapToLong(o -> o).average().orElse(0.0);
        log.info("输出结果, bizDescription={}, totalTime={}, size={}, diffAvg={}, diffNums={}, sortList={}"
                , bizDescription, maxTime - minTime, sortList.size(), diffAvg, diffNums, sortList);
    }
}