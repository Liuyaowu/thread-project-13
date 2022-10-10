package com.mobei.concurrent;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * @author liuyaowu
 * @date 2022/10/10 21:32
 * @remark
 */
public class ScheduledThreadPoolDemo {
    public static void main(String[] args) {
        ScheduledExecutorService pool = Executors.newScheduledThreadPool(10);
        pool.schedule(() -> System.out.println("延迟5秒执行的任务"), 5, SECONDS);

        pool.scheduleWithFixedDelay(() -> System.out.println("延迟5秒执行的任务"), 5, 3, SECONDS);
    }
}
