package com.mobei.concurrent;

import java.util.Iterator;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author liuyaowu
 * @date 2022/10/8 21:23
 * @remark
 */
public class LinkedBlockingQueueDemo {
    public static void main(String[] args) throws Exception {
        LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<>(10);

        // 如果队列满了就会处于阻塞状态
        queue.put("put1");

        queue.offer("e1");
        queue.offer("e2");
        queue.offer("e3");

        System.out.println(queue.take());
        System.out.println(queue.size());

        Iterator<String> iterator = queue.iterator();
        while (iterator.hasNext()) {
            System.out.println(iterator.next());
        }
    }
}
