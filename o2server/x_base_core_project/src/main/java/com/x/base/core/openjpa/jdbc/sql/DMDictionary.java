package com.x.base.core.openjpa.jdbc.sql;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;

import org.apache.openjpa.jdbc.sql.DBDictionary;
import org.apache.openjpa.jdbc.sql.SQLBuffer;
import org.apache.openjpa.lib.util.Localizer;

public class DMDictionary extends DBDictionary {
	public static final String VENDOR_DAMENG = "dameng";
	private static final Localizer _loc = Localizer.forPackage(DMDictionary.class);
	private String schemaCase = "preserve";
	public boolean uniqueIdentifierAsVarbinary = true;

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
				"PT_NOFOUND", "PT_ROWCOUNT", "PT_ROWTYPE", "PT_TYPE", "PUBLIC", "PUT", "RAISE", "RANGE", "RAWTOHEX",
				"READ", "READ_PER_CALL", "READ_PER_SESSION", "READONLY", "REAL", "REBUILD", "RECORD", "REF",
				"REFERENCES", "REFERENCING", "RELATED", "RELATIVE", "RENAME", "REPEAT", "REPEATABLE", "REPLACE",
				"REPLICATE", "RESIZE", "RESTORE", "RESTRICT", "RETURN", "RETURNING", "REVERSE", "REVOKE", "RIGHT",
				"ROLE", "ROLLBACK", "ROLLFILE", "ROLLUP", "ROOT", "ROW", "ROWCOUNT", "ROWID", "ROWNUM", "ROWS", "RULE",
				"SALT", "SAVEPOINT", "SBYTE", "SCHEMA", "SCOPE", "SEALED", "SECTION", "SECOND", "SELECT", "SELSTAR",
				  "SEQUENCE", "SERERR", "SERIALIZABLE", "SERVER", "SESSION_PER_USER", "SET", "SETS", "SHARE", "SHORT",
				"SHUTDOWN", "SIBLINGS", "SIZE", "SIZEOF", "SMALLINT", "SNAPSHOT", "SOME", "SOUND", "SPLIT", "SQL",
				"STANDBY", "START_WITH", "STARTUP", "STATEMENT", "STATIC", "STAT", "STDDEV", "STORAGE", "STORE",
				"STRING", "STRUCT", "STYLE", "SUBSTRING", "SUCCESSFUL", "SUM", "SUSPEND", "SWITCH", "SYNC", "SYNONYM",
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

	public Date getDate(ResultSet rs, int column) throws SQLException {
		return rs.getDate(column);
	}
}
