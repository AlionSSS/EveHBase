package com.skey.evehbase.pool;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.*;

/**
 * 全局的线程池
 * <p>
 * Date: 2019/2/15 8:52
 *
 * @author A Lion~
 */
public class PoolEngine {

    private PoolEngine() {

    }

    public static PoolEngine getInstance() {
        return InnerClass.instance;
    }

    private static class InnerClass {
        private static final PoolEngine instance = new PoolEngine();
    }

    private static final String NAME_FORMAT = "eve-pool-%d";

    private static int corePoolSize = 4;

    private static int maximumPoolSize = 8;

    private static long keepAliveTime = 100;

    private static int queueCapacity = 1024;

    public static void setConf(int core, int maximum, long keepAlive, int capacity) {
        corePoolSize = core;
        maximumPoolSize = maximum;
        keepAliveTime = keepAlive;
        queueCapacity = capacity;
    }

    private ThreadFactory namedThreadFactory = new ThreadFactoryBuilder()
                .setNameFormat(NAME_FORMAT)
                .build();

    private ExecutorService pool = new ThreadPoolExecutor(
            corePoolSize,
            maximumPoolSize,
            keepAliveTime,
            TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(queueCapacity),
            namedThreadFactory,
            new ThreadPoolExecutor.AbortPolicy());

    public void execute(Runnable runnable) {
        pool.execute(runnable);
    }

    public <T> Future<T> submit(Callable<T> callable) {
        return pool.submit(callable);
    }

    public void shutdown() {
        if (pool != null) pool.shutdown();
    }

}
