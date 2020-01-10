package com.skey.evehbase;

import com.skey.evehbase.client.EveHBase;
import com.skey.evehbase.client.HBaseClient;
import com.skey.evehbase.security.SecurityConf;
import com.skey.evehbase.util.PutBuffer;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;

/**
 * Description:
 * <br/>
 * Date: 2020/1/10 19:04
 *
 * @author ALion
 */
public class PutBufferTest {

    public static void main(String[] args) throws IOException {
        // HBase配置
        Configuration hbaseConf = HBaseConfiguration.create();
        hbaseConf.addResource(new Path("./conf/core-site.xml"));
        hbaseConf.addResource(new Path("./conf/hdfs-site.xml"));
        hbaseConf.addResource(new Path("./conf/hbase-site.xml"));

        // 安全认证配置
        SecurityConf securityConf = new SecurityConf(
                "test",
                "./kerberos/user.keytab",
                "./kerberos/krb5.conf");

        // 构建客户端
        HBaseClient client = new EveHBase.Builder()
                .config(hbaseConf)
                .enableSafeSupport(securityConf)
                .build();

        PutBuffer buffer = client.createPutBuffer("test_table", 1000, 5000);
        for (int i = 0; i < 100_000; i++) {
            Put put = new Put(Bytes.toBytes("ggg_132131" + i));
            put.addColumn(Bytes.toBytes("f"), Bytes.toBytes("phone"), Bytes.toBytes("1891234567" + i));

            buffer.put(put);
        }
        buffer.flush();


        client.close();
    }

}
