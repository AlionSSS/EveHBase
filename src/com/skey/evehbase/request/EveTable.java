package com.skey.evehbase.request;

import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.io.compress.Compression;
import org.apache.hadoop.hbase.io.encoding.DataBlockEncoding;

import javax.annotation.Nonnull;

/**
 * HBase的建表请求构造
 * <p>
 * Date: 2019/2/28 9:24
 *
 * @author A Lion~
 */
public class EveTable {

    private final TableName tableName;

    private final HTableDescriptor htd;

    EveTable(TableName tableName, HTableDescriptor htd) {
        this.tableName = tableName;
        this.htd = htd;
    }

    public TableName getTableName() {
        return tableName;
    }

    public HTableDescriptor getHtd() {
        return htd;
    }

    public static class Builder {

        private String table;

        private DataBlockEncoding encoding;

        private Compression.Algorithm algorithm;

        private String[] familyNames;

        /**
         * 制定建表的表名
         *
         * @param table 表名
         * @return {@link EveTable.Builder}
         */
        public Builder table(@Nonnull String table) {
            this.table = table;
            return this;
        }

        /**
         * 需要创建的列簇的名称
         *
         * @param familyNames 列簇名称...
         * @return {@link EveTable.Builder}
         */
        public Builder familys(@Nonnull String... familyNames) {
            this.familyNames = familyNames;
            return this;
        }

        /**
         * 制定建表的编码
         *
         * @param encoding {@link DataBlockEncoding}
         * @return {@link EveTable.Builder}
         */
        public Builder encoding(DataBlockEncoding encoding) {
            this.encoding = encoding;
            return this;
        }

        /**
         * 制定建表的压缩算法
         *
         * @param algorithm 压缩算法 {@link Compression.Algorithm}
         * @return {@link EveTable.Builder}
         */
        public Builder compression(Compression.Algorithm algorithm) {
            this.algorithm = algorithm;
            return this;
        }

        /**
         * 完成Table构造
         *
         * @return {@link EveTable}
         */
        public EveTable build() {
            return EveTableDirector.create(table, familyNames, encoding, algorithm);
        }

    }

}
