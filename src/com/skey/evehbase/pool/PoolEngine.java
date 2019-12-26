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

    private ExecutorService pool;

    private static ExecutorServiceAdapter adapter;

    private PoolEngine() {
        pool = adapter.generateExecutorService();
    }

    public static PoolEngine getInstance() {
        return InnerClass.instance;
    }

    private static class InnerClass {
        private static final PoolEngine instance = new PoolEngine();
    }

    public static void setAdapter(ExecutorServiceAdapter adapter) {
        PoolEngine.adapter = adapter;
    }

    public void execute(Runnable runnable) {
        if (pool != null) {
            pool.execute(runnable);
        }
    }

    public <T> Future<T> submit(Callable<T> callable) {
        if (pool != null) {
            return pool.submit(callable);
        } else {
            return null;
        }
    }

    public void shutdown() {
        if (pool != null) {
            pool.shutdown();
        }
    }

}
