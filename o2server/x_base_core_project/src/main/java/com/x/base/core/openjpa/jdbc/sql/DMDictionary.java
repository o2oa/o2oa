package com.x.base.core.openjpa.jdbc.sql;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

import org.apache.openjpa.jdbc.identifier.DBIdentifier;
import org.apache.openjpa.jdbc.identifier.DBIdentifier.DBIdentifierType;
import org.apache.openjpa.jdbc.schema.Column;
import org.apache.openjpa.jdbc.sql.DBDictionary;
import org.apache.openjpa.jdbc.sql.SQLBuffer;
import org.apache.openjpa.lib.util.Localizer;

public class DMDictionary extends DBDictionary {

	public static final String VENDOR_DAMENG = "dameng";

	private static final Localizer _loc = Localizer.forPackage(DMDictionary.class);

	private String schemaCase = "preserve";

	public boolean uniqueIdentifierAsVarbinary = true;

	/**
	 * 达梦 INTEGER/INT JDBC 元数据通常返回 COLUMN_SIZE = 10， 但 OpenJPA 映射侧固定整数列通常 size = 0。
	 *
	 * 开启后，在读取数据库元数据后把固定整数列 size 归一化为 0。
	 */
	public boolean normalizeFixedIntegerSize = true;

	/**
	 * 达梦 JDBC 元数据可能把列名返回为 "xorderColumn"， 而 OpenJPA 映射侧是 xorderColumn。
	 *
	 * 开启后，只在 schema metadata 反射路径上去掉外层定界符。
	 */
	public boolean normalizeMetadataIdentifierDelimiters = true;

	public void setNormalizeFixedIntegerSize(boolean normalizeFixedIntegerSize) {
		this.normalizeFixedIntegerSize = normalizeFixedIntegerSize;
	}

	public void setNormalizeMetadataIdentifierDelimiters(boolean normalizeMetadataIdentifierDelimiters) {
		this.normalizeMetadataIdentifierDelimiters = normalizeMetadataIdentifierDelimiters;
	}

