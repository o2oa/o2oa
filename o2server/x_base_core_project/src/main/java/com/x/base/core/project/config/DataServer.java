package com.x.base.core.project.config;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.annotation.FieldDescribe;

public class DataServer extends ConfigObject {

    private static final long serialVersionUID = 77120835343101221L;

    private static final Integer DEFAULT_TCPPORT = 20050;
    private static final Integer DEFAULT_WEBPORT = null;
    private static final Integer DEFAULT_CACHESIZE = 512;
    private static final Boolean DEFAULT_JMXENABLE = false;
    private static final Integer DEFAULT_MAXTOTAL = 50;
    private static final Integer DEFAULT_MAXIDLE = 0;
    private static final Boolean DEFAULT_STATENABLE = true;
    private static final String DEFAULT_STATFILTER = "mergeStat";
    private static final Boolean DEFAULT_SLOWSQLENABLE = true;
    private static final Integer DEFAULT_SLOWSQLTHRESHOLD = 3000;
    private static final Boolean DEFAULT_LOGSTATENABLE = false;
    private static final Integer DEFAULT_LOGSTATINTERVAL = 180;
    private static final Integer DEFAULT_LOCKTIMEOUT = 120000;
    private static final String DEFAULT_LOGLEVEL = "WARN";

    public static DataServer defaultInstance() {
        return new DataServer();
    }

    public DataServer() {
        this.enable = true;
        this.tcpPort = DEFAULT_TCPPORT;
        this.webPort = DEFAULT_WEBPORT;
        this.includes = new ArrayList<>();
        this.excludes = new ArrayList<>();
        this.cacheSize = DEFAULT_CACHESIZE;
        this.jmxEnable = DEFAULT_JMXENABLE;
        this.maxTotal = DEFAULT_MAXTOTAL;
        this.maxIdle = DEFAULT_MAXIDLE;
        this.logLevel = DEFAULT_LOGLEVEL;
        this.statEnable = DEFAULT_STATENABLE;
        this.statFilter = DEFAULT_STATFILTER;
        this.slowSqlEnable = DEFAULT_SLOWSQLENABLE;
        this.slowSqlThreshold = DEFAULT_SLOWSQLTHRESHOLD;
        this.lockTimeout = DEFAULT_LOCKTIMEOUT;
        this.logStatEnable = DEFAULT_LOGSTATENABLE;
        this.logStatInterval = DEFAULT_LOGSTATINTERVAL;
    }

    @FieldDescribe("是否启用,如果没有可用的externalDataSources.json文件,那么默认会在节点中启用本地的H2数据库作为默认的数据库.")
    private Boolean enable;
    @FieldDescribe("H2数据库jdbc连接端口,登录的用户名:sa,密码为xadmin的密码.数据库创建在/o2server/local/repository/data/X.mv.db,一旦数据库文件被创建,那么该数据库的密码被创建.")
    private Integer tcpPort;
    @FieldDescribe("H2数据库web端口,H2提供一个web端的client,此端口为web端client的访问端口.用户名sa,密码为xadmin数据库初始创建的密码.")
    private Integer webPort;
    @FieldDescribe("设置此数据库存储的类,默认情况下存储所有类型,如果需要对每个类进行单独的控制以达到高性能,可以将不同的类存储到不同的节点上提高性能.可以使用通配符*")
    private List<String> includes;
    @FieldDescribe("在此节点上不存储的类,和includes一起设置实际存储的类,可以使用通配符*")
    private List<String> excludes;
    @FieldDescribe("是否启动jmx,如果启用,可以通过本地的jmx客户端进行访问,不支持远程jmx客户端.")
    private Boolean jmxEnable;
    @FieldDescribe("H2数据库缓存大小,设置H2用于作为缓存的内存大小,以M作为单位,这里默认为512M.")
    private Integer cacheSize;
    @FieldDescribe("默认日志级别,FATAL, ERROR, WARN, INFO, TRACE. 完整的配置为DefaultLevel=WARN, Tool=TRACE, Enhance=TRACE, METADATA=TRACE, Runtime=TRACE, Query=TRACE, DataCache=TRACE, JDBC=TRACE, SQL=TRACE")
    private String logLevel;
    @FieldDescribe("最大使用连接数")
    private Integer maxTotal;
    @FieldDescribe("最大空闲连接数")
    private Integer maxIdle;
    @FieldDescribe("启用统计,默认关闭")
    private Boolean statEnable;
    @FieldDescribe("统计方式配置,默认mergeStat")
    private String statFilter;
    @FieldDescribe("默认锁超时时间毫秒).")
    private Integer lockTimeout;
    @FieldDescribe("启用记录统计日志.")
    private Boolean logStatEnable;
    @FieldDescribe("统计日志输出间隔.")
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

