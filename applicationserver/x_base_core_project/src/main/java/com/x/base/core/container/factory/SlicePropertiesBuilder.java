package com.x.base.core.container.factory;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.druid.pool.DruidDataSource;
import com.x.base.core.container.FactorDistributionPolicy;
import com.x.base.core.project.config.DataMapping;

public class SlicePropertiesBuilder {

	// 20160127要做的事情，删除应用要删除数据字典和work，目前已经删除了信泰的数据字典重复部分，没有来得及删除数据

	public static String driver_db2 = "com.ibm.db2.jcc.DB2Driver";
	public static String driver_oracle = "oracle.jdbc.OracleDriver";
	public static String driver_mysql = "com.mysql.jdbc.Driver";
	public static String driver_postgresql = "org.postgresql.Driver";
	public static String driver_informix = "com.informix.jdbc.IfxDriver";
	public static String driver_h2 = "org.h2.Driver";
	public static String driver_dm = "dm.jdbc.driver.DmDriver";
	/** 避免db2在aix版本和lwl版本字段长度不一致的问题 */
	private static String dictionary_db2 = "db2(createPrimaryKeys=false,characterColumnSize=255,maxColumnNameLength=128,maxIndexNameLength=128,maxConstraintNameLength=128)";
	private static String dictionary_oracle = "oracle(maxTableNameLength=128,maxColumnNameLength=128,maxIndexNameLength=128,maxConstraintNameLength=128,maxEmbeddedClobSize=104857600)";
	private static String dictionary_mysql = "mysql(clobTypeName=LONGTEXT,blobTypeName=LONGBLOB,createPrimaryKeys=false,maxIndexesPerTable=64)";
	private static String dictionary_postgresql = "postgres";
	private static String dictionary_informix = "informix";
	private static String dictionary_h2 = "org.apache.openjpa.jdbc.sql.H2Dictionary";
	private static String dictionary_dm = "com.x.base.core.openjpa.jdbc.sql.DMDictionary";

	private static String validationQuery_db2 = "select 1 from sysibm.sysdummy1";
	private static String validationQuery_oracle = "select 1 from dual";
	private static String validationQuery_mysql = "select 1";
	private static String validationQuery_postgresql = "select 1";
	private static String validationQuery_informix = "select 1";
	private static String validationQuery_h2 = "select 1";
	private static String validationQuery_dm = "select getdate()";

