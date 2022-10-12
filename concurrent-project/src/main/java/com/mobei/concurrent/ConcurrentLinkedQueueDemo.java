package com.mobei.concurrent;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author liuyaowu
 * @date 2022/10/7 20:29
 * @remark
 */
public class ConcurrentLinkedQueueDemo {

    public static void main(String[] args) {
        ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<>();

        queue.add("add1");

        queue.offer("offer1");
        queue.offer("offer2");
        queue.offer("offer3");

        System.out.println(queue.poll());
        System.out.println(queue.peek());
        System.out.println(queue.remove());

        System.out.println(queue);
    }

}
