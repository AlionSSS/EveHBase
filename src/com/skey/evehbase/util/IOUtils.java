package com.skey.evehbase.util;

/**
 * I/O工具
 * <p>
 * Date: 2019/1/15 12:28
 *
 * @author A Lion~
 */
public class IOUtils {

    private IOUtils() {
        throw new AssertionError(this + "不应该被实例化！");
    }

    public static void close(AutoCloseable... closeables) {
        for (AutoCloseable closeable : closeables) {
            if (closeable != null) {
                try {
                    closeable.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
