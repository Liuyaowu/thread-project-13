package com.mobei.register.server.cluster;

import com.mobei.register.server.web.AbstractRequest;
import com.mobei.register.server.web.CancelRequest;
import com.mobei.register.server.web.HeartbeatRequest;
import com.mobei.register.server.web.RegisterRequest;

import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 集群同步组件
 */
public class PeersReplicator {

    /**
     * 集群同步生成batch的间隔时间：500ms
     */
    private static final long PEERS_REPLICATE_BATCH_INTERVAL = 500;

    private static final PeersReplicator instance = new PeersReplicator();

    private PeersReplicator() {
        // 启动接收请求和打包batch的线程
        AcceptorBatchThread acceptorBatchThread = new AcceptorBatchThread();
        acceptorBatchThread.setDaemon(true);
        acceptorBatchThread.start();

        // 启动同步发送batch的线程
        PeersReplicateThread peersReplicateThread = new PeersReplicateThread();
        peersReplicateThread.setDaemon(true);
        peersReplicateThread.start();
    }

    public static PeersReplicator getInstance() {
        return instance;
    }

    /**
     * 第一层队列：接收请求的高并发写入，无界队列(有界队列可能导致线程全部卡死,一般宁愿内存撑爆也不要让线程卡死导致服务挂掉)
     */
    private ConcurrentLinkedQueue<AbstractRequest> acceptorQueue = new ConcurrentLinkedQueue<>();
    /**
     * 第二层队列：有界队列，用于batch生成,对这个队列的入队和出队操作是少数后台线程在执行,
     * 并不是大量线程高并发写入的,做成有界的,避免异常情况下过度消耗内存
     */
    private LinkedBlockingQueue<AbstractRequest> batchQueue = new LinkedBlockingQueue<>(1000000);
    /**
     * 第三层队列：有界队列，用于batch的同步发送
     * <p>
     * 每个AbstractRequest会占用多少字节的内存，假设大概来说一个request占用30个字节,
     * 平均一个batch包含100个request，3000个字节，3kb,10000 -> 30mb
     */
    private LinkedBlockingQueue<PeersReplicateBatch> replicateQueue = new LinkedBlockingQueue<>(10000);

    /**
     * 同步服务注册请求
     */
    public void replicateRegister(RegisterRequest request) {
        request.setType(AbstractRequest.REGISTER_REQUEST);
        acceptorQueue.offer(request);
    }

    /**
     * 同步服务下线请求
     */
    public void replicateCancel(CancelRequest request) {
        request.setType(AbstractRequest.CANCEL_REQUEST);
        acceptorQueue.offer(request);
    }

    /**
     * 同步发送心跳请求
     */
    public void replicateHeartbeat(HeartbeatRequest request) {
        request.setType(AbstractRequest.HEARTBEAT_REQUEST);
        acceptorQueue.offer(request);
    }

    /**
     * 负责接收数据以及打包为batch的后台线程
     */
    class AcceptorBatchThread extends Thread {
        long latestBatchGeneration = System.currentTimeMillis();

        @Override
        public void run() {
            while (true) {
                try {
                    // 从第一层队列获取请求，并且放入第二层队列中
                    AbstractRequest request = acceptorQueue.poll();
                    if (request != null) {
                        batchQueue.put(request);
                    }

                    // 采用一定的策略来进行打包，每隔500ms生成一个batch
                    long now = System.currentTimeMillis();
                    if (now - latestBatchGeneration >= PEERS_REPLICATE_BATCH_INTERVAL) {
                        // 此时如果第二层队列里面有数据的，生成一个batch
                        if (batchQueue.size() > 0) {
                            PeersReplicateBatch batch = createBatch();
                            replicateQueue.offer(batch);
                        }
                        this.latestBatchGeneration = System.currentTimeMillis();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        /**
         * 创建一个batch
         *
         * @return
         */
        private PeersReplicateBatch createBatch() {
            PeersReplicateBatch batch = new PeersReplicateBatch();

            Iterator<AbstractRequest> iterator = batchQueue.iterator();
            while (iterator.hasNext()) {
                AbstractRequest request = iterator.next();
                batch.add(request);
            }

            batchQueue.clear();
            return batch;
        }
    }

    /**
     * 集群同步线程
     */
    class PeersReplicateThread extends Thread {
        @Override
        public void run() {
            while (true) {
                try {
                    PeersReplicateBatch batch = replicateQueue.take();
                    if (batch != null) {
                        // 遍历所有的其他的register-server地址
                        // 给每个地址的register-server都发送一个http请求同步batch
                        System.out.println("给所有其他的register-server发送请求，同步batch过去......");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}