	public static Map<String, String> getPropertiesDBCP(List<DataMapping> list) throws Exception {
		try {
			if (list.isEmpty()) {
				throw new Exception("parameter data list is empty.");
			}
			Map<String, String> properties = new LinkedHashMap<String, String>();
			properties.put("openjpa.BrokerFactory", "slice");
			properties.put("openjpa.jdbc.DBDictionary", determineDBDictionary(list.get(0)));
			/**
			 * 如果是DB2 添加 Schema,mysql 不需要Schema 如果用了Schema H2数据库就会报错说没有Schema
			 */
			if (StringUtils.equals(determineDBDictionary(list.get(0)), dictionary_db2)) {
				properties.put("openjpa.jdbc.Schema", "x");
			}
			if (StringUtils.equals(determineDBDictionary(list.get(0)), dictionary_informix)) {
				properties.put("openjpa.jdbc.Schema", "x");
			}
			properties.put("openjpa.slice.Lenient", "false");
			// properties.put("openjpa.Multithreaded", "true");
			properties.put("openjpa.slice.DistributionPolicy", FactorDistributionPolicy.class.getName());
			properties.put("openjpa.slice.Names", getSliceNames(list));
			// properties.put("openjpa.ConnectionDriverName",
			// org.apache.commons.dbcp2.BasicDataSource.class.getName());
			properties.put("openjpa.ConnectionDriverName", DruidDataSource.class.getName());
			// properties.put("openjpa.ConnectionDriverName",
			// org.apache.commons.dbcp2.managed.BasicManagedDataSource.class.getCanonicalName());
			properties.put("openjpa.QueryCompilationCache", "false");
			properties.put("openjpa.IgnoreChanges", "true");
			properties.put("openjpa.QueryCache", "false");
			properties.put("openjpa.jdbc.ResultSetType", "scroll-insensitive");
			// properties.put("openjpa.DynamicEnhancementAgent", "true");
			properties.put("openjpa.jdbc.SynchronizeMappings", "buildSchema(ForeignKeys=false)");
			// properties.put("openjpa.jdbc.SchemaFactory",
			// "native(ForeignKeys=false)");
			// properties.put("openjpa.Compatibility",
			// "StrictIdentityValues=true");
			/* 事务管理 */
			// properties.put("openjpa.ManagedRuntime",
			// "jndi(TransactionManagerName=java:/TransactionManager)");
			/* 锁 */
			properties.put("openjpa.LockManager", "none");
			properties.put("openjpa.Log", "DefaultLevel=WARN");
			for (int i = 0; i < list.size(); i++) {
				String slice = getName(i);
				DataMapping dataMapping = list.get(i);
				properties.put("openjpa.slice." + slice + ".ConnectionProperties",
						getConnectionPropertiesDruid(dataMapping));
				// properties.put("openjpa.slice." + slice +
				// ".ConnectionDriverName",
				// org.apache.commons.dbcp.BasicDataSource.class.getCanonicalName());
				properties.put("openjpa.slice." + slice + ".ConnectionDriverName", DruidDataSource.class.getName());
				// properties.put("openjpa.slice." + slice +
				// ".ConnectionDriverName",
				// org.apache.commons.dbcp2.managed.BasicManagedDataSource.class.getCanonicalName());
				properties.put("openjpa.slice." + slice + ".Log", getLog(dataMapping));
				properties.put("openjpa.slice." + slice + ".IgnoreChanges", "true");
				properties.put("openjpa.slice." + slice + ".QueryCache", "false");
				properties.put("openjpa.slice." + slice + ".QueryCompilationCache", "false");
				properties.put("openjpa.slice." + slice + ".LockManager", "none");
				properties.put("openjpa.slice." + slice + ".ConnectionFactoryProperties",
						"PrettyPrint=true, PrettyPrintLineLength=72");
			}
			return properties;
		} catch (Exception e) {
			throw new Exception("can not convert connection to slice properties", e);
		}
	}

	// openjpa.slice.Names 属性值
	public static String getSliceNames(List<DataMapping> list) throws Exception {
		try {
			String[] arr = new String[list.size()];
			for (int j = 0; j < list.size(); j++) {
				arr[j] = getName(j);
			}
			return StringUtils.join(arr, ", ");
		} catch (Exception e) {
			throw new Exception("can not create slice names", e);
		}
	}

	// 单个slice名称
	public static String getName(Integer i) throws Exception {
		try {
			return "s" + ((1001 + i) + "").substring(1);
		} catch (Exception e) {
			throw new Exception("can not create slice name property", e);
		}
	}

	// /* 使用DBCP连接池时产生的属性 */
	// protected static String getConnectionPropertiesDBCP(DataMapping dataMapping)
	// throws Exception {
	// try {
	// String str = "MaxActive=5, MaxIdle=1, MinIdle=0, MaxWait=10000, Username=" +
	// dataMapping.getUsername()
	// + ", Password=" + dataMapping.getPassword() + ", TestOnBorrow=true";
	// if (StringUtils.equals(determineDBDictionary(dataMapping), dictionary_db2)) {
	// str += ", driverClassName=" + driver_db2 + ", url=" + dataMapping.getUrl();
	// } else if (StringUtils.equals(determineDBDictionary(dataMapping),
	// dictionary_mysql)) {
	// String url = dataMapping.getUrl();
	// // url += "?autoReconnect=true";
	// str += ", driverClassName=" + driver_mysql + ", url=" + url;
	// } else if (StringUtils.equals(determineDBDictionary(dataMapping),
	// dictionary_postgresql)) {
	// String url = dataMapping.getUrl();
	// str += ", driverClassName=" + driver_postgresql + ", url=" + url;
	// } else if (StringUtils.equals(determineDBDictionary(dataMapping),
	// dictionary_informix)) {
	// String url = dataMapping.getUrl();
	// str += ", driverClassName=" + driver_informix + ", url=" + url;
	// } else if (StringUtils.equals(determineDBDictionary(dataMapping),
	// dictionary_h2)) {
	// String url = dataMapping.getUrl();
	// str += ", driverClassName=" + driver_h2 + ", url=" + url;
	// }
	// return str;
	// } catch (Exception e) {
	// throw new Exception("can not create connection properites", e);
	// }
	// }

