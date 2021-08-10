package com.x.base.core.openjpa.jdbc.sql;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.openjpa.jdbc.identifier.DBIdentifier;
import org.apache.openjpa.jdbc.identifier.DBIdentifier.DBIdentifierType;
import org.apache.openjpa.jdbc.kernel.JDBCFetchConfiguration;
import org.apache.openjpa.jdbc.kernel.JDBCStore;
import org.apache.openjpa.jdbc.kernel.exps.FilterValue;
import org.apache.openjpa.jdbc.schema.Column;
import org.apache.openjpa.jdbc.schema.ForeignKey;
import org.apache.openjpa.jdbc.schema.Index;
import org.apache.openjpa.jdbc.schema.PrimaryKey;
import org.apache.openjpa.jdbc.schema.Table;
import org.apache.openjpa.jdbc.sql.DBDictionary;
import org.apache.openjpa.jdbc.sql.SQLBuffer;
import org.apache.openjpa.jdbc.sql.Select;
import org.apache.openjpa.lib.util.StringUtil;
import org.apache.openjpa.util.StoreException;

public class GBaseMySQLDictionary extends DBDictionary {
	public static final String SELECT_HINT = "openjpa.hint.MySQLSelectHint";

	public static final String DELIMITER_BACK_TICK = "`";

	/**
	 * The MySQL table type to use when creating tables; defaults to innodb.
	 */
	// public String tableType = "innodb";
	public String tableType = "AcidDB";

	/**
	 * Whether to use clobs; defaults to true. Set this to false if you have an old
	 * version of MySQL which does not handle clobs properly.
	 */
	public boolean useClobs = true;

	/**
	 * Whether the driver automatically deserializes blobs.
	 */
	public boolean driverDeserializesBlobs = false;

	/**
	 * Whether to inline multi-table bulk-delete operations into MySQL's combined
	 * <code>DELETE FROM foo, bar, baz</code> syntax. Defaults to false, since this
	 * may fail in the presence of InnoDB tables with foreign keys.
	 * 
	 * @link http://dev.mysql.com/doc/refman/5.0/en/delete.html
	 */
	public boolean optimizeMultiTableDeletes = false;

	public static final String tinyBlobTypeName = "TINYBLOB";
	public static final String mediumBlobTypeName = "MEDIUMBLOB";
	public static final String longBlobTypeName = "LONGBLOB";
	public static final String tinyTextTypeName = "TINYTEXT";
	public static final String mediumTextTypeName = "MEDIUMTEXT";
	public static final String longTextTypeName = "LONGTEXT";

