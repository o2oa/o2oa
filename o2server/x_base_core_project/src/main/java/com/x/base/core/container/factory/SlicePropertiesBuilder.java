package com.x.base.core.container.factory;

import org.apache.commons.lang3.StringUtils;

public class SlicePropertiesBuilder {

	private SlicePropertiesBuilder() {
		// nothing
	}

	public static String driver_db2 = "com.ibm.db2.jcc.DB2Driver";
	public static String driver_oracle = "oracle.jdbc.OracleDriver";
	public static String driver_mysql = "com.mysql.cj.jdbc.Driver";
	public static String driver_postgresql = "org.postgresql.Driver";
	public static String driver_informix = "com.informix.jdbc.IfxDriver";
	public static String driver_h2 = "org.h2.Driver";
	public static String driver_dm = "dm.jdbc.driver.DmDriver";
	public static String driver_sqlserver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
	public static String driver_gbase = "com.gbasedbt.jdbc.Driver";
	public static String driver_gbasemysql = "com.gbase.cj.jdbc.Driver";
	public static String driver_kingbase = "com.kingbase.Driver";
	public static String driver_kingbase8 = "com.kingbase8.Driver";
	public static String driver_oscar = "com.oscar.Driver";
	public static String driver_vastbase = "cn.com.vastbase.Driver";
	/* 避免db2在aix版本和lwl版本字段长度不一致的问题 */
	public static String dictionary_db2 = "db2(characterColumnSize=255,maxColumnNameLength=128,maxIndexNameLength=128,maxConstraintNameLength=128)";
	public static String dictionary_oracle = "oracle(maxTableNameLength=128,maxColumnNameLength=128,maxIndexNameLength=128,maxConstraintNameLength=128,maxEmbeddedClobSize=-1,maxEmbeddedBlobSize=-1)";
	public static String dictionary_mysql = "mysql(clobTypeName=LONGTEXT,blobTypeName=LONGBLOB,maxIndexesPerTable=64)";
	public static String dictionary_postgresql = "postgres";
	public static String dictionary_informix = "informix";
	public static String dictionary_h2 = "org.apache.openjpa.jdbc.sql.H2Dictionary";
	public static String dictionary_dm = "com.x.base.core.openjpa.jdbc.sql.DMDictionary";
	public static String dictionary_sqlserver = "sqlserver(schemaCase=preserve)";
	public static String dictionary_gbase = "com.x.base.core.openjpa.jdbc.sql.GBaseDictionary";
	public static String dictionary_gbasemysql = "com.x.base.core.openjpa.jdbc.sql.GBaseMySQLDictionary";
	public static String dictionary_kingbase = "com.x.base.core.openjpa.jdbc.sql.KingbaseDictionary";
	public static String dictionary_kingbase8 = "com.x.base.core.openjpa.jdbc.sql.Kingbase8Dictionary";
	public static String dictionary_oscar = "com.x.base.core.openjpa.jdbc.sql.OscarDictionary";
	public static String dictionary_vastbase = "com.x.base.core.openjpa.jdbc.sql.VastbaseDictionary";

//	public static String validationQuery_db2 = "select 1 from sysibm.sysdummy1";
//	public static String validationQuery_oracle = "select 1 from dual";
//	public static String validationQuery_mysql = "select 1";
//	public static String validationQuery_postgresql = "select 1";
//	public static String validationQuery_informix = "select 1";
//	public static String validationQuery_h2 = "select 1";
//	public static String validationQuery_dm = "select getdate()";
//	public static String validationQuery_sqlserver = "select 1";
//	public static String validationQuery_gbase = "select 1 from dual";
//	public static String validationQuery_gbasemysql = "select now()";
//	public static String validationQuery_kingbase = "select now()";
//	public static String validationQuery_kingbase8 = "select now()";
//	public static String validationQuery_oscar = "select 1 from dual";
//	public static String validationQuery_vastbase = "select 1";