	/* 使用DBCP2连接池时产生的属性 */
	protected static String getConnectionPropertiesDBCP2(DataMapping dataMapping) throws Exception {
		try {
			String str = "maxTotal=5, maxIdle=2, minIdle=0, maxWaitMillis=30000, timeBetweenEvictionRunsMillis=300000, minEvictableIdleTimeMillis=300000, maxConnLifetimeMillis=1200000, Username="
					+ dataMapping.getUsername() + ", Password=" + dataMapping.getPassword();
			if (StringUtils.equals(determineDBDictionary(dataMapping), dictionary_db2)) {
				str += ",validationQuery=" + validationQuery_db2 + ", driverClassName=" + driver_db2 + ", url="
						+ dataMapping.getUrl();
			} else if (StringUtils.equals(determineDBDictionary(dataMapping), dictionary_oracle)) {
				String url = dataMapping.getUrl();
				str += ",validationQuery=" + validationQuery_oracle + ", driverClassName=" + driver_oracle + ", url="
						+ url;
			} else if (StringUtils.equals(determineDBDictionary(dataMapping), dictionary_mysql)) {
				String url = dataMapping.getUrl();
				// url += "?autoReconnect=true";
				str += ",validationQuery=" + validationQuery_mysql + ", driverClassName=" + driver_mysql + ", url="
						+ url;
			} else if (StringUtils.equals(determineDBDictionary(dataMapping), dictionary_postgresql)) {
				String url = dataMapping.getUrl();
				str += ",validationQuery=" + validationQuery_postgresql + ", driverClassName=" + driver_postgresql
						+ ", url=" + url;
			} else if (StringUtils.equals(determineDBDictionary(dataMapping), dictionary_informix)) {
				String url = dataMapping.getUrl();
				str += ",validationQuery=" + validationQuery_informix + ", driverClassName=" + driver_informix
						+ ", url=" + url;
			} else if (StringUtils.equals(determineDBDictionary(dataMapping), dictionary_h2)) {
				String url = dataMapping.getUrl();
				str += ",validationQuery=" + validationQuery_h2 + ", driverClassName=" + driver_h2 + ", url=" + url;
			} else if (StringUtils.equals(determineDBDictionary(dataMapping), dictionary_dm)) {
				String url = dataMapping.getUrl();
				str += ",validationQuery=" + validationQuery_oracle + ", driverClassName=" + driver_dm + ", url=" + url;
			}
			return str;
		} catch (Exception e) {
			throw new Exception("can not create connection properites", e);
		}
	}

	/* 使用Druid连接池时产生的属性 */
	protected static String getConnectionPropertiesDruid(DataMapping dataMapping) throws Exception {
		try {
			String str = "filters=stat, poolPreparedStatements=true, maxActive=5, minIdle=0, testOnBorrow=true, Username="
					+ dataMapping.getUsername() + ", Password=" + dataMapping.getPassword();
			// String str = "filters=stat, poolPreparedStatements=true,
			// maxActive=20, minIdle=0, timeBetweenEvictionRunsMillis=100000,
			// minEvictableIdleTimeMillis=300000, Username="
			// + dataMapping.getUsername() + ", Password=" +
			// dataMapping.getPassword();
			if (StringUtils.equals(determineDBDictionary(dataMapping), dictionary_db2)) {
				str += ", validationQuery=" + validationQuery_db2 + ", driverClassName=" + driver_db2 + ", url="
						+ dataMapping.getUrl();
			}
			if (StringUtils.equals(determineDBDictionary(dataMapping), dictionary_oracle)) {
				str += ", validationQuery=" + validationQuery_oracle + ", driverClassName=" + driver_oracle + ", url="
						+ dataMapping.getUrl();
			} else if (StringUtils.equals(determineDBDictionary(dataMapping), dictionary_mysql)) {
				String url = dataMapping.getUrl();
				// url += "?autoReconnect=true";
				str += ", validationQuery=" + validationQuery_mysql + ", driverClassName=" + driver_mysql + ", url="
						+ url;
			} else if (StringUtils.equals(determineDBDictionary(dataMapping), dictionary_postgresql)) {
				String url = dataMapping.getUrl();
				str += ", validationQuery=" + validationQuery_postgresql + ", driverClassName=" + driver_postgresql
						+ ", url=" + url;
			} else if (StringUtils.equals(determineDBDictionary(dataMapping), dictionary_informix)) {
				String url = dataMapping.getUrl();
				str += ", validationQuery=" + validationQuery_informix + ", driverClassName=" + driver_informix
						+ ", url=" + url;
			} else if (StringUtils.equals(determineDBDictionary(dataMapping), dictionary_h2)) {
				String url = dataMapping.getUrl();
				str += ", validationQuery=" + validationQuery_h2 + ", driverClassName=" + driver_h2 + ", url=" + url;
			} else if (StringUtils.equals(determineDBDictionary(dataMapping), dictionary_dm)) {
				String url = dataMapping.getUrl();
				str += ", validationQuery=" + validationQuery_dm + ", driverClassName=" + driver_dm + ", url=" + url;
			}
			return str;
		} catch (Exception e) {
			throw new Exception("can not create connection properites", e);
		}
	}

