package com.skey.evehbase.security;

import java.util.Objects;

/**
 * HBase安全认证配置
 * <p>
 * Date: 2019/1/11 11:45
 *
 * @author A Lion~
 */
public class SecurityConf {

    public static final String ZOOKEEPER_DEFAULT_LOGIN_CONTEXT_NAME = "Client";

    public static final String ZOOKEEPER_SERVER_PRINCIPAL_KEY = "zookeeper.server.principal";

    public static final String ZOOKEEPER_DEFAULT_SERVER_PRINCIPAL = "zookeeper/hadoop";

    private String krb5File = null;

    private String userName = null;

    private String userKeytabFile = null;

    public SecurityConf() {
    }

    public SecurityConf(String userName, String userKeytabFile, String krb5File) {
        requireNonNull(userName, userKeytabFile, krb5File);
        this.userName = userName;
        this.userKeytabFile = userKeytabFile;
        this.krb5File = krb5File;
    }

    public String getKrb5File() {
        return krb5File;
    }

    public void setKrb5File(String krb5File) {
        requireNonNull(krb5File);
        this.krb5File = krb5File;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        requireNonNull(userName);
        this.userName = userName;
    }

    public String getUserKeytabFile() {
        return userKeytabFile;
    }

    public void setUserKeytabFile(String userKeytabFile) {
        requireNonNull(userKeytabFile);
        this.userKeytabFile = userKeytabFile;
    }

    /**
     * 检查非空
     * @param objects 对象
     */
    private void requireNonNull(Object... objects) {
        for (Object obj : objects) {
            Objects.requireNonNull(obj, "所有安全认证配置都不能为null");
        }
    }

}
