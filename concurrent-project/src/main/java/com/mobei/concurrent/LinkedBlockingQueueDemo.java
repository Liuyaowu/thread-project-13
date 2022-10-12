package com.mobei.concurrent;

import java.util.Iterator;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author liuyaowu
 * @date 2022/10/8 21:23
 * @remark
 */
public class LinkedBlockingQueueDemo {
    public static void main(String[] args) throws InterruptedException {
        LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<>(2);

        System.out.println(queue.poll());

        // 如果队列满了就会处于阻塞状态
        queue.put("put1");
//        queue.put("put1");
//        queue.put("put1");
//        System.out.println("队列put满了: " + queue);

        queue.offer("e1");
        queue.offer("e2");
        queue.offer("e3");
        System.out.println("队列offer满了: " + queue);

        System.out.println(queue.take());
        System.out.println(queue.peek());
        System.out.println(queue.poll());
        System.out.println(queue.size());

        Iterator<String> iterator = queue.iterator();
        while (iterator.hasNext()) {
            System.out.println(iterator.next());
        }
    }
}
