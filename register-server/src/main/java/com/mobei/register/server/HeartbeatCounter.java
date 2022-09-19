package com.mobei.register.server;

import java.util.concurrent.atomic.LongAdder;

/**
 * 心跳测量计数器
 *
 * @author liuyaowu
 * @date 2022/9/16 7:29
 * @remark
 */
public class HeartbeatCounter {

    /**
     * 单例实例
     */
    private static HeartbeatCounter instance = new HeartbeatCounter();

    /**
     * 最近一分钟的心跳次数
     */
//    private long latestMinuteHeartbeatRate = 0L;
//    private AtomicLong latestMinuteHeartbeatRate = new AtomicLong(0L);
    private LongAdder latestMinuteHeartbeatRate = new LongAdder();

    /**
     * 最近一分钟的时间戳
     */
    private long latestMinuteTimestamp = System.currentTimeMillis();

    private HeartbeatCounter() {
        Daemon daemon = new Daemon();
        daemon.setDaemon(true);
        daemon.start();
    }

    /**
     * 获取单例实例
     *
     * @return
     */
    public static HeartbeatCounter getInstance() {
        return instance;
    }

    /**
     * 增加一次最近一分钟的心跳次数
     */
//    public void increment() {
//        synchronized (HeartbeatCounter.class) {
//            latestMinuteHeartbeatRate++;
//        }
//    }
    /**
     * 优化后
     * <p>
     * 增加一次最近一分钟的心跳次数
     */
    public void increment() {
//        latestMinuteHeartbeatRate.incrementAndGet();
        latestMinuteHeartbeatRate.increment();
    }

    /**
     * 获取最近一分钟的心跳次数
     */
//    public synchronized long get() {
//        return latestMinuteHeartbeatRate;
//    }
    public long get() {
//        return latestMinuteHeartbeatRate.get();
        return latestMinuteHeartbeatRate.longValue();
    }

    private class Daemon extends Thread {
        @Override
        public void run() {
            while (true) {
                try {
                    long currentTime = System.currentTimeMillis();
                    if (currentTime - latestMinuteTimestamp > 60 * 1000) {
                        latestMinuteHeartbeatRate = new LongAdder();
                        latestMinuteTimestamp = System.currentTimeMillis();
                    }
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
