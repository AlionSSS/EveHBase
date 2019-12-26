package com.skey.evehbase.pool;


import java.util.concurrent.ExecutorService;

/**
 * Description: ExecutorService适配器
 * <br/>
 * Date: 2019/12/26 22:19
 *
 * @author ALion
 */
public interface ExecutorServiceAdapter {

    ExecutorService generateExecutorService();

}
