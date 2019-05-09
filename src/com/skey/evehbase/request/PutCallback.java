package com.skey.evehbase.request;


/**
 * HBase Put 回调接口
 * <p>
 * Date: 2019/2/28 14:58
 *
 * @author A Lion~
 */
public interface PutCallback {

    void onSuccessful();

    void onFailed(Exception e);

}
