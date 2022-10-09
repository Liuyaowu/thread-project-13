package com.mobei.concurrent;

import java.util.concurrent.locks.ReentrantLock;

public class ReentractLockDemo {

    static int data = 0;
    static ReentrantLock lock = new ReentrantLock();

    public static void main(String[] args) throws Exception {
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 100000; i++) {
                lock.lock();
                try {
                    data++;
//                    System.out.println(ReentractLockDemo.data);
//                    Thread.sleep(2000);
                } /*catch (InterruptedException e) {
                    e.printStackTrace();
                }*/ finally {
                    lock.unlock();
                }
            }
        });
        t1.start();

//        Thread.sleep(5000);

        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 100000; i++) {
                lock.lock();
                try {
                    data++;
//                    System.out.println(ReentractLockDemo.data);
                } finally {
                    lock.unlock();
                }
            }
        });
        t2.start();

        Thread t3 = new Thread(() -> {
            for (int i = 0; i < 100000; i++) {
                lock.lock();
                try {
                    data++;
//                    System.out.println(ReentractLockDemo.data);
                } finally {
                    lock.unlock();
                }
            }
        });
        t3.start();

        t1.join();
        t2.join();
        t3.join();

        System.out.println(data);
    }

}
