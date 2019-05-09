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
public enum PoolEngine {

    INSTANCE;

    public static final String NAME_FORMAT = "eve-pool-%d";

    public static final int CORE_POOL_SIZE = 4;

    public static final int MAXIMUM_POOL_SIZE = 8;

    public static final long KEEP_ALIVE_TIME = 100;

    public static final int QUEUE_CAPACITY = 1024;

    private ThreadFactory namedThreadFactory = new ThreadFactoryBuilder()
                .setNameFormat(NAME_FORMAT)
                .build();

    private ExecutorService pool = new ThreadPoolExecutor(
            CORE_POOL_SIZE,
            MAXIMUM_POOL_SIZE,
            KEEP_ALIVE_TIME,
            TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(QUEUE_CAPACITY),
            namedThreadFactory,
            new ThreadPoolExecutor.AbortPolicy());

    public void execute(Runnable runnable) {
        pool.execute(runnable);
    }

    public <T> Future<T> submit(Callable<T> callable) {
        return pool.submit(callable);
    }

    public void shutdown() {
        pool.shutdown();
    }

}
