package com.mobei.register.server.cluster;

import java.util.ArrayList;
import java.util.List;

public class RegisterServerCluster {

    private static List<String> peers = new ArrayList<>();

    static {
        // 读取配置文件，看看你配合了哪些机器部署的register-server
    }

    public static List<String> getPeers() {
        return peers;
    }

}
