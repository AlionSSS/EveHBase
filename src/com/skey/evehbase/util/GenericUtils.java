package com.skey.evehbase.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Description
 *
 * @author ALion
 * @version 2019/5/9 17:36
 */
public class GenericUtils {

    private GenericUtils() {
        throw new AssertionError(this + "不应该被实例化！");
    }

    public static Type[] getInterfaceT(Object obj) {
        ParameterizedType paramType = (ParameterizedType) obj.getClass().getGenericInterfaces()[0];
        return paramType.getActualTypeArguments();
    }

    public static Type getSupperclassT(Object obj) {
        return obj.getClass().getGenericSuperclass();
    }

}
