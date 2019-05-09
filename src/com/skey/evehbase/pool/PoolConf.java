package com.skey.evehbase.pool;

/**
 * 线程池配置
 *
 * @author ALion
 * @version 2019/5/9 20:09
 */
public class PoolConf {

    public int core;

    public int maximum;

    public int keepAlive;

    public int queueCapacity;

    public PoolConf(int core, int maximum, int keepAlive, int queueCapacity) {
        this.core = core;
        this.maximum = maximum;
        this.keepAlive = keepAlive;
        this.queueCapacity = queueCapacity;
    }

}
