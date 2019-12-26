# EveHBase
## Introductions
- 针对华为HBase封装的客户端EveHBase
- 支持普通客户端、安全模式客户端，
- 功能包括：建表、预分区、建索引、异步请求、Put、Get、Scan、结果自动解析为JavaBean等

## Dependencies
- 本项目EveHBase.jar ---> 从[releases](https://github.com/AlionSSS/EveHBase/releases)处获取
- 华为HBase一系列依赖包 ---> 从华为HBase集群中获取

## Examples
- 创建普通客户端
```java
// HBase配置
// 需从HBase的配置文件目录中分别下载core-site.xml,hdfs-site.xml,hbase-site.xml
Configuration hbaseConf = HBaseConfiguration.create();
hbaseConf.addResource(new Path("./conf/core-site.xml"));
hbaseConf.addResource(new Path("./conf/hdfs-site.xml"));
hbaseConf.addResource(new Path("./conf/hbase-site.xml"));

// 构建客户端
// 当3个配置文件存在项目根目录下时，可以不指定config
HBaseClient client = new EveHBase.Builder()
        .config(hbaseConf)
        .build();
```
- 创建安全模式客户端
```java
// HBase配置
// ……

// 安全认证配置
// 需从集群管理界面下载对应用户的kerberos文件
SecurityConf securityConf = new SecurityConf(
        "test",
        "./kerberos/user.keytab",
        "./kerberos/krb5.conf");

// 构建安全模式客户端
HBaseClient client = new EveHBase.Builder()
        .config(hbaseConf)
        .enableSafeSupport(securityConf)
        .build();

```
- 自定义线程池
```java
ExecutorServiceAdapter adapter = new ExecutorServiceAdapter() {
            @Override
            public ExecutorService generateExecutorService() {
                return Executors.newFixedThreadPool(4);
            }
        };
HBaseClient client = new EveHBase.Builder()
        .config(hbaseConf)
        .pool(adapter)
        .build();
```
- 建表
```java
EveTable eveTable = new EveTable.Builder()
        .table("tb_test")
        .familys("family01")
        .encoding(DataBlockEncoding.FAST_DIFF)
        .compression(Compression.Algorithm.SNAPPY)
        .build();

client.create(eveTable);
```
- 关闭并删除表
```java
client.disableAndDelete(eveTable);
```
- 预分区
```java
String[] splitKeys = {"aaa", "ddd", "hhh", "vvv"};
client.multiSplit("tb_test", splitKeys);
```
- 创建索引
```java
client.createIndex(
    "tb_test", 
    "family01",
    "phone",
    "tb_test_phone_idx");
```
- 为需要自动解析的JavaBean添加注解
```java
public class Person {

    @RowKey
    public String rowkey;

    // 列簇, 对应family 
    @ColumnFamily
    public Info f;

    public class Info {

        // 列簇中的字段, 对应qualifier
        public String address;

        public String phone;

        public String name;

        public String age;
        
    }
    
}
```
- 执行Get同步请求
```java
String[] qualifiers = {"address", "phone", "name", "age", "lac"};
EveGet eveGet = new EveGet.Builder()
        .table("tb_test")
        .addRowkey("ccc_21312312")
        .addRowkey("hhh_24242348")
        .select("f", qualifiers)
        .build();

try {
    List<Person> personList = client.get(eveGet, Person.class);
    System.out.println("person = " + personList);
} catch (IOException e) {
    e.printStackTrace();
}
```
- 执行Get异步请求
```java
client.getAsync(eveGet, new ResultCallback<Person>() {
    @Override
    public void onSuccessful(List<Person> results) {
        for (Person result : results) {
            System.out.println("result = " + result);
        }
    }

    @Override
    public void onFailed(Exception e) {
        System.out.println("e = " + e);
        e.printStackTrace();
    }
});
```
- 如果不需要自动解析JavaBean, 那么可以这样做
```java
// 传入Result.class将返回HBase原生的结果
List<Result> personList = client.get(eveGet, Result.class);
```
- 执行Scan同步请求
```java
String[] qualifiers = {"address", "phone", "name", "age", "lac"};

EveScan eveScan = new EveScan.Builder()
        .table("tb_test")
        .startRow("aaa")
        .endRow("hhh")
//      .filter(...)
        .select("f", qualifiers)
        .build();
try {
    List<Person> personList = client.scan(eveScan, Person.class);
    System.out.println("personList = " + personList);
} catch (IOException e) {
    e.printStackTrace();
}
```
- 执行Scan异步请求
```java
client.scanAsync(eveScan, new ResultCallback<Person>() {
    @Override
    public void onSuccessful(List<Person> results) {
        for (Person result : results) {
            System.out.println("result = " + result);
        }
    }

    @Override
    public void onFailed(Exception e) {

    }
});
```
- 提交Put同步请求
```java
List<Put> putList = new ArrayList<>();
for (int i = 0; i < 5; i++) {
    Put put = new Put(Bytes.toBytes("ggg_132131" + i));
    put.addColumn(Bytes.toBytes("f"), Bytes.toBytes("phone"), Bytes.toBytes("1891234567"+ i));
    putList.add(put);
}
try {
    client.put("tb_test", putList);
} catch (IOException e) {
    e.printStackTrace();
}
```
- 提交Put异步请求
```java
client.putAsync("tb_test", putList, new PutCallback() {
    @Override
    public void onSuccessful() {
        System.out.println("GetTest.onSuccessful 提交Put成功!");
    }

    @Override
    public void onFailed(Exception e, List<Put> puts) {
        System.out.println("GetTest.onFailed 提交Put失败!");
        System.out.println("失败的 puts = " + puts);
    }
});
```
- 提交Put异步请求, 不要回调
```java
client.putAsync("tb_test", putList, null);
```

## LICENSE
```
Copyright 2019 ALion

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

	http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
