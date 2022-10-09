package com.mobei.concurrent;

/**
 * @author liuyaowu
 * @date 2022-09-27 13:48
 * @description
 */
public class PrintSyncMaskDemo {
    public static void main(String[] args) {
        final int SHARED_SHIFT = 16;
        final int SHARED_UNIT = (1 << SHARED_SHIFT);
        final int MAX_COUNT = (1 << SHARED_SHIFT) - 1;
        final int EXCLUSIVE_MASK = (1 << SHARED_SHIFT) - 1;

        System.out.println("SHARED_SHIFT: " + SHARED_SHIFT);
        System.out.println(Integer.toHexString(SHARED_SHIFT));
        System.out.println("SHARED_UNIT: " + SHARED_UNIT);
        System.out.println(Integer.toHexString(SHARED_UNIT));
        System.out.println("MAX_COUNT: " + MAX_COUNT);
        System.out.println(Integer.toHexString(MAX_COUNT));
        System.out.println("EXCLUSIVE_MASK: " + EXCLUSIVE_MASK);
        System.out.println(Integer.toHexString(EXCLUSIVE_MASK));

        System.out.println("---" + Integer.toHexString(1 >>> SHARED_SHIFT));
        System.out.println("===" + Integer.toHexString(1 & EXCLUSIVE_MASK));

        System.out.println(">>> SHARED_SHIFT: " + (0 >>> SHARED_SHIFT));
        System.out.println(Integer.toHexString(0 >>> SHARED_SHIFT));

        System.out.println(">>> SHARED_SHIFT: " + (65536 >>> SHARED_SHIFT));
        System.out.println(Integer.toHexString(65536 >>> SHARED_SHIFT));


        System.out.println("65536 + 65536 : " + Integer.toHexString(65536 + 65536));

//        System.out.println(Integer.toBinaryString(1 & EXCLUSIVE_MASK));

//        System.out.println(Integer.toBinaryString(16));
//        System.out.println(Integer.toBinaryString(16 - 1));
//        System.out.println(Integer.toBinaryString(1 >>> 16));
//
//
//        System.out.println("===================================");
//
//        System.out.println(Integer.toBinaryString((1 << 16) - 1));
    }
}
