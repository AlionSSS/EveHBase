package com.skey.evehbase.client;

import com.skey.evehbase.pool.PoolConf;
import com.skey.evehbase.pool.PoolEngine;
import com.skey.evehbase.request.*;
import com.skey.evehbase.security.SecurityConf;
import com.skey.evehbase.util.GenericUtils;
import com.skey.evehbase.util.HResultUtils;
import com.skey.evehbase.util.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.index.ColumnQualifier;
import org.apache.hadoop.hbase.index.Constants;
import org.apache.hadoop.hbase.index.IndexSpecification;
import org.apache.hadoop.hbase.index.client.IndexAdmin;
import org.apache.hadoop.hbase.index.coprocessor.master.IndexMasterObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * HBase客户端
 * <pre>{@code
 * // HBase配置
 * String confDir =
 *         System.getProperty("user.dir") + File.separator + "conf" + File.separator;
 * Configuration hbaseConf = HBaseConfiguration.create();
 * hbaseConf.addResource(new Path(confDir + "core-site.xml"));
 * hbaseConf.addResource(new Path(confDir + "hdfs-site.xml"));
 * hbaseConf.addResource(new Path(confDir + "hbase-site.xml"));
 *
 * // 安全认证配置
 * String kerberosDir =
 *         System.getProperty("user.dir") + File.separator + "kerberos" + File.separator;
 * SecurityConf securityConf = new SecurityConf(
 *         "cqtest",
 *         kerberosDir + "user.keytab",
 *         kerberosDir + "krb5.conf");
 *
 * // 创建HBase客户端
 * HBaseClient client = new EveHBase.Builder()
 *         .config(hbaseConf)
 *         .enableSafeSupport(securityConf)
 *         .build();
 * }</pre>
 * Date: 2019/1/11 10:17
 *
 * @author A Lion~
 */
public class EveHBase implements HBaseClient {

    private static final Logger LOG = LoggerFactory.getLogger(EveHBase.class);

    /**
     * HBase连接
     */
    private Connection conn;

    EveHBase(Connection conn) {
        this.conn = conn;
    }

    @Override
    public Connection getConn() {
        return conn;
    }

    @Override
    public void close() {
        IOUtils.close(conn);
    }

