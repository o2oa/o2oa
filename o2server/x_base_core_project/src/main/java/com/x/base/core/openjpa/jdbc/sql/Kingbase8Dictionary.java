package com.x.base.core.openjpa.jdbc.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import org.apache.openjpa.jdbc.kernel.exps.FilterValue;
import org.apache.openjpa.jdbc.schema.Column;
import org.apache.openjpa.jdbc.schema.Sequence;
import org.apache.openjpa.jdbc.schema.Table;
import org.apache.openjpa.jdbc.sql.BooleanRepresentation;
import org.apache.openjpa.jdbc.sql.BooleanRepresentationFactory;
import org.apache.openjpa.jdbc.sql.DBDictionary;
import org.apache.openjpa.jdbc.sql.SQLBuffer;
import org.apache.openjpa.lib.jdbc.DelegatingConnection;
import org.apache.openjpa.lib.jdbc.DelegatingPreparedStatement;

public class Kingbase8Dictionary extends DBDictionary {
	public String allSequencesSQL = "select SYS_NAMESPACE.NSPNAME as SEQUENCE_SCHEMA, SYS_CLASS.RELNAME as SEQUENCE_NAME from SYS_NAMESPACE,SYS_CLASS where SYS_NAMESPACE.OID=SYS_CLASS.RELNAMESPACE and SYS_CLASS.RELKIND='S'";
	public String namedSequencesFromAllSchemasSQL = "select SYS_NAMESPACE.NSPNAME as SEQUENCE_SCHEMA, SYS_CLASS.RELNAME as SEQUENCE_NAME from SYS_NAMESPACE,SYS_CLASS where  SYS_NAMESPACE.OID=SYS_CLASS.RELNAMESPACE and SYS_CLASS.RELKIND='S' AND SYS_CLASS.RELNAME = ?";
	public String allSequencesFromOneSchemaSQL = "select SYS_NAMESPACE.NSPNAME as SEQUENCE_SCHEMA, SYS_CLASS.RELNAME as SEQUENCE_NAME from SYS_NAMESPACE,SYS_CLASS where SYS_NAMESPACE.OID=SYS_CLASS.RELNAMESPACE and SYS_CLASS.RELKIND='S' AND SYS_NAMESPACE.NSPNAME = ?";
	public String namedSequenceFromOneSchemaSQL = "select SYS_NAMESPACE.NSPNAME as SEQUENCE_SCHEMA, SYS_CLASS.RELNAME as SEQUENCE_NAME from SYS_NAMESPACE,SYS_CLASS where  SYS_NAMESPACE.OID=SYS_CLASS.RELNAMESPACE and SYS_CLASS.RELKIND='S' AND SYS_CLASS.RELNAME = ? AND SYS_NAMESPACE.NSPNAME = ?";

