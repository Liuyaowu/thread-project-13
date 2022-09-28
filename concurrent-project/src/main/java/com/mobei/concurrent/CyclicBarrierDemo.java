package com.mobei.concurrent;

import java.util.concurrent.CyclicBarrier;

/**
 * @author liuyaowu
 * @date 2022/9/29 6:59
 * @remark
 */
public class CyclicBarrierDemo {

    public static void main(String[] args) {
        CyclicBarrier cyclicBarrier = new CyclicBarrier(3, new Runnable() {
            @Override
            public void run() {
                System.out.println("所有线程都完成了自己的任务,可以开始合并任务了");
            }
        });

        new Thread() {
            @Override
            public void run() {
                try {
                    System.out.println("线程1完成了自己的工作...");
                    cyclicBarrier.await();
                    System.out.println("结果合并完毕,线程1退出");
                } catch (Exception e) {

                }
            }
        }.start();

        new Thread() {
            @Override
            public void run() {
                try {
                    System.out.println("线程2完成了自己的工作...");
                    cyclicBarrier.await();
                    System.out.println("结果合并完毕,线程2退出");
                } catch (Exception e) {

                }
            }
        }.start();

        new Thread() {
            @Override
            public void run() {
                try {
                    System.out.println("线程3完成了自己的工作...");
                    cyclicBarrier.await();
                    System.out.println("结果合并完毕,线程3退出");
                } catch (Exception e) {

                }
            }
        }.start();
    }

}
