package com.mobei.concurrent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author liuyaowu
 * @date 2022/10/9 21:58
 * @remark
 */
public class CachedThreadPoolDemo {

    public static void main(String[] args) {
        /**
         * 不限制线程数量,无论提交多少个线程都会直接开辟创建一个新的线程来执行这个个任务,
         * 适合短时间内突然涌入大量任务的场景,大量线程之后空闲了没有任务了,达到一定时间后就自动释放掉
         */
        ExecutorService pool = Executors.newCachedThreadPool();
        for (int i = 0; i < 10; i++) {
            pool.execute(() -> {
                try {
                    System.out.println("fixedThreadPool execute..." + Thread.currentThread());
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
        pool.shutdown();
    }

}