	public Kingbase8Dictionary() {
		this.platform = "KingbaseES";
		this.validationSQL = "SELECT NOW()";
		this.supportsSelectStartIndex = true;
		this.supportsSelectEndIndex = true;
		this.supportsLockingWithDistinctClause = false;
		this.maxTableNameLength = 63;
		this.maxColumnNameLength = 63;
		this.maxIndexNameLength = 63;
		this.maxConstraintNameLength = 63;
		this.maxAutoAssignNameLength = 63;
		this.lastGeneratedKeyQuery = "SELECT CURRVAL(''{2}'')";
		this.supportsAutoAssign = true;
		this.autoAssignTypeName = "INT IDENTITY";
		this.nextSequenceQuery = "SELECT NEXTVAL(''{0}'')";
		this.binaryTypeName = "BYTEA";
		this.longVarbinaryTypeName = "BYTEA";
		this.varbinaryTypeName = "BYTEA";
		this.longVarcharTypeName = "TEXT";
		this.charTypeName = "CHAR{0}";
		this.varcharTypeName = "VARCHAR{0}";
		/* add by Ray */
		this.bitTypeName = "BOOL";
		this.systemSchemaSet.addAll(Arrays.asList(new String[] { "INFORMATION_SCHEMA", "SYS_CATALOG" }));
		this.fixedSizeTypeNameSet.addAll(Arrays.asList(new String[] { "TEXT", "XML", "INTERVAL YEAR", "INTERVAL MONTH",
				"INTERVAL DAY", "INTERVAL HOUR", "INTERVAL MINUTE", "INTERVAL SECOND", " INTERVAL YEAR TO MONTH",
				"INTERVAL DAY TO SECOND", "BIT VARYING", "BYTEA", "BOOLEAN" }));
		this.reservedWordSet.addAll(Arrays.asList(new String[] { "ABORT", "ABSOLUTE", "ACCESS", "ACTION", "ADD",
				"ADMIN", "AFTER", "AGGREGATE", "ALL", "ALSO", "ALTER", "ANALYSE", "ANALYZE", "AND", "ANY", "ARRAY",
				"AS", "ASC", "ASSERTION", "ASSIGNMENT", "ASYMMETRIC", "AT", "AUTHID", "AUTHORIZATION", "BACKWARD",
				"BEFORE", "BEGIN", "BETWEEN", "BIGINT", "BINARY", "BIT", "BODY", "BOOLEAN", "BOTH", "BY", "CACHE",
				"CALL", "CALLED", "CASCADE", "CASCADED", "CASE", "CAST", "CHAIN", "CHAR", "CHARACTER",
				"CHARACTERISTICS", "CHECK", "CHECKALLOCATE", "CHECKCATALOG", "CHECKDB", "CHECKINDEX", "CHECKPOINT",
				"CHECKTABLE", "CHECKTABLESPACE", "CLASS", "CLOSE", "CLUSTER", "COALESCE", "COLLATE", "COLUMN",
				"COMMENT", "COMMIT", "COMMITTED", "CONCURRENTLY", "CONNECTION", "CONSTANT", "CONSTRAINT", "CONSTRAINTS",
				"CONVERSION", "CONVERT", "COPY", "CREATE", "CREATEDB", "CREATEROLE", "CREATEUSER", "CROSS", "CSV",
				"CURRENT_DATE", "CURRENT_ROLE", "CURRENT_TIME", "CURRENT_TIMESTAMP", "CURRENT_USER", "CURSOR", "CYCLE",
				"DATABASE", "DATAFILE", "DATEADD", "DATEDIFF", "DATEPART", "DAY", "DBCC", "DEALLOCATE", "DEC",
				"DECIMAL", "DECLARE", "DEFAULT", "DEFAULTS", "DEFERRABLE", "DEFERRED", "DEFINER", "DELETE", "DELIMITER",
				"DELIMITERS", "DESC", "DISABLE", "DISTINCT", "DO", "DOMAIN", "DOUBLE", "DROP", "EACH", "ELSE", "ENABLE",
				"ENCODING", "ENCRYPTED", "END", "ESCAPE", "EXCEPT", "EXCLUDING", "EXCLUSIVE", "EXECUTE", "EXISTS",
				"EXPLAIN", "EXTERNAL", "EXTERNALLY", "EXTRACT", "FALSE", "FETCH", "FILEGROWTH", "FILENAME", "FIRST",
				"FLOAT", "FOR", "FORCE", "FOREIGN", "FORWARD", "FREEZE", "FROM", "FULL", "FUNCTION", "GETDATE",
				"GLOBAL", "GRANT", "GRANTED", "GREATEST", "GROUP", "HANDLER", "HAVING", "HOLD", "HOUR", "IDENTIFIED",
				"IDENTITY", "IF", "IFNULL", "ILIKE", "IMMEDIATE", "IMMUTABLE", "IMPLICIT", "IN", "INCLUDING",
				"INCREMENT", "INDEX", "INDEXES", "INHERIT", "INHERITS", "INITIALLY", "INNER", "INOUT", "INPUT",
				"INSENSITIVE", "INSERT", "INSTEAD", "INT", "INTEGER", "INTERNAL", "INTERSECT", "INTERVAL", "INTO",
				"INVOKER", "IS", "ISNULL", "ISOLATION", "JOIN", "KEY", "LANCOMPILER", "LANGUAGE", "LARGE", "LAST",
				"LEADING", "LEAST", "LEFT", "LEVEL", "LIKE", "LIMIT", "LINK", "LIST", "LISTEN", "LOAD", "LOCAL",
				"LOCALTIME", "LOCALTIMESTAMP", "LOCATION", "LOCK", "LOGFILE", "LOGIN", "MATCH", "MAXSIZE", "MAXVALUE",
				"MINUTE", "MINVALUE", "MODE", "MODIFY", "MONTH", "MOVE", "NAME", "NAMES", "NATIONAL", "NATURAL",
				"NCHAR", "NEW", "NEXT", "NO", "NOALLOCATE", "NOCONSTRAINT", "NOCREATEDB", "NOCREATEROLE",
				"NOCREATEUSER", "NOINDEX", "NOINHERIT", "NOLOGIN", "NONE", "NOSUPERUSER", "NOT", "NOTHING", "NOTIFY",
				"NOTNULL", "NOWAIT", "NULL", "NULLIF", "NUMERIC", "OBJECT", "OF", "OFF", "OFFSET", "OIDS", "OLD", "ON",
				"ONLY", "OPERATOR", "OPTION", "OR", "ORDER", "OUT", "OUTER", "OVERLAPS", "OVERLAY", "OWNED", "OWNER",
				"PACKAGE", "PARTIAL", "PASSWORD", "PERCENT", "PLACING", "POSITION", "PRECISION", "PREPARE", "PREPARED",
				"PRESERVE", "PRIMARY", "PRIOR", "PRIVILEGES", "PROCEDURAL", "PROCEDURE", "QUOTE", "READ", "REAL",
				"REASSIGN", "RECHECK", "REFERENCES", "REINDEX", "RELATIVE", "RELEASE", "REMOVE", "RENAME", "REPEATABLE",
				"REPLACE", "RESET", "RESIZE", "RESTART", "RESTRICT", "RETURNING", "RETURNS", "REVOKE", "RIGHT", "ROLE",
				"ROLLBACK", "ROW", "ROWNUM", "ROWS", "ROWTYPE", "RULE", "SAVEPOINT", "SCHEMA", "SCROLL", "SECOND",
				"SECURITY", "SELECT", "SEQUENCE", "SERIALIZABLE", "SESSION", "SESSION_USER", "SET", "SETOF", "SHARE",
				"SHOW", "SIMILAR", "SIMPLE", "SIZE", "SMALLINT", "SOME", "STABLE", "START", "STATEMENT", "STATISTICS",
				"STDIN", "STDOUT", "STORAGE", "STRICT", "SUBSTRING", "SUPERUSER", "SWITCH", "SYMMETRIC", "SYSDATE",
				"SYSID", "SYSTEM", "TABLE", "TABLESPACE", "TEMP", "TEMPFILE", "TEMPLATE", "TEMPORARY", "THEN", "TIME",
				"TIMESTAMP", "TIMESTAMPADD", "TIMESTAMPDIFF", "TINYINT", "TO", "TOP", "TRAILING", "TRANSACTION",
				"TREAT", "TRIGGER", "TRIM", "TRUE", "TRUNCATE", "TRUSTED", "TYPE", "UNCOMMITTED", "UNENCRYPTED",
				"UNION", "UNIQUE", "UNKNOWN", "UNLISTEN", "UNTIL", "UPDATE", "USAGE", "USER", "USING", "VACUUM",
				"VALID", "VALIDATOR", "VALUES", "VARCHAR", "VARCHAR2", "VARYING", "VERBOSE", "VIEW", "VOLATILE", "WHEN",
				"WHERE", "WITH", "WITHOUT", "WORK", "WRITE", "YEAR", "ZONE", "BLOB", "BYTEA", "CLOB", "DATE", "TEXT",
				"TIMESTAMPTZ", "TIMETZ", "RETURN", "ROWCOUNT", "CMAX", "CMIN", "CTID", "OID", "TABLEOID", "XMAX",
				"XMIN" }));
	}

