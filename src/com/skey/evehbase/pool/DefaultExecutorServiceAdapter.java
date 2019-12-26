package com.skey.evehbase.pool;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.*;

/**
 * Description: 默认的ExecutorService适配器
 * <br/>
 * Date: 2019/12/26 22:40
 *
 * @author ALion
 */
public class DefaultExecutorServiceAdapter implements ExecutorServiceAdapter {

    @Override
    public ExecutorService generateExecutorService() {
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder()
                .setNameFormat("eve-pool-%d")
                .build();

        return new ThreadPoolExecutor(
                4,
                8,
                100,
                TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(1024),
                namedThreadFactory,
                new ThreadPoolExecutor.AbortPolicy());
    }

}
