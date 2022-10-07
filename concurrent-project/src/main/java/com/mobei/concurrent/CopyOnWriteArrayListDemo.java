package com.mobei.concurrent;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author liuyaowu
 * @date 2022/10/7 19:07
 * @remark
 */
public class CopyOnWriteArrayListDemo {

    public static void main(String[] args) {
        List<String> list = new CopyOnWriteArrayList<>();
        list.add("element1");
        list.add("element2");

        list.set(0, "updated1");

        list.remove(1);
        System.out.println(list);
    }

}
