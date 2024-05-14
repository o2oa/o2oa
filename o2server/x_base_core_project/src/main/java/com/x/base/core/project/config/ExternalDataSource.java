package com.x.base.core.project.config;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.factory.SlicePropertiesBuilder;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.tools.Crypto;

public class ExternalDataSource extends ConfigObject {

    private static final long serialVersionUID = -2098883883740436217L;

	// 无需保存
    private transient String _password;

    public ExternalDataSource() {
    }

    public static final Boolean DEFAULT_ENABLE = false;

    public static final String DEFAULT_URL = "jdbc:mysql://127.0.0.1:3306/X?autoReconnect=true&allowPublicKeyRetrieval=true&useSSL=false&useUnicode=true&characterEncoding=UTF-8&useLegacyDatetimeCode=false&serverTimezone=GMT%2B8";

    public static final String DEFAULT_USERNAME = "root";

    public static final String DEFAULT_PASSWORD = "password";

    public static final String DEFAULT_DRIVERCLASSNAME = "";

    public static final String DEFAULT_DICTIONARY = "";

    public static final Integer DEFAULT_MAXTOTAL = 100;

    public static final Integer DEFAULT_MAXIDLE = 0;

    public static final Boolean DEFAULT_STATENABLE = true;

    public static final String DEFAULT_STATFILTER = "mergeStat";

    private static final Boolean DEFAULT_SLOWSQLENABLE = true;

    private static final Integer DEFAULT_SLOWSQLTHRESHOLD = 3000;

    private static final Boolean DEFAULT_LOGSTATENABLE = false;

    private static final Integer DEFAULT_LOGSTATINTERVAL = 180;

    public static final List<String> DEFAULT_INCLUDES = new ArrayList<>();

    public static final List<String> DEFAULT_EXCLUDES = new ArrayList<>();

    public static final String DEFAULT_LOGLEVEL = "ERROR";

    public static final String DEFAULT_TRANSACTIONISOLATION = "read-committed";

    public static final Boolean DEFAULT_TESTCONNECTIONONCHECKIN = false;

    public static final Boolean DEFAULT_TESTCONNECTIONONCHECKOUT = false;

    public static final Integer DEFAULT_MAXIDLETIME = 300;

    public static final Boolean DEFAULT_AUTOCOMMIT = false;
    // 20240428修改为空值,取消默认的SCHEMA这样可以手工适陪各种postgres变种版本.
    // public static final String DEFAULT_SCHEMA = "X";
    public static final String DEFAULT_SCHEMA = "";

    public static ExternalDataSource defaultInstance() {

        ExternalDataSource o = new ExternalDataSource();
        o.enable = DEFAULT_ENABLE;
        o.url = DEFAULT_URL;
        o.username = DEFAULT_USERNAME;
        o.password = DEFAULT_PASSWORD;
        o.driverClassName = DEFAULT_DRIVERCLASSNAME;
        o.dictionary = DEFAULT_DICTIONARY;
        o.maxTotal = DEFAULT_MAXTOTAL;
        o.maxIdle = DEFAULT_MAXIDLE;
        o.statEnable = DEFAULT_STATENABLE;
        o.statFilter = DEFAULT_STATFILTER;
        o.slowSqlEnable = DEFAULT_SLOWSQLENABLE;
        o.slowSqlThreshold = DEFAULT_SLOWSQLTHRESHOLD;
        o.logStatEnable = DEFAULT_LOGSTATENABLE;
        o.logStatInterval = DEFAULT_LOGSTATINTERVAL;
        o.includes = DEFAULT_INCLUDES;
        o.excludes = DEFAULT_EXCLUDES;
        o.logLevel = DEFAULT_LOGLEVEL;
        o.transactionIsolation = DEFAULT_TRANSACTIONISOLATION;
        o.testConnectionOnCheckin = DEFAULT_TESTCONNECTIONONCHECKIN;
        o.testConnectionOnCheckout = DEFAULT_TESTCONNECTIONONCHECKOUT;
        o.maxIdleTime = DEFAULT_MAXIDLETIME;
        o.autoCommit = DEFAULT_AUTOCOMMIT;
        return o;
    }

