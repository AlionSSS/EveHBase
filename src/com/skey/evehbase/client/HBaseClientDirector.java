package com.skey.evehbase.client;

import com.skey.evehbase.security.LoginUtil;
import com.skey.evehbase.security.SecurityConf;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.security.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 生成客户端的导演类
 * <p>
 * Date: 2019/1/28 10:10
 *
 * @author A Lion~
 */
public class HBaseClientDirector {

    private static final Logger LOG = LoggerFactory.getLogger(HBaseClientDirector.class);

    /**
     * 按流程生成HBaseClient
     *
     * @param conf         Configuration
     * @param confMap      额外的KV对配置
     * @param securityConf 安全配置
     * @return HBase客户端
     */
    static EveHBase create(Configuration conf, HashMap<String, String> confMap, SecurityConf securityConf) {
        Connection conn = null;
        try {
            Configuration fixedConf = fixConf(conf, confMap);
            if (securityConf != null) login(fixedConf, securityConf);
            conn = ConnectionFactory.createConnection(fixedConf);
        } catch (IOException e) {
            if (LOG.isErrorEnabled()) LOG.error("HBase连接异常", e);
        }
        return new EveHBase(conn);
    }

    /**
     * 检查HBase的配置，并修正设置
     * <p>
     * 如果没有，就去资源目录下找
     * core-site.xml,hdfs-site.xml,hbase-site.xml
     *
     * @param conf    Configuration
     * @param confMap 额外的KV对配置
     * @return Configuration
     */
    private static Configuration fixConf(Configuration conf, HashMap<String, String> confMap) {
        if (conf == null) {
            if (LOG.isInfoEnabled()) LOG.info("不存在Configuration, 开始创建Configuration.");
            // 如果conf为null，HBase就会去资源目录下找配置文件
            // core-site.xml,hdfs-site.xml,hbase-site.xml
            conf = HBaseConfiguration.create();
        }

        // 添加配置
        if (confMap != null) {
            for (Map.Entry<String, String> entry : confMap.entrySet()) {
                conf.set(entry.getKey(), entry.getValue());
            }
        }

        return conf;
    }

    /**
     * Kerbos认证登录
     *
     * @param conf         HBase的Configuration
     * @param securityConf 安全认证配置
     * @throws IOException
     */
    private static void login(Configuration conf, SecurityConf securityConf) throws IOException {
        if (User.isHBaseSecurityEnabled(conf)) {
            LoginUtil.setJaasConf(
                    SecurityConf.ZOOKEEPER_DEFAULT_LOGIN_CONTEXT_NAME,
                    securityConf.getUserName(),
                    securityConf.getUserKeytabFile());

            LoginUtil.setZookeeperServerPrincipal(
                    SecurityConf.ZOOKEEPER_SERVER_PRINCIPAL_KEY,
                    SecurityConf.ZOOKEEPER_DEFAULT_SERVER_PRINCIPAL);

            LoginUtil.login(
                    securityConf.getUserName(),
                    securityConf.getUserKeytabFile(),
                    securityConf.getKrb5File(),
                    conf);
        }
    }

}
