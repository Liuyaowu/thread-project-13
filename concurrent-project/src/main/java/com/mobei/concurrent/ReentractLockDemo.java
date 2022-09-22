package com.mobei.concurrent;

import java.util.concurrent.locks.ReentrantLock;

public class ReentractLockDemo {

    static int data = 0;
    static ReentrantLock lock = new ReentrantLock();

    public static void main(String[] args) {
        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                lock.lock();
                try {
                    ReentractLockDemo.data++;
                    System.out.println(ReentractLockDemo.data);
                } finally {
                    lock.unlock();
                }
            }
        }).start();

        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                lock.lock();
                try {
                    ReentractLockDemo.data++;
                    System.out.println(ReentractLockDemo.data);
                } finally {
                    lock.unlock();
                }
            }
        }).start();
    }

}
