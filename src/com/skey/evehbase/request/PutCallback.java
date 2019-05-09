package com.skey.evehbase.request;


import org.apache.hadoop.hbase.client.Put;

import java.util.List;

/**
 * HBase Put 回调接口
 * <p>
 * Date: 2019/2/28 14:58
 *
 * @author A Lion~
 */
public interface PutCallback {

    void onSuccessful();

    void onFailed(Exception e, List<Put> puts);

}
