package com.mobei.concurrent;

/**
 * @author liuyaowu
 * @date 2022-09-29 17:49
 * @description
 */
public class OneToHundred {
    public static void main(String[] args) throws InterruptedException {
        Task t = new Task();
        Thread t1 = new Thread(t, "A");
        Thread t2 = new Thread(t, "B");
//        Thread t3 = new Thread(t, "C");
//        Thread t4 = new Thread(t, "D");
//        Thread t5 = new Thread(t, "E");
//        Thread t6 = new Thread(t, "F");
//        Thread t7 = new Thread(t, "G");
//        Thread t8 = new Thread(t, "H");

        t1.start();
        t2.start();
//        t3.start();
//        t4.start();
//        t5.start();
//        t6.start();
//        t7.start();
//        t8.start();

        t1.join();
        t2.join();
//        t3.join();
//        t4.join();
//        t5.join();
//        t6.join();
//        t7.join();
//        t8.join();

        System.out.println(t.getNumber());
        System.out.println(t.getX());
    }
}

class Task implements Runnable {
    private int number = 0;
    private int x = 0;
    @Override
    public void run() {
        while (number < 200000000) {
            number++;
            x++;
        }
    }

    public int getNumber() {
        return number;
    }
    public int getX() {
        return x;
    }
}