    @FieldDescribe("是否启用,如果启用多个数据库,那么必须是相同类型的,不能混用,且用户名密码必须一致.")
    private Boolean enable;
    @FieldDescribe("jdbc连接地址.")
    private String url;
    @FieldDescribe("数据库jdbc连接用户名.")
    private String username;
    @FieldDescribe("数据库jdbc连接密码.")
    private String password;
    @FieldDescribe("数据库驱动类名.")
    private String driverClassName;
    @FieldDescribe("方言类.")
    private String dictionary;
    @FieldDescribe("最大使用连接数.")
    private Integer maxTotal;
    @FieldDescribe("最大空闲连接数.")
    private Integer maxIdle;
    @FieldDescribe("启用统计,默认启用.")
    private Boolean statEnable;
    @FieldDescribe("统计方式配置,默认mergeStat.")
    private String statFilter;
    @FieldDescribe("设置此数据库存储的类,默认情况下存储所有类型,如果需要对每个类进行单独的控制以达到高性能,可以将不同的类存储到不同的节点上提高性能.可以使用通配符*.")
    private List<String> includes;
    @FieldDescribe("在此节点上不存储的类,和includes一起设置实际存储的类,可以使用通配符*.")
    private List<String> excludes;
    @FieldDescribe("默认日志级别,FATAL, ERROR, WARN, INFO, TRACE. 完整的配置为DefaultLevel=WARN, Tool=TRACE, Enhance=TRACE, METADATA=TRACE, Runtime=TRACE, Query=TRACE, DataCache=TRACE, JDBC=TRACE, SQL=TRACE.")
    private String logLevel = DEFAULT_LOGLEVEL;
    @FieldDescribe("事务隔离级别:default,none,read-uncommitted,read-committed,repeatable-read,serializable.默认使用default(数据库设置的事务级别).")
    private String transactionIsolation;
    @FieldDescribe("测试入池连接,默认false.")
    private Boolean testConnectionOnCheckin;
    @FieldDescribe("测试出池连接,默认false.")
    private Boolean testConnectionOnCheckout;
    @FieldDescribe("空闲阈值,默认300秒.")
    private Integer maxIdleTime;
    @FieldDescribe("自动提交,默认为false.")
    private Boolean autoCommit = DEFAULT_AUTOCOMMIT;
    @FieldDescribe("模式.")
    private String schema = DEFAULT_SCHEMA;
    @FieldDescribe("启用记录统计日志.")
    private Boolean logStatEnable;
    @FieldDescribe("统计日志输出间隔,单位分钟,默认180.")
    private Integer logStatInterval;
    @FieldDescribe("是否启用执行慢sql记录.")
    private Boolean slowSqlEnable;
    @FieldDescribe("执行慢sql记录阈值,毫秒数,默认3000毫秒.")
    private Integer slowSqlThreshold;

    public Boolean getLogStatEnable() {
        return BooleanUtils.isTrue(this.logStatEnable);
    }

    public Integer getLogStatInterval() {
        return (null == this.logStatInterval || this.logStatInterval < 1) ? DEFAULT_LOGSTATINTERVAL
                : this.logStatInterval;
    }

    public Boolean getSlowSqlEnable() {
        return BooleanUtils.isTrue(this.slowSqlEnable);
    }

    public Integer getSlowSqlThreshold() {
        return (null == this.slowSqlThreshold || this.slowSqlThreshold < 1) ? DEFAULT_SLOWSQLTHRESHOLD
                : this.slowSqlThreshold;
    }

    public Boolean getAutoCommit() {
        return (null == this.autoCommit) ? DEFAULT_AUTOCOMMIT : this.autoCommit;
    }

    public Integer getMaxIdleTime() {
        return maxIdleTime == null ? DEFAULT_MAXIDLETIME : this.maxIdleTime;
    }

    public Boolean getTestConnectionOnCheckin() {
        return this.testConnectionOnCheckin == null ? DEFAULT_TESTCONNECTIONONCHECKIN : this.testConnectionOnCheckin;
    }

    public Boolean getTestConnectionOnCheckout() {
        return this.testConnectionOnCheckout == null ? DEFAULT_TESTCONNECTIONONCHECKOUT : this.testConnectionOnCheckout;
    }

    public String getTransactionIsolation() {
        return StringUtils.isEmpty(this.transactionIsolation) ? DEFAULT_TRANSACTIONISOLATION
                : this.transactionIsolation;
    }

    public String getLogLevel() {
        return StringUtils.isEmpty(this.logLevel) ? DEFAULT_LOGLEVEL : this.logLevel;
    }

    public String getDriverClassName() throws Exception {
        return StringUtils.isEmpty(this.driverClassName) ? SlicePropertiesBuilder.driverClassNameOfUrl(this.url)
                : this.driverClassName;
    }

    public String getDictionary() throws Exception {
        return StringUtils.isEmpty(this.dictionary) ? SlicePropertiesBuilder.dictionaryOfUrl(this.url)
                : this.dictionary;
    }

    public String getStatFilter() {
        return StringUtils.isEmpty(this.statFilter) ? DEFAULT_STATFILTER : this.statFilter;
    }

    public Boolean getStatEnable() {
        return null == statEnable ? DEFAULT_STATENABLE : this.statEnable;
    }

    public Integer getMaxIdle() {
        if ((this.maxIdle == null) || (this.maxIdle < 1)) {
            return DEFAULT_MAXIDLE;
        } else {
            return this.maxIdle;
        }
    }

    public Integer getMaxTotal() {
        if ((this.maxTotal == null) || (this.maxTotal < 0)) {
            return DEFAULT_MAXTOTAL;
        } else {
            return this.maxTotal;
        }
    }

    public Boolean getEnable() {
        return null == enable ? DEFAULT_ENABLE : this.enable;
    }

    public String getUrl() {
        return StringUtils.isEmpty(this.url) ? DEFAULT_URL : this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        if (StringUtils.isEmpty(this._password)) {
            this._password = Crypto.plainText(this.password);
        }
        return this._password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<String> getIncludes() {
        return null == includes ? DEFAULT_INCLUDES : this.includes;
    }

    public void setIncludes(List<String> includes) {
        this.includes = includes;
    }

    public List<String> getExcludes() {
        return null == excludes ? DEFAULT_EXCLUDES : this.excludes;
    }

    public void setExcludes(List<String> excludes) {
        this.excludes = excludes;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    public void setDictionary(String dictionary) {
        this.dictionary = dictionary;
    }

    public void setMaxTotal(Integer maxTotal) {
        this.maxTotal = maxTotal;
    }

    public void setLogLevel(String logLevel) {
        this.logLevel = logLevel;
    }

    public String getSchema() {
        return StringUtils.isBlank(this.schema) ? DEFAULT_SCHEMA : this.schema;
    }

}
