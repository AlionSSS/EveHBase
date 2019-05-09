package com.skey.evehbase.request;

import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.util.Bytes;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 生成EveGet的导演类
 * <p>
 * Date: 2019/2/16 14:05
 *
 * @author A Lion~
 */
class EveGetDirector {

    private EveGetDirector() {
        throw new AssertionError(this + "不应该被实例化！");
    }

    /**
     * 按流程生成EveGet
     */
    static EveGet create(String table, Set<String> rowkeys, Map<String, Set<String>> columnMap) {
        TableName tableName = TableName.valueOf(table);

        List<Get> gets = rowkeys.stream()
                .map(rk -> generateGet(rk, columnMap))
                .collect(Collectors.toList());

        return new EveGet(tableName, gets);
    }

    /**
     * 根据rowkey和列字段生成Get
     * @param rowkey HBase的rowkey
     * @param columnMap 要查询的列
     * @return {@link Get}
     */
    private static Get generateGet(String rowkey, Map<String, Set<String>> columnMap) {
        Get get = new Get(Bytes.toBytes(rowkey));
        // 添加要查询的列
        for (Map.Entry<String, Set<String>> entry : columnMap.entrySet()) {
            byte[] bFamily = Bytes.toBytes(entry.getKey());
            for (String qualifier : entry.getValue()) {
                get.addColumn(bFamily, Bytes.toBytes(qualifier));
            }
        }
        return get;
    }

}