	/* 获取驱动名称 */
	public static String getConnectionDriverName(DataMapping dataMapping) throws Exception {
		try {
			if (StringUtils.equals(determineDBDictionary(dataMapping), dictionary_db2)) {
				return driver_db2;
			} else if (StringUtils.equals(determineDBDictionary(dataMapping), dictionary_oracle)) {
				return driver_oracle;
			} else if (StringUtils.equals(determineDBDictionary(dataMapping), dictionary_mysql)) {
				return driver_mysql;
			} else if (StringUtils.equals(determineDBDictionary(dataMapping), dictionary_postgresql)) {
				return driver_postgresql;
			} else if (StringUtils.equals(determineDBDictionary(dataMapping), dictionary_informix)) {
				return driver_informix;
			} else if (StringUtils.equals(determineDBDictionary(dataMapping), dictionary_h2)) {
				return driver_h2;
			} else if (StringUtils.equals(determineDBDictionary(dataMapping), dictionary_dm)) {
				return driver_dm;
			}
			throw new Exception("database jdbc driver miss match:" + dataMapping.getUrl());
		} catch (Exception e) {
			throw new Exception("can not get driverClassName property", e);
		}
	}

	/** 获取日志属性 */
	protected static String getLog(DataMapping dataMapping) throws Exception {
		try {
			return "Tool=" + dataMapping.getToolLevel() + ", Enhance=" + dataMapping.getEnhanceLevel() + ", METADATA="
					+ dataMapping.getMetaDataLevel() + ", RUNTIME=" + dataMapping.getRuntimeLevel() + ", Query="
					+ dataMapping.getQueryLevel() + ", DataCache=" + dataMapping.getDataCacheLevel() + ", JDBC="
					+ dataMapping.getJdbcLevel() + ", SQL=" + dataMapping.getSqlLevel();
		} catch (Exception e) {
			throw new Exception("can not get log property.", e);
		}
	}

	public static String determineDBDictionary(DataMapping dataMapping) throws Exception {
		try {
			if (StringUtils.containsIgnoreCase(dataMapping.getUrl(), "jdbc:db2:")) {
				return dictionary_db2;
			} else if (StringUtils.containsIgnoreCase(dataMapping.getUrl(), "jdbc:oracle:")) {
				return dictionary_oracle;
			} else if (StringUtils.containsIgnoreCase(dataMapping.getUrl(), "jdbc:mysql:")) {
				return dictionary_mysql;
			} else if (StringUtils.containsIgnoreCase(dataMapping.getUrl(), "jdbc:postgresql:")) {
				return dictionary_postgresql;
			} else if (StringUtils.containsIgnoreCase(dataMapping.getUrl(), "jdbc:informix-sqli:")) {
				return dictionary_informix;
			} else if (StringUtils.containsIgnoreCase(dataMapping.getUrl(), "jdbc:h2:tcp:")) {
				return dictionary_h2;
			} else if (StringUtils.containsIgnoreCase(dataMapping.getUrl(), "jdbc:dm:")) {
				return dictionary_dm;
			}
			throw new Exception("database jdbc driver miss match:" + dataMapping.getUrl());
		} catch (Exception e) {
			throw new Exception("can not get driverClassName property", e);
		}
	}
}