package com.skey.evehbase.client;

import com.skey.evehbase.request.*;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.Put;

import java.io.IOException;
import java.util.List;

/**
 * HBase客户端统一化接口
 * <p>
 * Date: 2019/1/15 16:02
 *
 * @author A Lion~
 */
public interface HBaseClient {

    /**
     * 获取HBase连接
     *
     * @return Connection
     */
    Connection getConn();

    /**
     * 关闭HBase连接
     */
    void close();

    /**
     * 列出库中的表
     * @return 表名数组
     */
    TableName[] list();

    /**
     * HBase建表
     *
     * @param eveTable {@link EveTable}
     */
    void create(EveTable eveTable);

    /**
     * 关闭表
     * @param eveTable {@link EveTable}
     */
    void disable(EveTable eveTable);

    /**
     * 启用表
     * @param eveTable {@link EveTable}
     */
    void enable(EveTable eveTable);

    /**
     * 删表
     * @param eveTable {@link EveTable}
     */
    void delete(EveTable eveTable);

    /**
     * 关闭表，并删除
     * @param eveTable {@link EveTable}
     */
    void disableAndDelete(EveTable eveTable);

    /**
     * 表Region拆分
     *
     * @param tableName 表名
     * @param splitKeys keys
     */
    void multiSplit(String tableName, String... splitKeys);

    /**
     * 创建索引
     *
     * @param tableName  表名
     * @param familyName 列族
     * @param qualifier  字段
     * @param indexName  索引名
     */
    void createIndex(String tableName, String familyName, String qualifier, String indexName);

    void put(String tableName, Put put) throws IOException;

    /**
     * 入库数据
     * @param tableName 表名
     * @param putList Put列表
     */
    void put(String tableName, List<Put> putList) throws IOException;

    /**
     * 异步入库数据
     * @param tableName 表名
     * @param putList Put列表
     * @param callback 回调接口
     */
    void putAsync(String tableName, List<Put> putList, PutCallback callback);

    /**
     * Scan请求 - 同步
     * @param eveScan {@link EveScan}
     * @return Result
     */
    <T> List<T> scan(EveScan eveScan, Class<T> clazz) throws IOException;

    /**
     * Scan请求 - 异步
     * @param eveScan {@link EveScan}
     * @param callback 参数回调 {@link ResultCallback}
     */
    <T> void scanAsync(EveScan eveScan, ResultCallback<T> callback);

    /**
     * Get请求 - 同步
     * @param eveGet {@link EveGet}
     * @return Results
     */
    <T> List<T> get(EveGet eveGet, Class<T> clazz) throws IOException;

    /**
     * Get请求 - 异步
     * @param eveGet {@link EveGet}
     * @param callback 参数回调 {@link ResultCallback}
     */
    <T> void getAsync(EveGet eveGet, ResultCallback<T> callback);

}