    @Override
    public TableName[] list() {
        TableName[] tableNames = new TableName[0];
        try {
            tableNames = conn.getAdmin().listTableNames();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tableNames;
    }

    @Override
    public void create(@Nonnull EveTable eveTable) {
        if (LOG.isInfoEnabled()) LOG.info("Entering create.");

        TableName tn = eveTable.getTableName();
        HTableDescriptor htd = eveTable.getHtd();

        Admin admin = null;
        try {
            admin = conn.getAdmin();
            if (!admin.tableExists(tn)) {
                if (LOG.isInfoEnabled()) LOG.info("Creating table...");
                admin.createTable(htd);
                if (LOG.isInfoEnabled()) {
                    LOG.info("ClusterStatus: ", admin.getClusterStatus());
                    LOG.info("NamespaceDescriptors: ", (Object[]) admin.listNamespaceDescriptors());
                    LOG.info("Table created successfully.");
                }
            } else {
                if (LOG.isWarnEnabled()) LOG.warn("table already exists");
            }
        } catch (IOException e) {
            if (LOG.isErrorEnabled()) LOG.error("Create table failed.", e);
        } finally {
            close(admin, "Close admin failed ");
        }
        if (LOG.isInfoEnabled()) LOG.info("Exiting create.");
    }

    @Override
    public void multiSplit(@Nonnull String tableName, @Nonnull String... splitKeys) {
        if (LOG.isInfoEnabled()) LOG.info("Entering multiSplit.");

        TableName tn = TableName.valueOf(tableName);
        Table table = null;
        Admin admin = null;
        try {
            admin = conn.getAdmin();
            table = conn.getTable(tn);

            // 获取Region信息
            Set<HRegionInfo> regionSet = new HashSet<HRegionInfo>();
            List<HRegionLocation> regionList = conn.getRegionLocator(tn).getAllRegionLocations();
            for (HRegionLocation hrl : regionList) {
                regionSet.add(hrl.getRegionInfo());
            }

            // key 转 byte
            byte[][] sk = new byte[splitKeys.length][];
            for (int i = 0; i < splitKeys.length - 1; i++) {
                sk[i - 1] = splitKeys[i].getBytes();
            }

            for (HRegionInfo regionInfo : regionSet) {
                ((HBaseAdmin) admin).multiSplit(regionInfo.getRegionName(), sk);
            }
            if (LOG.isInfoEnabled()) LOG.info("MultiSplit successfully.");
        } catch (Exception e) {
            if (LOG.isErrorEnabled()) LOG.error("MultiSplit failed ", e);
        } finally {
            close(table, "Close table failed ");
            close(admin, "Close admin failed ");
        }
        if (LOG.isInfoEnabled()) LOG.info("Exiting multiSplit.");
    }

    @Override
    public void createIndex(@Nonnull String tableName, @Nonnull String familyName,
                            @Nonnull String qualifier, String indexName) {
        if (LOG.isInfoEnabled()) LOG.info("Entering createIndex.");

        TableName tn = TableName.valueOf(tableName);

        // 创建索引实例
        if (indexName == null || indexName.trim().equals("")) {
            indexName = tableName + "_" + familyName + "_" + qualifier + "_idx";
        }
        IndexSpecification iSpec = new IndexSpecification(indexName);

        iSpec.addIndexColumn(new HColumnDescriptor(familyName), qualifier, ColumnQualifier.ValueType.String);

        IndexAdmin iAdmin = null;
        Admin admin = null;
        try {
            iAdmin = new IndexAdmin(conn.getConfiguration());
            iAdmin.addIndex(tn, iSpec);
            admin = conn.getAdmin();

            admin.disableTable(tn);

            HTableDescriptor htd = admin.getTableDescriptor(tn);
            // 实例化索引列描述
            HColumnDescriptor indexColDesc = new HColumnDescriptor(IndexMasterObserver.DEFAULT_INDEX_COL_DESC);

            // 设置索引描述到表描述
            htd.setValue(Constants.INDEX_COL_DESC_BYTES, indexColDesc.toByteArray());
            admin.modifyTable(tn, htd);

            admin.enableTable(tn);

            if (LOG.isInfoEnabled()) LOG.info("Create index successfully.");
        } catch (IOException e) {
            if (LOG.isErrorEnabled()) LOG.error("Create index failed.", e);
        } finally {
            close(admin, "Close admin failed ");
            close(iAdmin, "Close admin failed ");
        }
        if (LOG.isInfoEnabled()) LOG.info("Exiting createIndex.");
    }

    @Override
    public void putAsync(@Nonnull String tableName, @Nonnull List<Put> putList, PutCallback callback) {
        PoolEngine.getInstance().execute(() -> {
            try {
                put(tableName, putList);
                if (callback != null) callback.onSuccessful();
            } catch (IOException e) {
                if (callback != null) callback.onFailed(e, putList);
            }
        });
    }

    @Override
    public void put(@Nonnull String tableName, @Nonnull Put put) throws IOException {
        put(tableName, Collections.singletonList(put));
    }

    @Override
    public void put(@Nonnull String tableName, @Nonnull List<Put> putList) throws IOException {
        if (LOG.isInfoEnabled()) LOG.info("Entering put.");

        Table table = null;
        try {
            table = conn.getTable(TableName.valueOf(tableName));

            table.put(putList);

            if (LOG.isInfoEnabled()) LOG.info("Put successfully.");
        } finally {
            close(table, "Close table failed ");
        }
        if (LOG.isInfoEnabled()) LOG.info("Exiting put.");
    }

    @Override
    public <T> List<T> scan(@Nonnull EveScan eveScan, @Nonnull Class<T> clazz) throws IOException {
        if (LOG.isInfoEnabled()) LOG.info("Entering ColumnValueFilter.");

        ArrayList<Result> results = new ArrayList<>();

        Table table = null;
        ResultScanner scanner = null;
        try {
            table = conn.getTable(eveScan.getTableName());

            // 提交scan请求
            scanner = table.getScanner(eveScan.getScan());
            int i = 0;
            long max = eveScan.getMax();
            Result r;
            while ((r = scanner.next()) != null && i <= max) {
                results.add(r);
                i++;
            }

            if (LOG.isInfoEnabled()) LOG.info("Column value filter successfully.");
        } finally {
            close(scanner, "Close scanner failed ");
            close(table, "Close table failed ");
        }

        List<T> objects = HResultUtils.parseResults(results, clazz);

        if (LOG.isInfoEnabled()) LOG.info("Exiting ColumnValueFilter.");
        return objects;
    }

    @Override
    public <T> void scanAsync(@Nonnull EveScan eveScan, @Nonnull ResultCallback<T> callback) {
        PoolEngine.getInstance().execute(() -> {
            try {
                Class<T> clazz = (Class<T>) GenericUtils.getInterfaceT(callback)[0];
                List<T> result = scan(eveScan, clazz);
                callback.onSuccessful(result);
            } catch (IOException e) {
                callback.onFailed(e);
            }
        });
    }

    @Override
    public <T> List<T> get(@Nonnull EveGet eveGet, @Nonnull Class<T> clazz) throws IOException {
        if (LOG.isInfoEnabled()) LOG.info("Entering Get.");

        ArrayList<Result> results = new ArrayList<>();

        Table table = null;
        try {
            table = conn.getTable(eveGet.getTableName());

            // 提交Get请求
            for (Get get : eveGet.getGets()) {
                results.add(table.get(get));
            }

            if (LOG.isInfoEnabled()) LOG.info("Get data successfully.");
        } finally {
            close(table, "Close table failed ");
        }

        List<T> objects = HResultUtils.parseResults(results, clazz);

        if (LOG.isInfoEnabled()) LOG.info("Exiting testGet.");
        return objects;
    }

    @Override
    public <T> void getAsync(@Nonnull EveGet eveGet, @Nonnull ResultCallback<T> callback) {
        PoolEngine.getInstance().execute(() -> {
            try {
                Class<T> clazz = (Class<T>) GenericUtils.getInterfaceT(callback)[0];
                List<T> results = get(eveGet, clazz);
                callback.onSuccessful(results);
            } catch (IOException e) {
                callback.onFailed(e);
            }
        });
    }

    /**
     * 关闭工具，失败后打印错误信息
     *
     * @param closeable 可关闭的接口
     * @param errorMsg  错误信息
     */
    private void close(Closeable closeable, String errorMsg) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                if (LOG.isErrorEnabled()) LOG.error(errorMsg, e);
            }
        }
    }

    public static class Builder {

        private Configuration conf;

        private SecurityConf securityConf;

        private HashMap<String, String> confMap = new HashMap<>();

        private PoolConf poolConf;

        /**
         * 直接使用HBase的Configuration对象
         *
         * @param conf org.apache.hadoop.conf.Configuration
         * @return {@link EveHBase.Builder}
         */
        public Builder config(Configuration conf) {
            this.conf = conf;
            return this;
        }

        /**
         * 添加KV对配置，会覆盖已有的配置
         *
         * @param name  key
         * @param value value
         * @return {@link EveHBase.Builder}
         */
        public Builder config(String name, String value) {
            this.confMap.put(name, value);
            return this;
        }

        /**
         * 启用安全认证支持
         *
         * @param securityConf 安全认证配置
         * @return {@link EveHBase.Builder}
         */
        public Builder enableSafeSupport(SecurityConf securityConf) {
            this.securityConf = securityConf;
            return this;
        }

        /**
         * 设置线程池配置
         *
         * @param core          核心数
         * @param max           最大线程数
         * @param keepAlive     多少时间后关闭多余线程, 单位:毫秒
         * @param queueCapacity 队列容量
         * @return {@link EveHBase.Builder}
         */
        public Builder pool(int core, int max, int keepAlive, int queueCapacity) {
            poolConf = new PoolConf(core, max, keepAlive, queueCapacity);
            return this;
        }

        public EveHBase build() {
            return HBaseClientDirector.create(conf, confMap, securityConf, poolConf);
        }

    }

}
