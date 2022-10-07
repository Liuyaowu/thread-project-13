package com.mobei.dfs.datanode.server;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;

/**
 * 负责跟一组NameNode进行通信的OfferServie组件
 *
 * @author liuyaowu
 * @date 2022/9/28 7:54
 * @remark
 */
public class NameNodeGroupOfferService {

    /**
     * 负责跟NameNode主节点通信的ServiceActor组件
     */
    private NameNodeServiceActor activeServiceActor;
    /**
     * 负责跟NameNode备节点通信的ServiceActor组件
     */
    private NameNodeServiceActor standbyServiceActor;
    /**
     * 该datanode上保存的ServiceActor列表
     */
    private CopyOnWriteArrayList<NameNodeServiceActor> serviceActors;

    /**
     * 构造函数
     */
    public NameNodeGroupOfferService() {
        this.activeServiceActor = new NameNodeServiceActor();
        this.standbyServiceActor = new NameNodeServiceActor();

        this.serviceActors = new CopyOnWriteArrayList<>();
        this.serviceActors.add(activeServiceActor);
        this.serviceActors.add(standbyServiceActor);
    }

    /**
     * 启动OfferService组件
     */
    public void start() {
        // 直接使用两个ServiceActor组件分别向主备两个NameNode节点进行注册
        register();
    }

    /**
     * 关闭指定的ServiceActor
     *
     * @param serviceActor
     */
    public void shutdown(NameNodeServiceActor serviceActor) {
        this.serviceActors.remove(serviceActor);
    }

    /**
     * 向主备两个NameNode节点进行注册
     */
    private void register() {
        try {
            CountDownLatch latch = new CountDownLatch(2);
            this.activeServiceActor.register(latch);
            this.standbyServiceActor.register(latch);
            latch.await();
            System.out.println("主备NameNode全部注册完毕......");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
