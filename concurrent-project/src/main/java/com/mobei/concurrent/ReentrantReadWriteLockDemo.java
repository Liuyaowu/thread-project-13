package com.mobei.concurrent;

import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author liuyaowu
 * @date 2022/9/23 22:14
 * @remark
 */
public class ReentrantReadWriteLockDemo {
    public static void main(String[] args) {
        ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

        lock.writeLock().lock();
        lock.writeLock().unlock();

        lock.readLock().lock();
        lock.readLock().unlock();
    }
}
