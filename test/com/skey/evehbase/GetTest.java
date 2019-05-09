package com.skey.evehbase;

import com.skey.evehbase.bean.Person;
import com.skey.evehbase.client.HBaseClient;
import com.skey.evehbase.client.EveHBase;
import com.skey.evehbase.pool.PoolEngine;
import com.skey.evehbase.request.EveGet;
import com.skey.evehbase.request.ResultCallback;
import com.skey.evehbase.security.SecurityConf;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;

import java.io.File;
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
////            Result result = client.get(eveGet, Result.class);
////            Person person = HResultUtils.parse(result, Person.class);
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
    }
}