	// 单个slice名称
	public static String getName(Integer i) throws Exception {
		try {
			return "s" + ((1001 + i) + "").substring(1);
		} catch (Exception e) {
			throw new Exception("can not create slice name property", e);
		}
	}

	public static String driverClassNameOfUrl(String url) throws Exception {
		if (StringUtils.containsIgnoreCase(url, "jdbc:db2:")) {
			return driver_db2;
		} else if (StringUtils.containsIgnoreCase(url, "jdbc:oracle:")) {
			return driver_oracle;
		} else if (StringUtils.containsIgnoreCase(url, "jdbc:mysql:")) {
			return driver_mysql;
		} else if (StringUtils.containsIgnoreCase(url, "jdbc:postgresql:")) {
			return driver_postgresql;
		} else if (StringUtils.containsIgnoreCase(url, "jdbc:informix-sqli:")) {
			return driver_informix;
		} else if (StringUtils.containsIgnoreCase(url, "jdbc:gbasedbt-sqli:")) {
			return driver_gbase;
		} else if (StringUtils.containsIgnoreCase(url, "jdbc:gbase:")) {
			return driver_gbasemysql;
		} else if (StringUtils.containsIgnoreCase(url, "jdbc:h2:tcp:")) {
			return driver_h2;
		} else if (StringUtils.containsIgnoreCase(url, "jdbc:dm:")) {
			return driver_dm;
		} else if (StringUtils.containsIgnoreCase(url, "jdbc:sqlserver:")) {
			return driver_sqlserver;
		} else if (StringUtils.containsIgnoreCase(url, "jdbc:kingbase:")) {
			return driver_kingbase;
		} else if (StringUtils.containsIgnoreCase(url, "jdbc:kingbase8:")) {
			return driver_kingbase8;
		} else if (StringUtils.containsIgnoreCase(url, "jdbc:oscar:")) {
			return driver_oscar;
		} else if (StringUtils.containsIgnoreCase(url, "jdbc:vastbase:")) {
			return driver_vastbase;
		}
		throw new Exception("can not get driverClassName of url: " + url + ".");
	}

	public static String dictionaryOfUrl(String url) throws Exception {
		if (StringUtils.containsIgnoreCase(url, "jdbc:db2:")) {
			return dictionary_db2;
		} else if (StringUtils.containsIgnoreCase(url, "jdbc:oracle:")) {
			return dictionary_oracle;
		} else if (StringUtils.containsIgnoreCase(url, "jdbc:mysql:")) {
			return dictionary_mysql;
		} else if (StringUtils.containsIgnoreCase(url, "jdbc:postgresql:")) {
			return dictionary_postgresql;
		} else if (StringUtils.containsIgnoreCase(url, "jdbc:informix-sqli:")) {
			return dictionary_informix;
		} else if (StringUtils.containsIgnoreCase(url, "jdbc:gbasedbt-sqli:")) {
			return dictionary_gbase;
		} else if (StringUtils.containsIgnoreCase(url, "jdbc:gbase:")) {
			return dictionary_gbasemysql;
		} else if (StringUtils.containsIgnoreCase(url, "jdbc:h2:tcp:")) {
			return dictionary_h2;
		} else if (StringUtils.containsIgnoreCase(url, "jdbc:dm:")) {
			return dictionary_dm;
		} else if (StringUtils.containsIgnoreCase(url, "jdbc:sqlserver:")) {
			return dictionary_sqlserver;
		} else if (StringUtils.containsIgnoreCase(url, "jdbc:kingbase:")) {
			return dictionary_kingbase;
		} else if (StringUtils.containsIgnoreCase(url, "jdbc:kingbase8:")) {
			return dictionary_kingbase8;
		} else if (StringUtils.containsIgnoreCase(url, "jdbc:oscar:")) {
			return dictionary_oscar;
		} else if (StringUtils.containsIgnoreCase(url, "jdbc:vastbase:")) {
			return dictionary_vastbase;
		}
		throw new Exception("can not get dictionary of url: " + url + ".");
	}

}