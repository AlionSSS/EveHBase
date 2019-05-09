package com.skey.evehbase;

import com.skey.evehbase.bean.Person;
import com.skey.evehbase.client.HBaseClient;
import com.skey.evehbase.client.EveHBase;
import com.skey.evehbase.security.SecurityConf;
import com.skey.evehbase.util.HResultUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Result;

import java.io.File;
import java.util.List;

/**
 * Descr
 * <p>
 * Date: 2019/1/11 9:40
 *
 * @author A Lion~
 */
public class Demo01Test {

    public static void main(String[] args) {
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



        client.close();
    }
}