    public Integer getLockTimeout() {
        return (null == this.lockTimeout || this.lockTimeout < 1) ? DEFAULT_LOCKTIMEOUT : this.lockTimeout;
    }

    public String getLogLevel() {
        return StringUtils.isEmpty(this.logLevel) ? DEFAULT_LOGLEVEL : this.logLevel;
    }

    public Boolean getSlowSqlEnable() {
        return BooleanUtils.isTrue(this.slowSqlEnable);
    }

    public Integer getSlowSqlThreshold() {
        return (null == this.slowSqlThreshold || this.slowSqlThreshold < 1) ? DEFAULT_SLOWSQLTHRESHOLD
                : this.slowSqlThreshold;
    }

    public String getStatFilter() {
        return StringUtils.isEmpty(this.statFilter) ? DEFAULT_STATFILTER : this.statFilter;
    }

    public Boolean getStatEnable() {
        return BooleanUtils.isNotFalse(this.statEnable);
    }

    public Integer getMaxIdle() {
        if ((this.maxIdle == null) || (this.maxIdle < 1)) {
            return DEFAULT_MAXIDLE;
        } else {
            return this.maxTotal;
        }
    }

    public Integer getMaxTotal() {
        if ((this.maxTotal == null) || (this.maxTotal < 0)) {
            return DEFAULT_MAXTOTAL;
        } else {
            return this.maxTotal;
        }
    }

    public Boolean getJmxEnable() {
        return BooleanUtils.isTrue(this.jmxEnable);
    }

    public Integer getCacheSize() {
        return (this.cacheSize == null || this.cacheSize < DEFAULT_CACHESIZE) ? DEFAULT_CACHESIZE : this.cacheSize;
    }

    public Integer getTcpPort() {
        if (null != this.tcpPort && this.tcpPort > 0) {
            return this.tcpPort;
        }
        return DEFAULT_TCPPORT;
    }

    public Integer getWebPort() {
        if (null != this.webPort && this.webPort > 0) {
            return this.webPort;
        }
        return DEFAULT_WEBPORT;
    }

    public Boolean getEnable() {
        return BooleanUtils.isTrue(this.enable);
    }

    public List<String> getIncludes() {
        if (null != this.includes) {
            return this.includes;
        }
        return new ArrayList<>();
    }

    public List<String> getExcludes() {
        if (null != this.excludes) {
            return this.excludes;
        }
        return new ArrayList<>();
    }

    public void setTcpPort(Integer tcpPort) {
        this.tcpPort = tcpPort;
    }

    public void setWebPort(Integer webPort) {
        this.webPort = webPort;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public void setIncludes(List<String> includes) {
        this.includes = includes;
    }

    public void setExcludes(List<String> excludes) {
        this.excludes = excludes;
    }

    public void setJmxEnable(Boolean jmxEnable) {
        this.jmxEnable = jmxEnable;
    }

    public void setCacheSize(Integer cacheSize) {
        this.cacheSize = cacheSize;
    }

    public void setLogLevel(String logLevel) {
        this.logLevel = logLevel;
    }

}