	public GBaseMySQLDictionary() {
		platform = "GBaseMySQL";
		validationSQL = "SELECT NOW()";
		distinctCountColumnSeparator = ",";

		supportsUniqueConstraints = true;
		supportsDeferredConstraints = false;
		constraintNameMode = CONS_NAME_MID;
		supportsMultipleNontransactionalResultSets = false;
		requiresAliasForSubselect = true; // new versions
		requiresTargetForDelete = true;
		supportsSelectStartIndex = true;
		supportsSelectEndIndex = true;

		datePrecision = MICRO;

		concatenateFunction = "CONCAT({0},{1})";

		maxTableNameLength = 64;
		maxColumnNameLength = 64;
		maxIndexNameLength = 64;
		maxConstraintNameLength = 64;
		maxIndexesPerTable = 64;
		schemaCase = SCHEMA_CASE_PRESERVE;

		supportsAutoAssign = true;
		lastGeneratedKeyQuery = "SELECT LAST_INSERT_ID()";
		autoAssignClause = "AUTO_INCREMENT";

		clobTypeName = longTextTypeName;
		blobTypeName = longBlobTypeName;
		longVarcharTypeName = "TEXT";
		longVarbinaryTypeName = "LONG VARBINARY";
		timestampTypeName = "DATETIME";
		xmlTypeName = "TEXT";
		fixedSizeTypeNameSet.addAll(Arrays.asList(new String[] { "BOOL", "LONG VARBINARY", "MEDIUMBLOB", "LONGBLOB",
				"TINYBLOB", "LONG VARCHAR", "MEDIUMTEXT", "LONGTEXT", "TEXT", "TINYTEXT", "DOUBLE PRECISION", "ENUM",
				"SET", "DATETIME", }));
		reservedWordSet.addAll(Arrays.asList(new String[] { "AUTO_INCREMENT", "BINARY", "BLOB", "CHANGE", "ENUM",
				"INFILE", "INT1", "INT2", "INT4", "FLOAT1", "FLOAT2", "FLOAT4", "LOAD", "MEDIUMINT", "OUTFILE",
				"REPLACE", "STARTING", "TEXT", "UNSIGNED", "ZEROFILL", "INDEX", }));

		// reservedWordSet subset that CANNOT be used as valid column names
		// (i.e., without surrounding them with double-quotes)
		// generated at 2021-05-02T15:40:16.383 via
		// org.apache.openjpa.reservedwords.ReservedWordsIT
		invalidColumnWordSet.addAll(Arrays.asList(new String[] { "ADD", "ALL", "ALTER", "ANALYZE", "AND", "AS", "ASC",
				"ASENSITIVE", "BEFORE", "BETWEEN", "BIGINT", "BINARY", "BLOB", "BOTH", "BY", "CALL", "CASCADE", "CASE",
				"CHANGE", "CHAR", "CHARACTER", "CHECK", "COLLATE", "COLUMN", "CONDITION", "CONSTRAINT", "CONTINUE",
				"CONVERT", "CREATE", "CROSS", "CURRENT_DATE", "CURRENT_TIME", "CURRENT_TIMESTAMP", "CURRENT_USER",
				"CURSOR", "DATABASE", "DATABASES", "DAY_HOUR", "DAY_MICROSECOND", "DAY_MINUTE", "DAY_SECOND", "DEC",
				"DECIMAL", "DECLARE", "DEFAULT", "DELAYED", "DELETE", "DESC", "DESCRIBE", "DETERMINISTIC", "DISTINCT",
				"DISTINCTROW", "DIV", "DOUBLE", "DROP", "DUAL", "EACH", "ELSE", "ELSEIF", "ENCLOSED", "END-EXEC",
				"ESCAPED", "EXISTS", "EXIT", "EXPLAIN", "FALSE", "FETCH", "FLOAT", "FLOAT4", "FLOAT8", "FOR", "FORCE",
				"FOREIGN", "FROM", "FULLTEXT", "GENERATED", "GET", "GRANT", "GROUP", "HAVING", "HIGH_PRIORITY",
				"HOUR_MICROSECOND", "HOUR_MINUTE", "HOUR_SECOND", "IF", "IGNORE", "IN", "INDEX", "INFILE", "INNER",
				"INOUT", "INSENSITIVE", "INSERT", "INT", "INT1", "INT2", "INT3", "INT4", "INT8", "INTEGER", "INTERVAL",
				"INTO", "IS", "ITERATE", "JOIN", "KEY", "KEYS", "KILL", "LEADING", "LEAVE", "LEFT", "LIKE", "LIMIT",
				"LINES", "LOAD", "LOCALTIME", "LOCALTIMESTAMP", "LOCK", "LONG", "LONGBLOB", "LONGTEXT", "LOOP",
				"LOW_PRIORITY", "MATCH", "MAXVALUE", "MEDIUMBLOB", "MEDIUMINT", "MEDIUMTEXT", "MIDDLEINT",
				"MINUTE_MICROSECOND", "MINUTE_SECOND", "MOD", "MODIFIES", "NATURAL", "NO_WRITE_TO_BINLOG", "NOT",
				"NULL", "NUMERIC", "ON", "OPTIMIZE", "OPTION", "OPTIONALLY", "OR", "ORDER", "OUT", "OUTER", "OUTFILE",
				"PARTITION", "PRECISION", "PRIMARY", "PROCEDURE", "PURGE", "RANGE", "READ", "READS", "REAL",
				"REFERENCES", "REGEXP", "RELEASE", "RENAME", "REPEAT", "REPLACE", "REQUIRE", "RESIGNAL", "RESTRICT",
				"RETURN", "REVOKE", "RIGHT", "RLIKE", "SCHEMA", "SCHEMAS", "SECOND_MICROSECOND", "SENSITIVE",
				"SEPARATOR", "SET", "SHOW", "SIGNAL", "SMALLINT", "SPATIAL", "SPECIFIC", "SQL", "SQL_BIG_RESULT",
				"SQL_CALC_FOUND_ROWS", "SQL_SMALL_RESULT", "SQLEXCEPTION", "SQLSTATE", "SQLWARNING", "SSL", "STARTING",
				"STRAIGHT_JOIN", "TABLE", "TERMINATED", "THEN", "TINYBLOB", "TINYINT", "TINYTEXT", "TO", "TRAILING",
				"TRIGGER", "TRUE", "UNDO", "UNION", "UNIQUE", "UNLOCK", "UNSIGNED", "UPDATE", "USAGE", "USE", "USING",
				"UTC_DATE", "UTC_TIME", "UTC_TIMESTAMP", "VALUES", "VARBINARY", "VARCHAR", "VARCHARACTER", "VARYING",
				"WHEN", "WHERE", "WHILE", "WITH", "WRITE", "XOR", "YEAR_MONTH", "ZEROFILL",
				// end generated.
				// the following keywords used to be defined as reserved words in the past, but
				// now seem to work
				// we still add them for compat reasons
				"INDEX", "SELECT" }));

		requiresSearchStringEscapeForLike = true;

		// MySQL requires double-escape for strings
		searchStringEscape = "\\\\";

		typeModifierSet.addAll(Arrays.asList(new String[] { "UNSIGNED", "ZEROFILL" }));

		setLeadingDelimiter(DELIMITER_BACK_TICK);
		setTrailingDelimiter(DELIMITER_BACK_TICK);

		fixedSizeTypeNameSet.remove("NUMERIC");

		dateFractionDigits = 0;
	}