	public Date getDate(ResultSet paramResultSet, int paramInt) throws SQLException {
		try {
			return super.getDate(paramResultSet, paramInt);
		} catch (StringIndexOutOfBoundsException localStringIndexOutOfBoundsException) {
			String str = paramResultSet.getString(paramInt);
			SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SS");
			try {
				return localSimpleDateFormat.parse(str);
			} catch (ParseException localParseException) {
				throw new SQLException(localParseException.toString());
			}
		}
	}

	/* add by Ray */
	protected BooleanRepresentation booleanRepresentation = BooleanRepresentationFactory.BOOLEAN;

	/**
	 * Convert the specified column of the SQL ResultSet to the proper java type.
	 */
	public boolean getBoolean(ResultSet rs, int column) throws SQLException {
		return booleanRepresentation.getBoolean(rs, column);
	}

	/**
	 * Set the given value as a parameter to the statement.
	 */
	public void setBoolean(PreparedStatement stmnt, int idx, boolean val, Column col) throws SQLException {
		booleanRepresentation.setBoolean(stmnt, idx, val);
	}

//	public void setBoolean(PreparedStatement paramPreparedStatement, int paramInt, boolean paramBoolean,
//			Column paramColumn) throws SQLException {
//		paramPreparedStatement.setBoolean(paramInt, paramBoolean);
//	}

	/* add by Ray end */

