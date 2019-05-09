package com.skey.evehbase.request;

import java.util.List;

/**
 * HBase结果 回调接口
 * <p>
 * Date: 2019/2/14 14:34
 *
 * @author A Lion~
 */
public interface ResultCallback<T> {

    void onSuccessful(List<T> results);

    void onFailed(Exception e);

}
