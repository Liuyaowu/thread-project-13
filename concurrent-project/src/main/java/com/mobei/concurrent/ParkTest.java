package com.mobei.concurrent;

import java.util.concurrent.locks.LockSupport;

/**
 * @author liuyaowu
 * @date 2022-09-23 16:31
 * @description
 */
public class ParkTest {
    public static void main(String[] args) throws Exception {
        Object o = new Object();
        Thread thread = new Thread(() -> {
            try {
                for (int i = 0; i < 50; i++) {
                    if (i == 20) {
                        System.out.println("park之前线程是否被中断: " + Thread.interrupted());
                        LockSupport.park(o);
                        System.out.println("park之后线程是否被中断: " + Thread.interrupted());
                        System.out.println("调用interrupted方法后线程是否被中断 : " + Thread.interrupted());
                        Thread.currentThread().interrupt();
                        System.out.println("调用interrupt方法后线程是否被中断 : " + Thread.interrupted());
                    }
                    System.out.println(i);
                }
            } finally {
                System.out.println("执行finally");
            }
        });
        thread.start();

        System.out.println("主线程开始等待");
        Thread.sleep(5000);
        System.out.println("主线程等待结束");
//        Thread.currentThread().interrupt();
//        LockSupport.unpark(thread);
        thread.interrupt();
//        Thread.currentThread().notifyAll();
//        thread.notify();
    }
}
