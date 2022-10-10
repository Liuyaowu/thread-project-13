package com.mobei.concurrent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author liuyaowu
 * @date 2022-10-09 9:15
 * @description
 */
public class ReentrantLockConditionDemo {

    private int queueSize = 5;
//    private PriorityQueue<Integer> queue = new PriorityQueue<>(queueSize);
    private List<Integer> queue = new ArrayList<>(queueSize);
    private Lock lock = new ReentrantLock(false);
    private Condition notFull = lock.newCondition();
    private Condition notEmpty = lock.newCondition();

    private Condition condition = lock.newCondition();

    public static void main(String[] args) throws InterruptedException {
        ReentrantLockConditionDemo conditionDemo = new ReentrantLockConditionDemo();
        Producer producer = conditionDemo.new Producer();
        Consumer consumer = conditionDemo.new Consumer();
        producer.start();

        Thread.sleep(5000);
//        Thread.sleep(100);

        System.out.println("-----------");

        consumer.start();
//        Thread.sleep(0);
//        producer.interrupt();
//        consumer.interrupt();
    }

    class Producer extends Thread {
        volatile boolean flag = true;

        @Override
        public void run() {
            while (flag) {
                lock.lock();
                try {
//                    System.out.println("生产者----获取锁");
////                    Thread.sleep(5000);
//                    condition.await();
//                    System.out.println("======生产者等待结束");
                    if (queue.size() == queueSize) {
                        try {
                            System.out.println("队列满，等待有空余空间");
                            notFull.await();
                            System.out.println("队列有空间了，被唤醒");
                        } catch (InterruptedException e) {
                            flag = false;
                        }
                    }
                    //每次插入一个元素
//                    queue.offer(1);
                    queue.add(1);
                    notEmpty.signal();
                    Thread.sleep(100);
                    System.out.println("向队列中插入一个元素，队列空间：" + (queue.size()));
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    lock.unlock();
//                    try {
//                        System.out.println("生产者----释放锁");
//                        Thread.sleep(5000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
                }
            }
        }
    }

    class Consumer extends Thread {
        volatile boolean flag = true;
        volatile int count = 0;

        @Override
        public void run() {
            while (flag) {
                lock.lock();
                try {
//                    if (count == 5) {
//                        condition.signal();
//                    }
//                    System.out.println("消费者----获取锁");
//                    Thread.sleep(5000);

                    if (queue.isEmpty()) {
                        try {
                            System.out.println("消费者------队列空，等待数据");
                            notEmpty.await();
                            System.out.println("消费者++++++队列空，结束等待");
                        } catch (InterruptedException e) {
                            flag = false;
                        }
                    }
                    //每次移走队首元素
//                    queue.poll();
                    queue.remove(0);
                    notFull.signal();
                    Thread.sleep(100);
                    System.out.println("从队列取走一个元素，队列剩余" + queue.size() + "个元素");
//                    count ++;
//                    System.out.println(count);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    lock.unlock();
//                    try {
//                        System.out.println("消费者----释放锁");
//                        Thread.sleep(5000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
                }
            }
        }
    }
}