	public DMDictionary() {
		this.platform = "dameng";
		this.validationSQL = "SELECT GETDATE()";
		this.supportsAutoAssign = true;
		this.autoAssignClause = "IDENTITY";
		this.lastGeneratedKeyQuery = "SELECT @@IDENTITY";
		this.nextSequenceQuery = "SELECT {0}.NEXTVAL";
		this.integerTypeName = "INT";
		this.substringFunctionName = "SUBSTR";

		this.reservedWordSet.addAll(Arrays.asList(new String[] { "ABORT", "ABSOLUTE", "ABSTRACT", "ACROSS", "ACTION",
				"ADD", "AUDIT", "ADMIN", "AFTER", "ALL", "ALLOW_DATETIME", "ALLOW_IP", "ALTER", "ANALYZE", "AND", "ANY",
				"ARCHIVEDIR", "ARCHIVELOG", "ARCHIVESTYLE", "ARRAY", "ARRAYLEN", "AS", "ASC", "ASSIGN", "AT", "ATTACH",
				"AUTHORIZATION", "AUTO", "AUTOEXTEND", "AVG", "BACKUP", "BACKUPDIR", "BACKUPINFO", "BAKFILE", "BASE",
				"BEFORE", "BEGIN", "BETWEEN", "BIGDATEDIFF", "BIGINT", "BINARY", "BIT", "BITMAP", "BLOB", "BLOCK",
				"BOOL", "BOOLEAN", "BOTH", "BOUNDARY", "BRANCH", "BREAK", "BSTRING", "BTREE", "BY", "BYTE", "CACHE",
				"CALL", "CASCADE", "CASCADED", "CASE", "CAST", "CATALOG", "CATCH", "CHAIN", "CHAR", "CHARACTER",
				"CHECK", "CIPHER", "CLASS", "CLOB", "CLOSE", "CLUSTER", "CLUSTERBTR", "COLUMN", "COMMENT", "COMMIT",
				"COMMITTED", "COMMITWORK", "COMPILE", "COMPRESS", "COMPRESSED", "CONNECT", "CONNECT_BY_IS_CYCLE",
				"CONNECT_BY_IS_LEAF", "CONNECT_BY_ROOT", "CONNECT_IDLE_TIME", "CONST", "CONSTANT", "CONSER_OP",
				"CONSTRAINT", "CONTAINS", "CONTEXT", "CONTINUE", "CONVERT", "COUNT", "CPU_REF_CALL", "CPU_REF_SESSION",
				"CREATE", "CROSS", "CRYPTO", "CTLFILE", "CUBE", "CURRENT", "CURSOR", "CYCLE", "DANGLING", "DATABASE",
				"DATAFILE", "DATE", "DATEADD", "DATEDIFF", "DATEPART", "DATETIME", "DAY", "DBFILE", "DEBUG", "DEC",
				"DECIMAL", "DECLARE", "DECODE", "DEFAULT", "DEFERRABLE", "DELETE", "DELETING", "DEREF", "DESC",
				"DETACH", "DISABLE", "DISCONNECT", "DISKSPACE", "DISTINCT", "DISTRIBUTED", "DO", "DOUBLE", "DOWN",
				"DROP", "EACH", "ELSE", "ELSEIF", "ENABLE", "ENCRYPT", "ENCRYPTION", "END", "EQU", "ERROR", "ESCAPE",
				"EVENTINFO", "EXCEPT", "EXCEPTION", "EXCHANGE", "EXCLUSIVE", "EXECUTE", "EXISTS", "EXIT", "EXPLAIN",
				"EXTERN", "EXTERNAL", "EXTERNALLY", "EXTRACT", "FAILED_LOGIN_ATTEMPS", "FALSE", "FETCH", "FILEGROUP",
				"FILLFACTOR", "FINALLY", "FIRST", "FLOAT", "FOR", "FORCE", "FOREIGN", "FREQUENCE", "FROM", "FULL",
				"FUNCTION", "FOLLOWING", "GET", "GLOBAL", "GOTO", "GRANT", "GROUP GROUPING", "HASH", "HAVING",
				"HEXTORAW", "HOUR", "IDENTIFIED", "IDENTITY", "IDENTITY_INSERT", "IF", "IMAGE", "IMMEDIATE", "IN",
				"INCREASE", "INCREMENT", "INDEX", "INITIAL", "INITIALLY", "INNER", "INNERID", "INSERT", "INSERTING",
				"INSTEAD", "INT", "INTEGER", "INTENT", "INTERNAL", "INTERSECT", "INTERVAL", "INTO", "IS", "ISOLATION",
				"JAVA", "JOIN", "KEY", "LABEL", "LAST", "LEAD", "LEFT", "LESS", "LEVEL", "LEXER", "LIKE", "LIMIT",
				"LINK", "LIST", "LOB", "LOCAL", "LOCK", "LOG", "LOGFILE", "LOGIN", "LOGOUT", "LONG", "LONGVARBINARY",
				"LONGVARCHAR", "LOOP", "LP_OP", "LT_BINTEGER", "LT_BIGINTEGER", "LT_BITSTRING", "LT_DECIMAL",
				"LT_GLOBAL_VAR", "LT_IDENTIFIER", "LT_INTEGER", "LP_REAL", "LT_STRING", "MANUAL", "MAP", "MATCH",
				"MATCHED", "MAX", "MAXSIZE", "MAXVALUE", "MEMBER", "MEN_SPACE", "MERGE", "MIN", "MINEXTENTS", "MINUS",
				"MINUTE", "MINVALUE", "MODE", "MODIFY", "MONEY", "MONTH", "MOUNT", "NATURAL", "NEW", "NEXT", "NO",
				"NOARCHIVELOG", "NOAUDIT", "NOBRANCH", "NOCACHE", "NOCYCLE", "NOMAXVALUE", "NOMINVALUE", "NONE",
				"NOORDER", "NORMAL", "NOSALT", "NOT", "NOT_ALLOW_DATETIME", "NOT_ALLOW_IP", "NOWAIT", "NULL", "",
				"NUMBER", "NUMERIC", "OBJECT", "OF", "OFF", "OFFLINE", "OFFSET", "OLD", "ON", "ONCE", "ONLINE", "ONLY",
				"OP_SHIFT_LERT", "OP_SHIFT_RIGHT", " OPEN", "OPTION", "OR", "ORDER", "OUT", "OUTER", "OVER ",
				"OVERLAPS", "OVERRIDE", "PACKAGE", "PACKAGE_BODY", "PAGE", "PARTIAL", "PARTITION", "PARTITIONS",
				"PASSWORD_GRACE_TIME", "PASSWORD_LIFE_TIME", "PASSWORD_LOCK_TIME", "PASSWORD_POLICY",
				"PASSWORD_REUSE_MAX", "PASSWORD_REUSE_TIME", "PENDANT", "PERCENT", "PRECEDING", "PRECISION", "PRESERVE",
				"PRIMARY", "PRINT", "PRIOR", "PRIVATE", "PRIVILEGES", "PROCEDURE", "PROTECTED", "PT_FOUND", "PT_ISOPEN",
				"PT_NOFOUND", "PT_ROWCOUNT", "PT_ROWTYPE", "PUBLIC", "PUT", "RAISE", "RANGE", "RAWTOHEX", "READ",
				"READ_PER_CALL", "READ_PER_SESSION", "READONLY", "REAL", "REBUILD", "RECORD", "REF", "REFERENCES",
				"REFERENCING", "RELATED", "RELATIVE", "RENAME", "REPEAT", "REPEATABLE", "REPLACE", "REPLICATE",
				"RESIZE", "RESTORE", "RESTRICT", "RETURN", "RETURNING", "REVERSE", "REVOKE", "RIGHT", "ROLE",
				"ROLLBACK", "ROLLFILE", "ROLLUP", "ROOT", "ROW", "ROWCOUNT", "ROWID", "ROWNUM", "ROWS", "RULE", "SALT",
				"SAVEPOINT", "SBYTE", "SCHEMA", "SCOPE", "SEALED", "SECTION", "SECOND", "SELECT", "SELSTAR", "SEQUENCE",
				"SERERR", "SERIALIZABLE", "SERVER", "SESSION_PER_USER", "SET", "SETS", "SHARE", "SHORT", "SHUTDOWN",
				"SIBLINGS", "SIZE", "SIZEOF", "SMALLINT", "SNAPSHOT", "SOME", "SOUND", "SPLIT", "SQL", "STANDBY",
				"START_WITH", "STARTUP", "STATEMENT", "STATIC", "STAT", "STDDEV", "STORAGE", "STORE", "STRING",
				"STRUCT", "STYLE", "SUBSTRING", "SUCCESSFUL", "SUM", "SUSPEND", "SWITCH", "SYNC", "SYNONYM",
				"SYS_CONNECT_BY_PATH", "TABLE", "TABLESPACE", "TEMPORARY", "TEXT", "THAN", "THEN", "THROW", "TIES",
				"TIME", "TIMER", "TIMES", "TIMESTAMP", "TIMESTAMPADD", "TIMESTAMPDIFF", "TINYINT", "TO",
				"TOO_MANY_ROWS", "TOP", "TRAIL", "TRANSACTION", "TRANSACTIONAL", "TRIGGER", "TRIGGERS", "TRIM", "TRUE",
				"TRUNCATE", "TRUNCSIZE", "TRY", "TYPE", "TYPE_BODY", "TYPEOF", "UINT", "ULONG", "UNBOUNDED",
				"UNCOMMITTED", "UNDER", "UNION", "UNIQUE", "UNLIMITED", "UNSAFE", "UNTIL", "UP", "UPDATE", "UPDATING",
				"USER", "USHORT", "USING", "VALUE", "VALUES", "VARBINARY", "VARCHAR", "VARCHAR2", "VARIANCE", "VARYING",
				"VERIFY", "VERTICAL", "VIEW", "VIRTUAL", "VOID", "VOLATILE", "VSIZE", "WEEK", "WHEN", "WHENEVER",
				"WHERE", "WHILE", "WITH", "WORK", "WRAPPED", "WRITE", "YEAR", "ZONE" }));

		this.systemSchemaSet.addAll(Arrays.asList(new String[] { "CTISYS", "SYS", "SYSDBA", "SYSSSO", "SYSAUDITOR" }));

		this.fixedSizeTypeNameSet
				.addAll(Arrays.asList(new String[] { "IMAGE", "TEXT", "DATETIME", "LONGVARBINARY", "LONGVARCHAR" }));

		/*
		 * 达梦固定整数/布尔类型： 1. 生成 DDL 时不要追加长度。 2. 配合 newColumn(...) 消除 Existing size 10 vs
		 * Given size 0 的伪差异。
		 */
		this.fixedSizeTypeNameSet.addAll(Arrays.asList(
				new String[] { "INT", "INTEGER", "BIGINT", "SMALLINT", "TINYINT", "BYTE", "BIT", "BOOL", "BOOLEAN" }));

		this.supportsDeferredConstraints = true;
		this.supportsSelectEndIndex = true;
	}

