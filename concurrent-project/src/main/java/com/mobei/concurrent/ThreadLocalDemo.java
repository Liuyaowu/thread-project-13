package com.mobei.concurrent;

/**
 * @author liuyaowu
 * @date 2022/9/25 20:07
 * @remark
 */
public class ThreadLocalDemo {

    public static void main(String[] args) {
        ThreadLocal<String> threadLocal = new ThreadLocal<>();

        Thread t1 = new Thread(() -> {
            threadLocal.set("thread-1");
            System.out.println("线程1获取ThreadLocal的值: " + threadLocal.get());
        });

        Thread t2 = new Thread(() -> {
            threadLocal.set("thread-2");
            System.out.println("线程2获取ThreadLocal的值: " + threadLocal.get());
        });

        t1.start();
        t2.start();

        threadLocal.set("thread-main");
        System.out.println("主线程获取ThreadLocal的值: " + threadLocal.get());
    }

}