	@Override
	public void connectedConfiguration(Connection conn) throws SQLException {
		super.connectedConfiguration(conn);

		DatabaseMetaData metaData = conn.getMetaData();
		int maj = 0;
		int min = 0;
		if (isJDBC3) {
			maj = metaData.getDatabaseMajorVersion();
			min = metaData.getDatabaseMinorVersion();
		} else {
			try {
				// The product version looks like 4.1.3-nt or 5.1.30
				String productVersion = metaData.getDatabaseProductVersion();
				int[] versions = getMajorMinorVersions(productVersion);
				maj = versions[0];
				min = versions[1];
			} catch (IllegalArgumentException e) {
				// we don't understand the version format.
				// That is ok. We just take the default values.
				if (log.isWarnEnabled())
					log.warn(e.toString(), e);
			}
		}
		if (maj < 4 || (maj == 4 && min < 1)) {
			supportsSubselect = false;
			allowsAliasInBulkClause = false;
			supportsForeignKeysComposite = false;
		}
		if (maj > 5 || (maj == 5 && min >= 1)) {
			supportsXMLColumn = true;
		}
		if (maj > 5 || (maj == 5 && min >= 7)) {
			// from this version on MySQL supports fractions of a second
			timestampTypeName = "DATETIME{0}";
			fixedSizeTypeNameSet.remove(timestampTypeName);
			fractionalTypeNameSet.add(timestampTypeName);
		}

		if (metaData.getDriverMajorVersion() < 5) {
			driverDeserializesBlobs = true;
		}
	}

//    @Override
//    public Connection decorate(Connection conn)  throws SQLException {
//        conn = super.decorate(conn);
//        String driver = conf.getConnectionDriverName();
//        if ("com.mysql.jdbc.ReplicationDriver".equals(driver))
//            conn.setReadOnly(true);
//        return conn;
//    }

	private static int[] getMajorMinorVersions(String versionStr) throws IllegalArgumentException {
		int beginIndex = 0;

		versionStr = versionStr.trim();
		char[] charArr = versionStr.toCharArray();
		for (int i = 0; i < charArr.length; i++) {
			if (Character.isDigit(charArr[i])) {
				beginIndex = i;
				break;
			}
		}

		int endIndex = charArr.length;
		for (int i = beginIndex + 1; i < charArr.length; i++) {
			if (charArr[i] != '.' && !Character.isDigit(charArr[i])) {
				endIndex = i;
				break;
			}
		}

		String[] arr = versionStr.substring(beginIndex, endIndex).split("\\.");
		if (arr.length < 2)
			throw new IllegalArgumentException();

		int maj = Integer.parseInt(arr[0]);
		int min = Integer.parseInt(arr[1]);
		return new int[] { maj, min };
	}

	@Override
	public String[] getCreateTableSQL(Table table) {
		String[] sql = super.getCreateTableSQL(table);
		if (!StringUtil.isEmpty(tableType))
			sql[0] = sql[0] + " ENGINE = " + tableType;
		return sql;
	}

	@Override
	public String[] getDropIndexSQL(Index index) {
		return new String[] { "DROP INDEX " + getFullName(index) + " ON " + getFullName(index.getTable(), false) };
	}

	/**
	 * Return <code>ALTER TABLE &lt;table name&gt; DROP PRIMARY KEY</code>.
	 */
	@Override
	public String[] getDropPrimaryKeySQL(PrimaryKey pk) {
		if (DBIdentifier.isNull(pk.getIdentifier()))
			return new String[0];
		return new String[] { "ALTER TABLE " + getFullName(pk.getTable(), false) + " DROP PRIMARY KEY" };
	}

