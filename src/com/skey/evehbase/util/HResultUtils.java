package com.skey.evehbase.util;

import com.skey.evehbase.anno.ColumnFamily;
import com.skey.evehbase.anno.RowKey;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.util.Bytes;

import java.lang.reflect.Field;
import java.util.*;

/**
 * HBase 结果->对象 解析工具
 * <p>
 * Date: 2019/1/17 19:02
 *
 * @author A Lion~
 */
public class HResultUtils {

    private HResultUtils() {
        throw new AssertionError(this + "不应该被实例化！");
    }

    /**
     * 反射解析HBase查询结果成对象
     *
     * @param result HBase查询结果
     * @param clazz  对象类
     * @param <T>    对象类型
     * @return 对象
     */
    public static <T> T parse(Result result, Class<T> clazz) {
        List<Result> results = Collections.singletonList(result);
        return parse(results, clazz).get(0);
    }

    /**
     * 反射解析HBase查询结果成对象
     *
     * @param results HBase查询结果
     * @param clazz   对象类
     * @param <T>     对象类型
     * @return List<对象>
     */
    public static <T> List<T> parse(Collection<Result> results, Class<T> clazz) {
        List<T> list = new ArrayList<>();

        Map<Field, Map<Field, String>> map = parseClass(clazz);
//        System.out.println(map);
        for (Result result : results) {
            try {
                T obj = clazz.newInstance();
                for (Map.Entry<Field, Map<Field, String>> entry : map.entrySet()) {
                    Field key = entry.getKey();
                    Map<Field, String> value = entry.getValue();
                    if (value == null) {
                        key.set(obj, Bytes.toString(result.getRow()));
                    } else {
                        Class<?> cfClazz = key.getType();
                        Object cfObj = cfClazz.getDeclaredConstructors()[0].newInstance(clazz.newInstance());
                        for (Field cfField : cfClazz.getFields()) {
                            byte[] family = Bytes.toBytes(key.getName());
                            byte[] qualifier = Bytes.toBytes(cfField.getName());
                            cfField.set(cfObj, Bytes.toString(result.getValue(family, qualifier)));
                        }
                        // FIXME: 下面的代码待测试
//                        for (Field cfField : value.keySet()) {
//                            byte[] family = Bytes.toBytes(key.getName());
//                            byte[] qualifier = Bytes.toBytes(cfField.getName());
//                            cfField.set(cfObj, Bytes.toString(result.getValue(family, qualifier)));
//                        }
                        key.set(obj, cfObj);
                    }
                }
                list.add(obj);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    /**
     * 反射解析HBase查询结果Scanner
     *
     * @param rScanner HBase原生结果Scanner
     * @param clazz    对象类
     * @param <T>      对象类型
     * @return List<对象>
     */
    public static <T> List<T> parse(ResultScanner rScanner, Class<T> clazz) {
        ArrayList<Result> list = new ArrayList<>();
        for (Result result : rScanner) {
            list.add(result);
        }
        return parse(list, clazz);
    }

    /**
     * 根据clazz来决定最终解析为Result还是JavaBean
     */
    public static <T> List<T> parseResults(ArrayList<Result> results, Class<T> clazz) {
        List<T> objects = new ArrayList<>(results.size());
        if (clazz == Result.class) {
            for (Result result : results) {
                objects.add((T) result);
            }
        } else {
            objects = parse(results, clazz);
        }
        return objects;
    }

    /**
     * 解析类属性
     *
     * @param clazz 类
     * @return 'addRowkey' 或 '列簇 -> (列簇字段 -> 描述)'
     */
    private static Map<Field, Map<Field, String>> parseClass(Class<?> clazz) {
        Map<Field, Map<Field, String>> map = new HashMap<>();
        try {
            boolean flag = true; // 用于解析rowkey
            for (Field field : clazz.getFields()) {
                field.setAccessible(true);
                if (flag) {
                    RowKey rowKey = field.getAnnotation(RowKey.class);
                    if (rowKey != null) {
                        // 如果是rowkey，那么value为null
                        map.put(field, null);
                        flag = false;
                    }
                }

                ColumnFamily cf = field.getAnnotation(ColumnFamily.class);
                if (cf != null) {
                    Map<Field, String> cfMap = new HashMap<>(8);
                    Class<?> cfClazz = field.getType();
                    for (Field cfField : cfClazz.getFields()) {
                        // TODO: 2019/1/28 暂时对列簇中的字段的描述为null，后续可添加描述解析类型的注解
                        cfMap.put(cfField, null);
                    }
                    map.put(field, cfMap);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

}
