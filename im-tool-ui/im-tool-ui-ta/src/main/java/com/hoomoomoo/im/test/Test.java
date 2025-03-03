package com.hoomoomoo.im.test;


public class Test {

    public static void main(String[] args) {
        while (true) {
            Runtime runtime = Runtime.getRuntime();
            long totalMemory = runtime.totalMemory(); // 获取JVM总内存
            long freeMemory = runtime.freeMemory(); // 获取空闲内存
            long maxMemory = runtime.maxMemory(); // 获取最大可用内存
            long usedMemory = totalMemory - freeMemory; // 计算已用内存

            System.out.println("Total Memory (JVM): " + totalMemory / (1024 * 1024) + " MB");
            System.out.println("Free Memory (JVM): " + freeMemory / (1024 * 1024) + " MB");
            System.out.println("Used Memory (JVM): " + usedMemory / (1024 * 1024) + " MB");
            System.out.println("Max Memory (JVM): " + maxMemory / (1024 * 1024) + " MB");
            System.out.println("--------------------------------------------");
        }
    }
}