	/**
	 * Return <code>ALTER TABLE &lt;table name&gt; DROP FOREIGN KEY
	 * &lt;fk name&gt;</code>.
	 */
	@Override
	public String[] getDropForeignKeySQL(ForeignKey fk, Connection conn) {
		if (DBIdentifier.isNull(fk.getIdentifier())) {
			DBIdentifier fkName = fk.loadIdentifierFromDB(this, conn);
			String[] retVal = (fkName == null) ? new String[0]
					: new String[] { "ALTER TABLE " + getFullName(fk.getTable(), false) + " DROP FOREIGN KEY "
							+ toDBName(fkName) };
			return retVal;
		}
		return new String[] { "ALTER TABLE " + getFullName(fk.getTable(), false) + " DROP FOREIGN KEY "
				+ toDBName(fk.getIdentifier()) };
	}

	@Override
	public String[] getAddPrimaryKeySQL(PrimaryKey pk) {
		String[] sql = super.getAddPrimaryKeySQL(pk);

		// mysql requires that a column be declared NOT NULL before
		// it can be made a primary key.
		Column[] cols = pk.getColumns();
		String[] ret = new String[cols.length + sql.length];
		for (int i = 0; i < cols.length; i++) {
			ret[i] = "ALTER TABLE " + getFullName(cols[i].getTable(), false) + " CHANGE "
					+ toDBName(cols[i].getIdentifier()) + " " + toDBName(cols[i].getIdentifier()) // name twice
					+ " " + getTypeName(cols[i]) + " NOT NULL";
		}

		System.arraycopy(sql, 0, ret, cols.length, sql.length);
		return ret;
	}

	@Override
	public String[] getDeleteTableContentsSQL(Table[] tables, Connection conn) {
		// mysql >= 4 supports more-optimal delete syntax
		if (!optimizeMultiTableDeletes)
			return super.getDeleteTableContentsSQL(tables, conn);
		else {
			StringBuilder buf = new StringBuilder(tables.length * 8);
			buf.append("DELETE FROM ");
			for (int i = 0; i < tables.length; i++) {
				buf.append(toDBName(tables[i].getFullIdentifier()));
				if (i < tables.length - 1)
					buf.append(", ");
			}
			return new String[] { buf.toString() };
		}
	}

	@Override
	protected void appendSelectRange(SQLBuffer buf, long start, long end, boolean subselect) {
		buf.append(" LIMIT ").appendValue(start).append(", ");
		if (end == Long.MAX_VALUE)
			buf.appendValue(Long.MAX_VALUE);
		else
			buf.appendValue(end - start);
	}

	@Override
	protected Column newColumn(ResultSet colMeta) throws SQLException {
		Column col = super.newColumn(colMeta);
		if (col.isNotNull() && "0".equals(col.getDefaultString()))
			col.setDefaultString(null);
		return col;
	}

	@Override
	public Object getBlobObject(ResultSet rs, int column, JDBCStore store) throws SQLException {
		// if the user has set a get-blob strategy explicitly or the driver
		// does not automatically deserialize, delegate to super
		if (useGetBytesForBlobs || useGetObjectForBlobs || !driverDeserializesBlobs)
			return super.getBlobObject(rs, column, store);

		// most mysql drivers deserialize on getObject
		return rs.getObject(column);
	}

	@Override
	public int getPreferredType(int type) {
		if (type == Types.CLOB && !useClobs) {
			return Types.LONGVARCHAR;
		} else if (type == Types.TIME_WITH_TIMEZONE) {
			// MySQL doesn't support SQL-2003 'WITH TIMEZONE' nor the respective JDBC types.
			return Types.TIME;
		} else if (type == Types.TIMESTAMP_WITH_TIMEZONE) {
			// MySQL doesn't support SQL-2003 'WITH TIMEZONE' nor the respective JDBC types.
			return Types.TIMESTAMP;
		}

		return super.getPreferredType(type);
	}

	/**
	 * Append XML comparison.
	 *
	 * @param buf    the SQL buffer to write the comparison
	 * @param op     the comparison operation to perform
	 * @param lhs    the left hand side of the comparison
	 * @param rhs    the right hand side of the comparison
	 * @param lhsxml indicates whether the left operand maps to XML
	 * @param rhsxml indicates whether the right operand maps to XML
	 */
	@Override
	public void appendXmlComparison(SQLBuffer buf, String op, FilterValue lhs, FilterValue rhs, boolean lhsxml,
			boolean rhsxml) {
		super.appendXmlComparison(buf, op, lhs, rhs, lhsxml, rhsxml);
		if (lhsxml)
			appendXmlValue(buf, lhs);
		else
			lhs.appendTo(buf);
		buf.append(" ").append(op).append(" ");
		if (rhsxml)
			appendXmlValue(buf, rhs);
		else
			rhs.appendTo(buf);
	}

