package com.mobei.concurrent;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author liuyaowu
 * @date 2022/10/7 13:50
 * @remark
 */
public class ConcurrentHashMapDemo {

    public static void main(String[] args) {
        ConcurrentHashMap<String, String> map = new ConcurrentHashMap();
        map.put("k1", "v1");

        System.out.println(map.get("k1"));
    }

}