	protected void appendSelectRange(SQLBuffer paramSQLBuffer, long paramLong1, long paramLong2, boolean paramBoolean) {
		if (paramLong2 != Long.MAX_VALUE) {
			paramSQLBuffer.append(" LIMIT ").appendValue(paramLong2 - paramLong1);
		}
		if (paramLong1 != 0L) {
			paramSQLBuffer.append(" OFFSET ").appendValue(paramLong1);
		}
	}

	public void indexOf(SQLBuffer paramSQLBuffer, FilterValue paramFilterValue1, FilterValue paramFilterValue2,
			FilterValue paramFilterValue3) {
		paramSQLBuffer.append("(POSITION(");
		paramFilterValue2.appendTo(paramSQLBuffer);
		paramSQLBuffer.append(" IN ");
		if (paramFilterValue3 != null) {
			substring(paramSQLBuffer, paramFilterValue1, paramFilterValue3, null);
		} else {
			paramFilterValue1.appendTo(paramSQLBuffer);
		}
		paramSQLBuffer.append(") - 1");
		if (paramFilterValue3 != null) {
			paramSQLBuffer.append(" + ");
			paramFilterValue3.appendTo(paramSQLBuffer);
		}
		paramSQLBuffer.append(")");
	}

	public String[] getCreateSequenceSQL(Sequence paramSequence) {
		String[] arrayOfString = super.getCreateSequenceSQL(paramSequence);
		if (paramSequence.getAllocate() > 1) {
			int tmp23_22 = 0;
			String[] tmp23_21 = arrayOfString;
			tmp23_21[tmp23_22] = (tmp23_21[tmp23_22] + " CACHE " + paramSequence.getAllocate());
		}
		return arrayOfString;
	}

	protected String getSequencesSQL(String paramString1, String paramString2) {
		if ((paramString1 == null) && (paramString2 == null)) {
			return this.allSequencesSQL;
		}
		if (paramString1 == null) {
			return this.namedSequencesFromAllSchemasSQL;
		}
		if (paramString2 == null) {
			return this.allSequencesFromOneSchemaSQL;
		}
		return this.namedSequenceFromOneSchemaSQL;
	}

	public boolean isSystemSequence(String paramString1, String paramString2, boolean paramBoolean) {
		if (super.isSystemSequence(paramString1, paramString2, paramBoolean)) {
			return true;
		}
		int i = paramString1.indexOf('_');
		return (i != -1) && (i != paramString1.length() - 4) && (paramString1.toUpperCase().endsWith("_SEQ"));
	}

	public boolean isSystemTable(String paramString1, String paramString2, boolean paramBoolean) {
		return (super.isSystemTable(paramString1, paramString2, paramBoolean))
				|| ((paramString1 != null) && (paramString1.toLowerCase().startsWith("sys_")));
	}

	public boolean isSystemIndex(String paramString, Table paramTable) {
		return (super.isSystemIndex(paramString, paramTable))
				|| ((paramString != null) && (paramString.toLowerCase().startsWith("sys_")));
	}

	public Connection decorate(Connection paramConnection) throws SQLException {
		return new KingbaseConnection(super.decorate(paramConnection), this);
	}

	private static class KingbasePreparedStatement extends DelegatingPreparedStatement {
		public KingbasePreparedStatement(PreparedStatement paramPreparedStatement, Connection paramConnection,
				Kingbase8Dictionary paramKingbaseDictionary) {
			super(paramPreparedStatement, paramConnection);
		}

		protected ResultSet executeQuery(boolean paramBoolean) throws SQLException {
			try {
				return super.executeQuery(paramBoolean);
			} catch (SQLException localSQLException) {
				ResultSet localResultSet = getResultSet(paramBoolean);
				if (localResultSet == null) {
					throw localSQLException;
				}
				return localResultSet;
			}
		}
	}

	private static class KingbaseConnection extends DelegatingConnection {
		private final Kingbase8Dictionary _dict;

		public KingbaseConnection(Connection paramConnection, Kingbase8Dictionary paramKingbaseDictionary) {
			super(paramConnection);
			this._dict = paramKingbaseDictionary;
		}

		protected PreparedStatement prepareStatement(String paramString, boolean paramBoolean) throws SQLException {
			return new Kingbase8Dictionary.KingbasePreparedStatement(super.prepareStatement(paramString, false), this,
					this._dict);
		}

		protected PreparedStatement prepareStatement(String paramString, int paramInt1, int paramInt2,
				boolean paramBoolean) throws SQLException {
			return new Kingbase8Dictionary.KingbasePreparedStatement(
					super.prepareStatement(paramString, paramInt1, paramInt2, false), this, this._dict);
		}
	}
}
