package com.x.base.core.openjpa.jdbc.sql;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import org.apache.openjpa.jdbc.identifier.DBIdentifier;
import org.apache.openjpa.jdbc.identifier.DBIdentifier.DBIdentifierType;
import org.apache.openjpa.jdbc.kernel.exps.FilterValue;
import org.apache.openjpa.jdbc.schema.Column;
import org.apache.openjpa.jdbc.schema.ForeignKey;
import org.apache.openjpa.jdbc.schema.Index;
import org.apache.openjpa.jdbc.schema.PrimaryKey;
import org.apache.openjpa.jdbc.schema.Table;
import org.apache.openjpa.jdbc.sql.DBDictionary;
import org.apache.openjpa.jdbc.sql.InformixDictionary;
import org.apache.openjpa.jdbc.sql.SQLBuffer;
import org.apache.openjpa.jdbc.sql.Select;
import org.apache.openjpa.lib.util.Localizer;
import org.apache.openjpa.util.StoreException;
import org.apache.openjpa.util.UnsupportedException;

public class GBaseDictionary extends DBDictionary {
	public static final String VENDOR_OTHER = "gbase";

	/**
	 * If true, then we will issue a "SET LOCK MODE TO WAIT N" statement whenever we
	 * create a {@link Connection}, in order allow waiting on locks.
	 */
	public boolean lockModeEnabled = false;

	/**
	 * If {@link #lockModeEnabled} is <code>true</code>, then this parameter
	 * specifies the number of seconds we will wait to obtain a lock for inserts and
	 * pessimistic locking.
	 */
	public int lockWaitSeconds = 30;

	/**
	 * Informix JDBC metadata for all known drivers returns with the table catalog
	 * and the table schema name swapped. A <code>true</code> value for this
	 * property indicates that they should be reversed.
	 */
	public boolean swapSchemaAndCatalog = true;

	protected boolean useJCC = false;

	public boolean disableRetainUpdateLocksSQL = false;

	private static final Localizer _loc = Localizer.forPackage(InformixDictionary.class);

	public GBaseDictionary() {
		platform = "gbase";
		validationSQL = "SELECT 1 FROM DUAL";
		supportsDeferredConstraints = false;
		constraintNameMode = CONS_NAME_AFTER;

		useGetStringForClobs = true;
		longVarcharTypeName = "TEXT";
		clobTypeName = "TEXT";
		smallintTypeName = "INT8";
		tinyintTypeName = "INT8";
		floatTypeName = "FLOAT";
		bitTypeName = "BOOLEAN";
		blobTypeName = "BYTE";
		doubleTypeName = "NUMERIC(32,20)";
		dateTypeName = "DATE";
		timeTypeName = "DATETIME HOUR TO SECOND";
		timestampTypeName = "DATETIME YEAR TO FRACTION(3)";
		doubleTypeName = "NUMERIC(32,20)";
		floatTypeName = "REAL";
		bigintTypeName = "NUMERIC(32,0)";
		doubleTypeName = "DOUBLE PRECISION";
		fixedSizeTypeNameSet.addAll(
				Arrays.asList(new String[] { "BYTE", "DOUBLE PRECISION", "INTERVAL", "SMALLFLOAT", "TEXT", "INT8", }));

		// OpenJPA-2045: NAME has been removed from common reserved words to
		// only specific dictionaries
		reservedWordSet.add("NAME");

		supportsLockingWithDistinctClause = false;
		supportsLockingWithMultipleTables = false;
		supportsLockingWithOrderClause = false;

		// the informix JDBC drivers have problems with using the
		// schema name in reflection on columns and tables
		supportsSchemaForGetColumns = false;
		supportsSchemaForGetTables = false;

		// Informix doesn't support aliases in deletes if the table has an index
		allowsAliasInBulkClause = false;

		// Informix doesn't understand "X CROSS JOIN Y", but it does understand
		// the equivalent "X JOIN Y ON 1 = 1"
		crossJoinClause = "JOIN";
		requiresConditionForCrossJoin = true;

		concatenateFunction = "CONCAT({0},{1})";
		nextSequenceQuery = "SELECT {0}.NEXTVAL FROM SYSTABLES WHERE TABID=1";
		supportsCorrelatedSubselect = false;
		swapSchemaAndCatalog = false;

		// Informix does not support foreign key delete action NULL or DEFAULT
		supportsNullDeleteAction = false;
		supportsDefaultDeleteAction = false;

		trimSchemaName = true;
	}

