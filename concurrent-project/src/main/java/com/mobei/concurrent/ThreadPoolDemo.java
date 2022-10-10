package com.mobei.concurrent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author liuyaowu
 * @date 2022/10/9 21:58
 * @remark
 */
public class ThreadPoolDemo {

    public static void main(String[] args) {
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(10);;
        for (int i = 0; i < 10; i++) {
            fixedThreadPool.execute(() -> {
                try {
                    System.out.println("fixedThreadPool execute..." + Thread.currentThread());
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
        fixedThreadPool.shutdown();
    }

}
