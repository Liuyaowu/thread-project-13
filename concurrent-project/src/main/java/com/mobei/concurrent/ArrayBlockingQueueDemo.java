package com.mobei.concurrent;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * @author liuyaowu
 * @date 2022/10/9 8:09
 * @remark
 */
public class ArrayBlockingQueueDemo {
    public static void main(String[] args) throws InterruptedException {
        ArrayBlockingQueue<String> queue = new ArrayBlockingQueue<>(10);
        queue.put("put1");
    }
}