	@Override
	public Column[] getColumns(DatabaseMetaData meta, String catalog, String schemaName, String tableName,
			String columnName, Connection conn) throws SQLException {
		return getColumns(meta, DBIdentifier.newCatalog(catalog), DBIdentifier.newSchema(schemaName),
				DBIdentifier.newTable(tableName), DBIdentifier.newColumn(columnName), conn);
	}

	@Override
	public Column[] getColumns(DatabaseMetaData meta, DBIdentifier catalog, DBIdentifier schemaName,
			DBIdentifier tableName, DBIdentifier columnName, Connection conn) throws SQLException {
		Column[] cols = super.getColumns(meta, catalog, schemaName, tableName, columnName, conn);

		// treat logvarchar as clob
		for (int i = 0; cols != null && i < cols.length; i++)
			if (cols[i].getType() == Types.LONGVARCHAR)
				cols[i].setType(Types.CLOB);
		return cols;
	}

	@Override
	public Column newColumn(ResultSet colMeta) throws SQLException {
		Column col = super.newColumn(colMeta);
		if (swapSchemaAndCatalog)
			col.setSchemaIdentifier(fromDBName(colMeta.getString("TABLE_CAT"), DBIdentifierType.CATALOG));
		return col;
	}

	@Override
	public PrimaryKey newPrimaryKey(ResultSet pkMeta) throws SQLException {
		PrimaryKey pk = super.newPrimaryKey(pkMeta);
		if (swapSchemaAndCatalog)
			pk.setSchemaIdentifier(fromDBName(pkMeta.getString("TABLE_CAT"), DBIdentifierType.CATALOG));
		return pk;
	}

	@Override
	public Index newIndex(ResultSet idxMeta) throws SQLException {
		Index idx = super.newIndex(idxMeta);
		if (swapSchemaAndCatalog)
			idx.setSchemaIdentifier(fromDBName(idxMeta.getString("TABLE_CAT"), DBIdentifierType.CATALOG));
		return idx;
	}

	@Override
	public void setBoolean(PreparedStatement stmnt, int idx, boolean val, Column col) throws SQLException {
		// informix actually requires that a boolean be set: it cannot
		// handle a numeric argument
		stmnt.setString(idx, val ? "t" : "f");
	}

	@Override
	public String[] getCreateTableSQL(Table table) {
		String[] create = super.getCreateTableSQL(table);
		create[0] = create[0] + " LOCK MODE ROW";
		return create;
	}

	@Override
	public String[] getAddPrimaryKeySQL(PrimaryKey pk) {
		String pksql = getPrimaryKeyConstraintSQL(pk);
		if (pksql == null)
			return new String[0];
		return new String[] { "ALTER TABLE " + getFullName(pk.getTable(), false) + " ADD CONSTRAINT " + pksql };
	}

	@Override
	public String[] getAddForeignKeySQL(ForeignKey fk) {
		String fksql = getForeignKeyConstraintSQL(fk);
		if (fksql == null)
			return new String[0];
		return new String[] { "ALTER TABLE " + getFullName(fk.getTable(), false) + " ADD CONSTRAINT " + fksql };
	}

	@Override
	public boolean supportsRandomAccessResultSet(Select sel, boolean forUpdate) {
		return !forUpdate && !sel.isLob() && super.supportsRandomAccessResultSet(sel, forUpdate);
	}