	/**
	 * Append XML column value so that it can be used in comparisons.
	 *
	 * @param buf the SQL buffer to write the value
	 * @param val the value to be written
	 */
	private void appendXmlValue(SQLBuffer buf, FilterValue val) {
		buf.append("ExtractValue(").append(val.getColumnAlias(val.getFieldMapping().getColumns()[0])).append(",'/*/");
		val.appendTo(buf);
		buf.append("')");
	}

	@Override
	public int getBatchFetchSize(int batchFetchSize) {
		return Integer.MIN_VALUE;
	}

	/**
	 * Check to see if we have set the {@link #SELECT_HINT} in the fetch
	 * configuration, and if so, append the MySQL hint after the "SELECT" part of
	 * the query.
	 */
	@Override
	public String getSelectOperation(JDBCFetchConfiguration fetch) {
		Object hint = fetch == null ? null : fetch.getHint(SELECT_HINT);
		String select = "SELECT";
		if (hint != null)
			select += " " + hint;
		return select;
	}

	@Override
	protected Collection<String> getSelectTableAliases(Select sel) {
		Set<String> result = new HashSet<>();
		List<String> selects = sel.getIdentifierAliases();
		for (String s : selects) {
			String tableAlias = s.substring(0, s.indexOf('.'));
			result.add(tableAlias);
		}
		return result;
	}

//    @Override
//    protected int matchErrorState(Map<Integer,Set<String>> errorStates, SQLException ex) {
//        int state = super.matchErrorState(errorStates, ex);
//        // OPENJPA-1616 - Special case for MySQL not returning a SQLState for timeouts
//        if (state == ExceptionInfo.GENERAL && ex.getErrorCode() == 0 && ex.getSQLState() == null) {
//            // look at the nested MySQL exception for more details
//            SQLException sqle = ex.getNextException();
//            if (sqle != null && sqle.toString().startsWith("com.mysql.jdbc.exceptions.MySQLTimeoutException")) {
//                if (conf != null && conf.getLockTimeout() != -1) {
//                    state = StoreException.LOCK;
//                } else {
//                    state = StoreException.QUERY;
//                }
//            }
//        }
//        return state;
//    }

	@Override
	public boolean isFatalException(int subtype, SQLException ex) {
		if ((subtype == StoreException.LOCK && ex.getErrorCode() == 1205)
				|| (subtype == StoreException.QUERY && ex.getErrorCode() == 1317)) {
			return false;
		}
		if (ex.getErrorCode() == 0 && ex.getSQLState() == null)
			return false;
		return super.isFatalException(subtype, ex);
	}

	/**
	 * OPENJPA-740 Special case for MySql special column types, like LONGTEXT,
	 * LONGBLOG etc..
	 * 
	 * @see org.apache.openjpa.jdbc.sql.DBDictionary#getTypeName(org.apache.openjpa.jdbc.schema.Column)
	 */
	@Override
	public String getTypeName(Column col) {
		// handle blobs differently, if the DBItentifierType is NULL (e.g. no column
		// definition is set).
		if (col.getType() == Types.BLOB && col.getTypeIdentifier().getType() == DBIdentifierType.NULL) {
			if (col.getSize() <= 0) // unknown size
				return blobTypeName; // return old default of 64KB
			else if (col.getSize() <= 255)
				return tinyBlobTypeName;
			else if (col.getSize() <= 65535)
				return blobTypeName; // old default of 64KB
			else if (col.getSize() <= 16777215)
				return mediumBlobTypeName;
			else
				return longBlobTypeName;
		} else if (col.getType() == Types.CLOB && col.getTypeIdentifier().getType() == DBIdentifierType.NULL) {
			if (col.getSize() <= 0) // unknown size
				return clobTypeName; // return old default of 64KB
			else if (col.getSize() <= 255)
				return tinyTextTypeName;
			else if (col.getSize() <= 65535)
				return clobTypeName; // old default of 64KB
			else if (col.getSize() <= 16777215)
				return mediumTextTypeName;
			else
				return longTextTypeName;
		} else {
			return super.getTypeName(col);
		}
	}

	@Override
	public void indexOf(SQLBuffer buf, FilterValue str, FilterValue find, FilterValue start) {
		buf.append("LOCATE(");
		find.appendTo(buf);
		buf.append(", ");
		str.appendTo(buf);
		if (start != null) {
			buf.append(", ");
			start.appendTo(buf);
		}
		buf.append(")");
	}
}
