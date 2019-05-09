package com.skey.evehbase.request;

import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.Filter;

import java.util.*;

/**
 * HBase的Scan请求构造
 * <p>
 * Date: 2019/2/15 17:40
 *
 * @author A Lion~
 */
public class EveScan {

    private final TableName tableName;

    private final Scan scan;

    private final long max;

    EveScan(TableName tableName, Scan scan, long max) {
        this.tableName = tableName;
        this.scan = scan;
        this.max = max;
    }

    public TableName getTableName() {
        return tableName;
    }

    public Scan getScan() {
        return scan;
    }

    public long getMax() {
        return max;
    }

    public static class Builder {

        private String table;

        private String startRow;

        private String endRow;

        private Map<String, Set<String>> columnMap = new HashMap<>();

        private Filter filter;

        private long max = Long.MAX_VALUE;

        /**
         * 制定查询的表名
         *
         * @param table 表名
         * @return {@link EveScan.Builder}
         */
        public Builder table(String table) {
            this.table = table;
            return this;
        }

        /**
         * 设置起始rowkey
         * @param startRow 起始rowkey
         * @return {@link EveScan.Builder}
         */
        public Builder startRow(String startRow) {
            this.startRow = startRow;
            return this;
        }

        /**
         * 设置结束rowkey
         * @param endRow 结束rowkey
         * @return {@link EveScan.Builder}
         */
        public Builder endRow(String endRow) {
            this.endRow = endRow;
            return this;
        }

        /**
         * 添加要查询的信息
         * @param family 待查询的列簇
         * @param qualifiers 待查询的列簇字段名
         * @return {@link EveScan.Builder}
         */
        public Builder select(String family, String... qualifiers) {
            Set<String> set = columnMap.computeIfAbsent(family, k -> new HashSet<>());
            set.addAll(Arrays.asList(qualifiers));
            return this;
        }

        /**
         * 设置HBase做scan的时候的过滤器
         * @param filter 过滤器 {@link Filter}
         * @return {@link EveScan.Builder}
         */
        public Builder filter(Filter filter) {
            this.filter = filter;
            return this;
        }

        /**
         * 限制查询的数据的条数
         * @param max 最大条数
         * @return {@link EveScan.Builder}
         */
        public Builder limit(long max) {
            if (max < 0) throw new IllegalArgumentException("最大返回条数max不能小于0！");
            this.max = max;
            return this;
        }

        /**
         * 完成Scan构造
         * @return {@link EveScan}
         */
        public EveScan build() {
            return EveScanDirector.create(table, startRow, endRow, columnMap, filter, max);
        }

    }

}
