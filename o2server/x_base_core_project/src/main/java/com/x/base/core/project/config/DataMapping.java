//package com.x.base.core.project.config;
//
//import com.x.base.core.container.LogLevel;
//import com.x.base.core.project.gson.GsonPropertyObject;
//
//public class DataMapping extends GsonPropertyObject {
//
////	name		配置这个属性的意义在于，如果存在多个数据源，监控的时候可以通过名字来区分开来。如果没有配置，将会生成一个名字，格式是："DataSource-" + System.identityHashCode(this). 另外配置此属性至少在1.0.5版本中是不起作用的，强行设置name会出错。详情-点此处。
////	url		连接数据库的url，不同数据库不一样。例如：
////	mysql : jdbc:mysql://10.20.153.104:3306/druid2
////	oracle : jdbc:oracle:thin:@10.20.149.85:1521:ocnauto
////	username		连接数据库的用户名
////	password		连接数据库的密码。如果你不希望密码直接写在配置文件中，可以使用ConfigFilter。详细看这里
////	driverClassName	根据url自动识别	这一项可配可不配，如果不配置druid会根据url自动识别dbType，然后选择相应的driverClassName
////	initialSize	0	初始化时建立物理连接的个数。初始化发生在显示调用init方法，或者第一次getConnection时
////	maxActive	8	最大连接池数量
////	maxIdle	8	已经不再使用，配置了也没效果
////	minIdle		最小连接池数量
////	maxWait		获取连接时最大等待时间，单位毫秒。配置了maxWait之后，缺省启用公平锁，并发效率会有所下降，如果需要可以通过配置useUnfairLock属性为true使用非公平锁。
////	poolPreparedStatements	false	是否缓存preparedStatement，也就是PSCache。PSCache对支持游标的数据库性能提升巨大，比如说oracle。在mysql下建议关闭。
////	maxPoolPreparedStatementPerConnectionSize	-1	要启用PSCache，必须配置大于0，当大于0时，poolPreparedStatements自动触发修改为true。在Druid中，不会存在Oracle下PSCache占用内存过多的问题，可以把这个数值配置大一些，比如说100
////	validationQuery		用来检测连接是否有效的sql，要求是一个查询语句，常用select 'x'。如果validationQuery为null，testOnBorrow、testOnReturn、testWhileIdle都不会起作用。
////	validationQueryTimeout		单位：秒，检测连接是否有效的超时时间。底层调用jdbc Statement对象的void setQueryTimeout(int seconds)方法
////	testOnBorrow	true	申请连接时执行validationQuery检测连接是否有效，做了这个配置会降低性能。
////	testOnReturn	false	归还连接时执行validationQuery检测连接是否有效，做了这个配置会降低性能。
////	testWhileIdle	false	建议配置为true，不影响性能，并且保证安全性。申请连接的时候检测，如果空闲时间大于timeBetweenEvictionRunsMillis，执行validationQuery检测连接是否有效。
////	keepAlive	false
////	（1.0.28）	连接池中的minIdle数量以内的连接，空闲时间超过minEvictableIdleTimeMillis，则会执行keepAlive操作。
////	timeBetweenEvictionRunsMillis	1分钟（1.0.14）	有两个含义：
////	1) Destroy线程会检测连接的间隔时间，如果连接空闲时间大于等于minEvictableIdleTimeMillis则关闭物理连接。
////	2) testWhileIdle的判断依据，详细看testWhileIdle属性的说明
////	numTestsPerEvictionRun	30分钟（1.0.14）	不再使用，一个DruidDataSource只支持一个EvictionRun
////	minEvictableIdleTimeMillis		连接保持空闲而不被驱逐的最小时间
////	connectionInitSqls		物理连接初始化的时候执行的sql
////	exceptionSorter	根据dbType自动识别	当数据库抛出一些不可恢复的异常时，抛弃连接
////	filters		属性类型是字符串，通过别名的方式配置扩展插件，常用的插件有：
////	监控统计用的filter:stat
////	日志用的filter:log4j
////	防御sql注入的filter:wall
////	proxyFilters		类型是List<com.alibaba.druid.filter.Filter>，如果同时配置了filters和proxyFilters，是组合关系，并非替换关系
//
//	private String name;
//	private String url;
//	private String username;
//	private String password;
//	private String driverClassName;
//	private Integer initialSize;
//	private Integer maxActive;
//	private Integer minIdle; // 最小连接池数量
//
//	private LogLevel toolLevel = LogLevel.WARN;
//	private LogLevel runtimeLevel = LogLevel.WARN;
//	private LogLevel dataCacheLevel = LogLevel.WARN;
//	private LogLevel metaDataLevel = LogLevel.WARN;
//	private LogLevel enhanceLevel = LogLevel.WARN;
//	private LogLevel queryLevel = LogLevel.WARN;
//	private LogLevel sqlLevel = LogLevel.WARN;
//	private LogLevel jdbcLevel = LogLevel.ERROR;
//
//	public String getName() {
//		return name;
//	}
//
//	public String getDriverClassName() {
//		return driverClassName;
//	}
//
//	public Integer getInitialSize() {
//		return initialSize;
//	}
//
//	public Integer getMaxActive() {
//		return maxActive;
//	}
//
//	public Integer getMinIdle() {
//		return minIdle;
//	}
//
//	public String getUrl() {
//		return url;
//	}
//
//	public void setUrl(String url) {
//		this.url = url;
//	}
//
//	public String getUsername() {
//		return username;
//	}
//
//	public void setUsername(String username) {
//		this.username = username;
//	}
//
//	public String getPassword() {
//		return password;
//	}
//
//	public void setPassword(String password) {
//		this.password = password;
//	}
//
//	public LogLevel getToolLevel() {
//		return toolLevel;
//	}
//
//	public void setToolLevel(LogLevel toolLevel) {
//		this.toolLevel = toolLevel;
//	}
//
//	public LogLevel getRuntimeLevel() {
//		return runtimeLevel;
//	}
//
//	public void setRuntimeLevel(LogLevel runtimeLevel) {
//		this.runtimeLevel = runtimeLevel;
//	}
//
//	public LogLevel getDataCacheLevel() {
//		return dataCacheLevel;
//	}
//
//	public void setDataCacheLevel(LogLevel dataCacheLevel) {
//		this.dataCacheLevel = dataCacheLevel;
//	}
//
//	public LogLevel getMetaDataLevel() {
//		return metaDataLevel;
//	}
//
//	public void setMetaDataLevel(LogLevel metaDataLevel) {
//		this.metaDataLevel = metaDataLevel;
//	}
//
//	public LogLevel getEnhanceLevel() {
//		return enhanceLevel;
//	}
//
//	public void setEnhanceLevel(LogLevel enhanceLevel) {
//		this.enhanceLevel = enhanceLevel;
//	}
//
//	public LogLevel getQueryLevel() {
//		return queryLevel;
//	}
//
//	public void setQueryLevel(LogLevel queryLevel) {
//		this.queryLevel = queryLevel;
//	}
//
//	public LogLevel getSqlLevel() {
//		return sqlLevel;
//	}
//
//	public void setSqlLevel(LogLevel sqlLevel) {
//		this.sqlLevel = sqlLevel;
//	}
//
//	public LogLevel getJdbcLevel() {
//		return jdbcLevel;
//	}
//
//	public void setJdbcLevel(LogLevel jdbcLevel) {
//		this.jdbcLevel = jdbcLevel;
//	}
//
//	public void setName(String name) {
//		this.name = name;
//	}
//
//	public void setDriverClassName(String driverClassName) {
//		this.driverClassName = driverClassName;
//	}
//
//	public void setInitialSize(Integer initialSize) {
//		this.initialSize = initialSize;
//	}
//
//	public void setMaxActive(Integer maxActive) {
//		this.maxActive = maxActive;
//	}
//
//	public void setMinIdle(Integer minIdle) {
//		this.minIdle = minIdle;
//	}
//
//}