	protected void appendSelectRange(SQLBuffer buf, long start, long end, boolean subselect) {
		buf.append(" LIMIT ").appendValue(start).append(", ").appendValue(end - start);
	}

	public void connectedConfiguration(Connection conn) throws SQLException {
		super.connectedConfiguration(conn);

		boolean requiresWarnings = true;
		DatabaseMetaData meta = conn.getMetaData();
		String driverName = meta.getDriverName();
		String url = meta.getURL();

		if (this.driverVendor == null) {
			if ((driverName != null) && (driverName.equalsIgnoreCase("dm.jdbc.driver.DmDriver"))) {
				this.driverVendor = "Dm7JdbcDriver";
				if ((url != null) && (url.startsWith("jdbc:dm://"))) {
					requiresWarnings = false;
				}
			} else {
				this.driverVendor = "other";
			}
		}

		if (("Dm7JdbcDriver".equalsIgnoreCase(this.driverVendor)) && (requiresWarnings)) {
			this.log.warn(_loc.get("Dm Jdbc connection", url));
		}
	}

	/**
	 * OpenJPA 从数据库 metadata 名称转换为 DBIdentifier 时会走这里。
	 *
	 * 达梦 JDBC metadata 有时返回： "xorderColumn" ""xorderColumn""
	 *
	 * OpenJPA 映射侧通常是： xorderColumn
	 *
	 * 如果不清理，schema compare 会认为两者不兼容。
	 */
	@Override
	public DBIdentifier fromDBName(String name, DBIdentifierType type) {
		if (this.normalizeMetadataIdentifierDelimiters) {
			String cleaned = stripMetadataIdentifierDelimiters(name);
			if (cleaned != null && name != null && !cleaned.equals(name)) {
				DBIdentifier id = DBIdentifier.newIdentifier(cleaned, type, false);
				id.setIgnoreCase(true);
				return id;
			}
		}
		return super.fromDBName(name, type);
	}

