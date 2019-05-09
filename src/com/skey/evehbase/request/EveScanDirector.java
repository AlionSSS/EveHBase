package com.skey.evehbase.request;

import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.util.Bytes;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Set;

/**
 * 生成EveScan的导演类
 * <p>
 * Date: 2019/2/16 14:05
 *
 * @author A Lion~
 */
class EveScanDirector {

    private EveScanDirector() {
        throw new AssertionError(this + "不应该被实例化！");
    }

    /**
     * 按流程生成EveScan
     * @param table 表名
     * @param startRow 起始的rowkey
     * @param endRow 结束的rowkey
     * @param columnMap 列簇
     * @param filter 过滤器
     * @param max 最大返回条数
     */
    static EveScan create(String table, String startRow, String endRow,
                                 Map<String, Set<String>> columnMap, Filter filter, long max) {
        if (max < 0) throw new IllegalArgumentException("最大返回条数max不能小于0！");

        TableName tableName = TableName.valueOf(table);

        Scan scan = generateScan(columnMap, startRow, endRow, filter);

        return new EveScan(tableName, scan, max);
    }

    /**
     * 根据rowkey、列字段、过滤器生成Scan
     *
     * @param columnMap 要查询的列
     * @param startRow  HBase的startRowKey
     * @param endRow    HBase的endRowKey
     * @param filter    HBase的过滤器 {@link Filter}
     * @return {@link Scan}
     */
    private static Scan generateScan(Map<String, Set<String>> columnMap, String startRow, String endRow, Filter filter) {
        Scan scan = new Scan();

        // 添加要查询的列
        for (Map.Entry<String, Set<String>> entry : columnMap.entrySet()) {
            byte[] bFamily = Bytes.toBytes(entry.getKey());
            for (String qualifier : entry.getValue()) {
                scan.addColumn(bFamily, Bytes.toBytes(qualifier));
            }
        }

        // 设置key的起始、结束位置
        if (startRow != null) scan.setStartRow(Bytes.toBytes(startRow));
        if (endRow != null) scan.setStopRow(Bytes.toBytes(endRow));

        // 设置查询条件
        if (filter != null) scan.setFilter(filter);

        return scan;
    }

}
