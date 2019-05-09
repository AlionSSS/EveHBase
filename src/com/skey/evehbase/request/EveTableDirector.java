package com.skey.evehbase.request;

import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.io.compress.Compression;
import org.apache.hadoop.hbase.io.encoding.DataBlockEncoding;

import javax.annotation.Nonnull;

/**
 * 生成EveGet的导演类
 * <p>
 * Date: 2019/2/16 14:05
 *
 * @author A Lion~
 */
class EveTableDirector {

    private EveTableDirector() {
        throw new AssertionError(this + "不应该被实例化！");
    }

    /**
     * 按流程生成EveTable
     */
    static EveTable create(String table, String[] familyNames,
                           DataBlockEncoding encoding, Compression.Algorithm algorithm) {
        TableName tn = TableName.valueOf(table);

        HTableDescriptor htd = generateHTD(tn, familyNames, encoding, algorithm);

        return new EveTable(tn, htd);
    }

    /**
     * 生成表描述器
     * @param tn 表名
     * @param familyNames 列簇名
     * @param encoding 编码
     * @param algorithm 压缩算法
     * @return htd {@link HTableDescriptor}
     */
    private static HTableDescriptor generateHTD(TableName tn, String[] familyNames,
                                                DataBlockEncoding encoding, Compression.Algorithm algorithm) {
        HTableDescriptor htd = new HTableDescriptor(tn);
        for (String familyName : familyNames) {
            HColumnDescriptor hcd = new HColumnDescriptor(familyName);
            hcd.setDataBlockEncoding(encoding);
            if (algorithm != null) hcd.setCompressionType(algorithm);
            htd.addFamily(hcd);
        }
        return htd;
    }


}