	/**
	 * OpenJPA 读取 DatabaseMetaData.getColumns(...) 结果时会创建 Column。
	 *
	 * 在这里统一修复： 1. 列名/表名/schema/typeName 外层双引号。 2. 达梦 INT/INTEGER/BIGINT 等固定整数类型
	 * size。
	 */
	@Override
	protected Column newColumn(ResultSet colMeta) throws SQLException {
		Column col = super.newColumn(colMeta);
		normalizeReflectedColumn(col);
		return col;
	}

	private void normalizeReflectedColumn(Column col) {
		if (col == null) {
			return;
		}

		if (this.normalizeMetadataIdentifierDelimiters) {
			col.setSchemaIdentifier(stripIdentifier(col.getSchemaIdentifier()));
			col.setTableIdentifier(stripIdentifier(col.getTableIdentifier()));
			col.setIdentifier(stripIdentifier(col.getIdentifier()));
			col.setTypeIdentifier(stripIdentifier(col.getTypeIdentifier()));
		}

		String typeName = normalizedTypeName(col);

		/*
		 * 达梦驱动如果把 INT/BOOL 等报成其他兼容类型， 这里统一成 JDBC 标准类型，便于 OpenJPA 做类型比较。
		 */
		if ("INT".equals(typeName) || "INTEGER".equals(typeName)) {
			col.setType(Types.INTEGER);
		} else if ("BIGINT".equals(typeName)) {
			col.setType(Types.BIGINT);
		} else if ("SMALLINT".equals(typeName)) {
			col.setType(Types.SMALLINT);
		} else if ("TINYINT".equals(typeName) || "BYTE".equals(typeName)) {
			col.setType(Types.TINYINT);
		} else if ("BIT".equals(typeName) || "BOOL".equals(typeName) || "BOOLEAN".equals(typeName)) {
			col.setType(Types.BIT);
		}

		if (this.normalizeFixedIntegerSize && isFixedIntegerColumn(col, typeName)) {
			/*
			 * 关键修复：
			 *
			 * 达梦 metadata: INTEGER / INT Size 10
			 *
			 * OpenJPA mapping: INTEGER / INT Size 0
			 *
			 * 归一化后避免： Existing column Size: 10 Given column Size: 0
			 */
			col.setSize(0);
			col.setDecimalDigits(0);
		}
	}

