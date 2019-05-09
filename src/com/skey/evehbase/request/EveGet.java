package com.skey.evehbase.request;

import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Get;

import javax.annotation.Nonnull;
import java.util.*;

/**
 * HBase的Get请求构造
 * <p>
 * Date: 2019/2/14 9:18
 *
 * @author A Lion~
 */
public class EveGet {

    private final TableName tableName;

    private final List<Get> gets;

    EveGet(TableName tableName, List<Get> gets) {
        this.tableName = tableName;
        this.gets = gets;
    }

    public TableName getTableName() {
        return tableName;
    }


    public List<Get> getGets() {
        return gets;
    }

    public static class Builder {

        private String table;

        private Set<String> rowkeys = new HashSet<>();

        private Map<String, Set<String>> columnMap = new HashMap<>();

        /**
         * 制定查询的表名
         * @param table 表名
         * @return {@link EveGet.Builder}
         */
        public Builder table(@Nonnull String table) {
            this.table = table;
            return this;
        }

        /**
         * 添加要查询的 Rowkey
         * @param rowkey rowkey
         * @return {@link EveGet.Builder}
         */
        public Builder addRowkey(String rowkey) {
            this.rowkeys.add(rowkey);
            return this;
        }

        /**
         * 添加要查询的信息
         * @param family 待查询的列簇
         * @param qualifiers 待查询的列簇字段名
         * @return {@link EveGet.Builder}
         */
        public Builder select(@Nonnull String family, @Nonnull String... qualifiers) {
            Set<String> set = columnMap.computeIfAbsent(family, k -> new HashSet<>());
            set.addAll(Arrays.asList(qualifiers));
            return this;
        }

        /**
         * 完成Get构造
         * @return {@link EveGet}
         */
        public EveGet build() {
            return EveGetDirector.create(table, rowkeys, columnMap);
        }

    }

}
