package com.skey.evehbase;

import com.skey.evehbase.bean.Person;
import com.skey.evehbase.client.HBaseClient;
import com.skey.evehbase.client.EveHBase;
import com.skey.evehbase.pool.PoolEngine;
import com.skey.evehbase.request.*;
import com.skey.evehbase.security.SecurityConf;
import com.skey.evehbase.util.HResultUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.io.compress.Compression;
import org.apache.hadoop.hbase.io.encoding.DataBlockEncoding;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Descr
 * <p>
 * Date: 2019/1/11 9:40
 *
 * @author A Lion~
 */
public class GetTest {

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

        String[] qualifiers = {"address", "phone", "name", "age", "lac"};
        EveGet eveGet = new EveGet.Builder()
                .table("0_20180908_test")
                .addRowkey("wwgq91e_1536336409_fc4db9cf4c1bc4d1")
                .addRowkey("wwgq9my_1536363082_273499a8c69fb9c3")
                .select("f", qualifiers)
                .build();

        // 阻塞式
//        try {
//            List<Person> personList = client.get(eveGet, Person.class);
//            System.out.println("person = " + personList);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        client.close();

        // 异步式
//        client.getAsync(eveGet, new ResultCallback<Result>() {
//            @Override
//            public void onSuccessful(List<Result> results) {
//                Person person = HResultUtils.parse(results.get(0), Person.class);
//                System.out.println("person = " + person);
//
//                client.close();
//            }
//
//            @Override
//            public void onFailed(Exception e) {
//                System.out.println("e = " + e);
//                e.printStackTrace();
//
//                client.close();
//            }
//        });

        client.getAsync(eveGet, new ResultCallback<Person>() {
            @Override
            public void onSuccessful(List<Person> results) {
                for (Person result : results) {
                    System.out.println("result = " + result);
                }

                client.close();
            }

            @Override
            public void onFailed(Exception e) {
                System.out.println("e = " + e);
                e.printStackTrace();

                client.close();
            }
        });

        PoolEngine.INSTANCE.shutdown();

//        client.close();


//        EveTable eveTable = new EveTable.Builder()
//                .table("tb_test")
//                .familys("family01")
//                .encoding(DataBlockEncoding.FAST_DIFF)
//                .compression(Compression.Algorithm.SNAPPY)
//                .build();
//
//        client.create(eveTable);
//
//        String[] splitKeys = {"aaa", "ddd", "hhh", "vvv"};
//        client.multiSplit("tb_test", splitKeys);
//
//        client.createIndex(
//                "tb_test",
//                "family01",
//                "phone",
//                "tb_test_phone_idx");
//
////        String[] qualifiers = {"address", "phone", "name", "age", "lac"};
//
//        EveScan eveScan = new EveScan.Builder()
//                .table("tb_test")
//                .startRow("aaa")
//                .endRow("hhh")
////                .filter(...)
//                .select("f", qualifiers)
//                .build();
//        try {
//            List<Person> personList = client.scan(eveScan, Person.class);
//            System.out.println("personList = " + personList);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        client.scanAsync(eveScan, new ResultCallback<Person>() {
//            @Override
//            public void onSuccessful(List<Person> results) {
//                for (Person result : results) {
//                    System.out.println("result = " + result);
//                }
//            }
//
//            @Override
//            public void onFailed(Exception e) {
//
//            }
//        });
//
//        List<Put> putList = new ArrayList<>();
//        for (int i = 0; i < 5; i++) {
//            Put put = new Put(Bytes.toBytes("ggg_132131" + i));
//            put.addColumn(Bytes.toBytes("f"), Bytes.toBytes("phone"), Bytes.toBytes("1891234567"+ i));
//            putList.add(put);
//        }
//        try {
//            client.put("tb_test", putList);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        client.putAsync("tb_test", putList, new PutCallback() {
//            @Override
//            public void onSuccessful() {
//                System.out.println("GetTest.onSuccessful 提交Put成功!");
//            }
//
//            @Override
//            public void onFailed(Exception e) {
//                System.out.println("GetTest.onFailed 提交Put失败!");
//            }
//        });
//        client.putAsync("tb_test", putList, null);
    }
}
