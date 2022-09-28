package com.mobei.concurrent;

import java.util.concurrent.Semaphore;

/**
 * @author liuyaowu
 * @date 2022/9/29 7:29
 * @remark
 */
public class SemaphoreDemo {

    public static void main(String[] args) throws Exception {
        Semaphore semaphore = new Semaphore(0);

        new Thread(() -> {
            try {
                Thread.sleep(2000);
                System.out.println("线程1执行一个计算任务");
                semaphore.release();
            } catch (Exception e) {

            }
        }).start();

        new Thread(() -> {
            try {
                Thread.sleep(1000);
                System.out.println("线程2执行一个计算任务");
                semaphore.release();
            } catch (Exception e) {

            }
        }).start();

        semaphore.acquire(1);
        System.out.println("等待一个线程完成任务即可");
    }

}
