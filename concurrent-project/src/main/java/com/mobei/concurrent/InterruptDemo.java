package com.mobei.concurrent;

/**
 * @author liuyaowu
 * @date 2022/9/14 20:25
 * @remark
 */
public class InterruptDemo {

    public static void main(String[] args) throws Exception {
//        Thread thread = new Thread(){
//            @Override
//            public void run() {
//                while (true) {
//                    try {
//                        if (isInterrupted()) {
//                            /**
//                             * 线程虽然被中断了,但是不会阻断while ture的运行
//                             */
//                            System.out.println("线程被中断了");
//                            break;
//                        } else {
//                            /**
//                             * 休眠的线程会被唤醒
//                             */
//                            Thread.sleep(3000);
//                            System.out.println("线程正在工作");
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        };

        Thread thread = new Thread(){

            private Boolean shouldRun = true;

            @Override
            public void run() {
                while (shouldRun) {
                    try {
                        if (isInterrupted()) {
                            /**
                             * 线程虽然被中断了,但是不会阻断while ture的运行
                             */
                            System.out.println("线程被中断了");
                            shouldRun = false;
                        } else {
                            /**
                             * 休眠的线程会被唤醒并清除中断标记
                             */
                            Thread.sleep(1000);
                            System.out.println("线程正在工作");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        thread.start();
        Thread.sleep(2000);

        /**
         * 中断线程并设置中断标记,但是不会阻断while ture的运行
         */
        thread.interrupt();
    }

}
