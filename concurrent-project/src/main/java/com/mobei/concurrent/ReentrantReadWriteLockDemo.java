package com.mobei.concurrent;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author liuyaowu
 * @date 2022/9/23 22:14
 * @remark
 */
public class ReentrantReadWriteLockDemo {
    static int data = 0;
    public static void main(String[] args) throws InterruptedException {
        ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
        ReentrantReadWriteLock.ReadLock readLock = lock.readLock();
        ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();
        Condition condition = readLock.newCondition();
        condition.await();
//        lock.writeLock().lock();
//        lock.writeLock().unlock();

        Thread t1 = new Thread(() -> {
            readLock.lock();
            writeLock.lock();
            try {
                Thread.sleep(600000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                readLock.unlock();
                writeLock.unlock();
            }
//            for (int i = 0; i < 100000; i++) {
////                writeLock.lock();
//                readLock.lock();
//                try {
//                    data++;
////                    System.out.println(data);
////                    Thread.sleep(2000);
//                } /*catch (InterruptedException e) {
//                    e.printStackTrace();
//                }*/ finally {
////                    writeLock.unlock();
//                    readLock.unlock();
//                }
//            }
        });
        t1.start();

        Thread.sleep(1000);

        Thread t2 = new Thread(() -> {
//            readLock.lock();
//            readLock.lock();
            writeLock.lock();
            writeLock.lock();

            try {
                Thread.sleep(100000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
//                readLock.unlock();
//                readLock.unlock();
                writeLock.unlock();
                writeLock.unlock();
            }
//            for (int i = 0; i < 100000; i++) {
////                writeLock.lock();
//                readLock.lock();
//                try {
//                    data++;
////                    System.out.println(data);
//                } finally {
////                    writeLock.unlock();
//                    readLock.unlock();
//                }
//            }
        });
        t2.start();
//
//
//        Thread.sleep(5000);
//
//        Thread t3 = new Thread(() -> {
//            readLock.lock();
//            readLock.lock();
//
//            try {
//                Thread.sleep(2000000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            } finally {
//                readLock.unlock();
//                readLock.unlock();
//            }
//        });
//        t3.start();
//
//        t1.join();
//        t2.join();
//
//        System.out.println(data);

//        lock.readLock().lock();
//        lock.readLock().lock();
//        lock.readLock().unlock();
//        lock.readLock().unlock();
    }
}
