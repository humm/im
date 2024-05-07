package com.hoomoomoo.im.utils;

import java.util.concurrent.*;

public class TaskUtils {

    private static ThreadPoolExecutor threadPoolExecutor;

    private TaskUtils() {
    }

    static void initThreadPoolExecutor() {
        if (threadPoolExecutor == null) {
            threadPoolExecutor = new ThreadPoolExecutor(10, 50, 60L, TimeUnit.SECONDS, new SynchronousQueue<>());
        }
    }

    public static void execute(Callable callable) throws ExecutionException, InterruptedException {
        initThreadPoolExecutor();
        threadPoolExecutor.submit(callable);
    }
}
