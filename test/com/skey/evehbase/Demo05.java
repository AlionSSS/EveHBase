package com.skey.evehbase;

import com.skey.evehbase.request.ResultCallback;
import org.apache.hadoop.hbase.client.Result;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Descr
 * <p>
 * Date: 2019/2/22 9:21
 *
 * @author A Lion~
 */
public class Demo05 {

    public static void main(String[] args) {
        ResultCallback<Result> callback = new ResultCallback<Result>() {
            @Override
            public void onSuccessful(List<Result> results) {

            }

            @Override
            public void onFailed(Exception e) {

            }
        };

        ParameterizedType parameterizedType = (ParameterizedType) callback.getClass().getGenericInterfaces()[0];
        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
        for (Type actualTypeArgument : actualTypeArguments) {
            System.out.println(Result.class.getName().equals(actualTypeArgument.getTypeName()));
        }
    }

}
