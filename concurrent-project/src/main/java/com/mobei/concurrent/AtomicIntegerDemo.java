package com.mobei.concurrent;

import java.util.concurrent.atomic.AtomicInteger;

public class AtomicIntegerDemo {

    static Integer i = 0;
    static AtomicInteger j = new AtomicInteger(0);

    public static void main(String[] args) {
//		synchronizedAdd();
        atomicAdd();
    }

    private static void synchronizedAdd() {
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                // 10个线程就要依次的慢慢的一个一个的进入锁代码块
                // 然后依次对i变量进行++的操作
                // 每次操作完i++，就写回到主存，下一个线程间进行从主存来加载，再次i++
                synchronized (AtomicIntegerDemo.class) {
                    System.out.println(++AtomicIntegerDemo.i);
                }
            }).start();
        }
    }

    private static void atomicAdd() {
        for (int i = 0; i < 10; i++) {
            new Thread(() -> System.out.println(AtomicIntegerDemo.j.incrementAndGet())).start();
        }
    }

}
