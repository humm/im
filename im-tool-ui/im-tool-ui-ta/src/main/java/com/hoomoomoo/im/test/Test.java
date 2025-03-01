package com.hoomoomoo.im.test;


import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;

public class Test {

    public static void main(String[] args) {
        /*while (true) {
            Runtime runtime = Runtime.getRuntime();
            long totalMemory = runtime.totalMemory(); // 获取JVM总内存
            long freeMemory = runtime.freeMemory(); // 获取空闲内存
            long maxMemory = runtime.maxMemory(); // 获取最大可用内存
            long usedMemory = totalMemory - freeMemory; // 计算已用内存

            System.out.println("Total Memory (JVM): " + totalMemory / (1024 * 1024) + " MB");
            System.out.println("Free Memory (JVM): " + freeMemory / (1024 * 1024) + " MB");
            System.out.println("Used Memory (JVM): " + usedMemory / (1024 * 1024) + " MB");
            System.out.println("Max Memory (JVM): " + maxMemory / (1024 * 1024) + " MB");
            for (int i=0; i<5000; i++) {
                new MenuFunctionConfig();
            }
        }*/

        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory(); // 获取JVM总内存
        long freeMemory = runtime.freeMemory(); // 获取空闲内存
        long maxMemory = runtime.maxMemory(); // 获取最大可用内存
        long usedMemory = totalMemory - freeMemory; // 计算已用内存

        System.out.println("Total Memory (JVM): " + totalMemory / (1024 * 1024) + " MB");
        System.out.println("Free Memory (JVM): " + freeMemory / (1024 * 1024) + " MB");
        System.out.println("Used Memory (JVM): " + usedMemory / (1024 * 1024) + " MB");
        System.out.println("Max Memory (JVM): " + maxMemory / (1024 * 1024) + " MB");

        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage heapMemoryUsage = memoryMXBean.getHeapMemoryUsage();
        MemoryUsage nonHeapMemoryUsage = memoryMXBean.getNonHeapMemoryUsage();

        System.out.println("Heap Memory Usage:");
        System.out.println("  Init: " + heapMemoryUsage.getInit() / (1024 * 1024) + " MB");
        System.out.println("  Used: " + heapMemoryUsage.getUsed() / (1024 * 1024) + " MB");
        System.out.println("  Committed: " + heapMemoryUsage.getCommitted() / (1024 * 1024) + " MB");
        System.out.println("  Max: " + heapMemoryUsage.getMax() / (1024 * 1024) + " MB");

        System.out.println("Non-Heap Memory Usage:");
        System.out.println("  Init: " + nonHeapMemoryUsage.getInit() / (1024 * 1024) + " MB");
        System.out.println("  Used: " + nonHeapMemoryUsage.getUsed() / (1024 * 1024) + " MB");
        System.out.println("  Committed: " + nonHeapMemoryUsage.getCommitted() / (1024 * 1024) + " MB");
        System.out.println("  Max: " + nonHeapMemoryUsage.getMax() / (1024 * 1024) + " MB");


    }
}
