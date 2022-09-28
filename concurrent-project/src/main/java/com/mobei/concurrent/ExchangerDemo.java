package com.mobei.concurrent;

import java.util.concurrent.Exchanger;

/**
 * 支持线程之间进行数据交换的组件
 *
 * @author liuyaowu
 * @date 2022/9/29 7:52
 * @remark
 */
public class ExchangerDemo {
    public static void main(String[] args) {
        Exchanger<String> exchanger = new Exchanger<>();

        new Thread(() -> {
            try {
                String data = exchanger.exchange("线程1的数据");
                System.out.println("线程1获取到线程2交换过来的数据: " + data);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        new Thread(() -> {
            try {
                String data = exchanger.exchange("线程2的数据");
                System.out.println("线程2获取到线程1交换过来的数据: " + data);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

    }
}