	private DBIdentifier stripIdentifier(DBIdentifier id) {
		if (DBIdentifier.isNull(id)) {
			return id;
		}

		String name = id.getName();
		String cleaned = stripMetadataIdentifierDelimiters(name);

		if (cleaned == null || cleaned.length() == 0) {
			return id;
		}

		/*
		 * 即使文本看起来一样，但 DBIdentifier 自身处于 delimited 状态， 也重建为非 delimited，避免 schema compare
		 * 时把 "x" 和 x 当成不同。
		 */
		if (!cleaned.equals(name) || id.isDelimited()) {
			DBIdentifier normalized = DBIdentifier.newIdentifier(cleaned, id.getType(), false);
			normalized.setIgnoreCase(true);
			return normalized;
		}

		return id;
	}

	/**
	 * 清理 JDBC metadata 返回的标识符外层定界符。
	 *
	 * 支持： "x" -> x ""x"" -> x "X"."T" -> X.T `x` -> x [x] -> x
	 */
	private String stripMetadataIdentifierDelimiters(String name) {
		if (name == null) {
			return null;
		}

		String trimmed = name.trim();
		if (trimmed.length() == 0) {
			return trimmed;
		}

		String[] parts = trimmed.split("\\.");
		StringBuilder result = new StringBuilder();

		for (int i = 0; i < parts.length; i++) {
			if (i > 0) {
				result.append('.');
			}
			result.append(stripOneIdentifierSegment(parts[i]));
		}

		return result.toString();
	}

	private String stripOneIdentifierSegment(String segment) {
		String s = segment == null ? null : segment.trim();

		if (s == null) {
			return null;
		}

		boolean changed;

		do {
			changed = false;

			if (s.length() >= 2 && s.startsWith("\"") && s.endsWith("\"")) {
				s = s.substring(1, s.length() - 1);
				changed = true;
			} else if (s.length() >= 2 && s.startsWith("`") && s.endsWith("`")) {
				s = s.substring(1, s.length() - 1);
				changed = true;
			} else if (s.length() >= 2 && s.startsWith("[") && s.endsWith("]")) {
				s = s.substring(1, s.length() - 1);
				changed = true;
			}
		} while (changed && s.length() >= 2);

		return s;
	}

	private boolean isFixedIntegerColumn(Column col, String typeName) {
		switch (col.getType()) {
		case Types.INTEGER:
		case Types.BIGINT:
		case Types.SMALLINT:
		case Types.TINYINT:
		case Types.BIT:
		case Types.BOOLEAN:
			return true;
		default:
			return "INT".equals(typeName) || "INTEGER".equals(typeName) || "BIGINT".equals(typeName)
					|| "SMALLINT".equals(typeName) || "TINYINT".equals(typeName) || "BYTE".equals(typeName)
					|| "BIT".equals(typeName) || "BOOL".equals(typeName) || "BOOLEAN".equals(typeName);
		}
	}

	private String normalizedTypeName(Column col) {
		String name = null;

		if (col != null && !DBIdentifier.isNull(col.getTypeIdentifier())) {
			name = col.getTypeIdentifier().getName();
		}

		if (name == null && col != null) {
			/*
			 * getTypeName() 在 OpenJPA 3.x 标记为 deprecated， 但作为 fallback 使用可以提高兼容性。
			 */
			name = col.getTypeName();
		}

		if (name == null) {
			return "";
		}

		name = stripMetadataIdentifierDelimiters(name);

		if (name == null) {
			return "";
		}

		int paren = name.indexOf('(');
		if (paren >= 0) {
			name = name.substring(0, paren);
		}

		return name.trim().toUpperCase(Locale.ENGLISH);
	}

	public Date getDate(ResultSet rs, int column) throws SQLException {
		return rs.getDate(column);
	}
}