	@Override
	public Connection decorate(Connection conn) throws SQLException {
		conn = super.decorate(conn);
		if (isJDBC3 && conn.getHoldability() != ResultSet.HOLD_CURSORS_OVER_COMMIT) {
			conn.setHoldability(ResultSet.HOLD_CURSORS_OVER_COMMIT);
			if (log.isTraceEnabled()) {
				log.trace(_loc.get("connection-defaults",
						new Object[] { conn.getAutoCommit(), conn.getHoldability(), conn.getTransactionIsolation() }));
			}
		}

//		// if we haven't already done so, initialize the lock mode of the
//		// connection
//		if (_seenConnections.add(conn)) {
//			if (lockModeEnabled) {
//				String sql = "SET LOCK MODE TO WAIT";
//				if (lockWaitSeconds > 0)
//					sql = sql + " " + lockWaitSeconds;
//				execute(sql, conn, true);
//			}
//
//			if (!disableRetainUpdateLocksSQL) {
//				String sql = "SET ENVIRONMENT RETAINUPDATELOCKS 'ALL'";
//				execute(sql, conn, false);
//			}
//		}

		// the datadirect driver requires that we issue a rollback before using
		// each connection
		if (VENDOR_DATADIRECT.equalsIgnoreCase(driverVendor))
			try {
				conn.rollback();
			} catch (SQLException se) {
			}
		return conn;
	}

	@Override
	public void indexOf(SQLBuffer buf, FilterValue str, FilterValue find, FilterValue start) {
		throw new UnsupportedException(_loc.get("function-not-supported", getClass(), "LOCATE"));
	}

	@Override
	public boolean needsToCreateIndex(Index idx, Table table) {
		// Informix will automatically create a unique index for the
		// primary key, so don't create another index again
		PrimaryKey pk = table.getPrimaryKey();
		if (pk != null && idx.columnsMatch(pk.getColumns()))
			return false;
		return true;
	}

	public boolean useJCC() {
		return useJCC;
	}

	/**
	 * Return DB specific schemaCase
	 */
	@Override
	public String getSchemaCase() {
		return schemaCase;
	}

	/**
	 * Specialized matchErrorState method for Informix. Informix exceptions are
	 * typically nested multiple levels deep. Correct determination of the exception
	 * type requires inspection of nested exceptions to determine the root cause. A
	 * list of Informix (IDS v10) error codes can be found here:
	 *
	 * http://publib.boulder.ibm.com/infocenter/idshelp/v10/index.jsp?topic=/com.ibm.em.doc/errors_ids100.html
	 *
	 * @param errorStates classification of SQL error states by their specific
	 *                    nature. The keys of the map represent one of the constants
	 *                    defined in {@link StoreException}. The value corresponding
	 *                    to a key represent the set of SQL Error States
	 *                    representing specific category of database error. This
	 *                    supplied map is sourced from
	 *                    <code>sql-error-state-codes.xml</xml> and filtered the
	 *                    error states for the current database.
	 *
	 * @param ex          original SQL Exception as raised by the database driver.
	 *
	 * @return A constant indicating the category of error as defined in
	 *         {@link StoreException}.
	 */
	@Override
	protected int matchErrorState(Map<Integer, Set<String>> errorStates, SQLException ex) {
		// Informix SQLState IX000 is a general SQLState that applies to many possible
		// conditions
		// If the underlying cause is also an IX000 with error code:
		// -107 ISAM error: record is locked. || -154 ISAM error: Lock Timeout Expired.
		// the exception type is LOCK.
		if (checkNestedErrorCodes(ex, "IX000", -107, -154)) {
			return StoreException.LOCK;
		}
		return super.matchErrorState(errorStates, ex);
	}

	private boolean checkNestedErrorCodes(SQLException ex, String sqlState, int... errorCodes) {
		SQLException cause = ex;
		int level = 0;
		// Query at most 5 exceptions deep to prevent infinite iteration exception loops
		// Typically, the root exception is at level 3.
		while (cause != null && level < 5) {
			String errorState = cause.getSQLState();
			if (sqlState == null || sqlState.equals(errorState)) {
				for (int ec : errorCodes) {
					if (cause.getErrorCode() == ec) {
						return true;
					}
				}
			}
			cause = cause.getNextException();
			level++;
		}
		return false;
	}
}
