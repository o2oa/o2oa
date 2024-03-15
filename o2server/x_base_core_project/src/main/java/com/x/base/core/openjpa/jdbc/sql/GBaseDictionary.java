package com.x.base.core.openjpa.jdbc.sql;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Collection;
import java.util.Collections;

import org.apache.openjpa.jdbc.identifier.DBIdentifier;
import org.apache.openjpa.jdbc.schema.Column;
import org.apache.openjpa.jdbc.sql.BooleanRepresentationFactory;
import org.apache.openjpa.jdbc.sql.DBDictionary;
import org.apache.openjpa.jdbc.sql.InformixDictionary;
import org.apache.openjpa.jdbc.sql.SQLExceptions;
import org.apache.openjpa.lib.util.Localizer;
//import org.apache.openjpa.jdbc.sql.InformixDictionary;
import org.apache.openjpa.lib.util.ReferenceHashSet;
import org.apache.openjpa.lib.util.collections.AbstractReferenceMap;

public class GBaseDictionary extends DBDictionary {
	public static final String VENDOR_OTHER = "gbase";

	public final boolean swapSchemaAndCatalog = false;

	private static final Localizer _loc = Localizer.forPackage(GBaseDictionary.class);

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

	// 由decorate方法调用
	public boolean disableRetainUpdateLocksSQL = false;

	// 由decorate方法调用
	// weak set of connections we've already executed lock mode sql on
	private final Collection _seenConnections = Collections
			.synchronizedSet(new ReferenceHashSet(AbstractReferenceMap.ReferenceStrength.WEAK));

	public GBaseDictionary() {
		platform = VENDOR_OTHER;
		supportsDeferredConstraints = false;
		constraintNameMode = CONS_NAME_AFTER;
		useGetStringForClobs = true;
		longVarcharTypeName = "CLOB";
		clobTypeName = "CLOB";
		booleanTypeName = "BOOLEAN";
		smallintTypeName = "INTEGER";
		tinyintTypeName = "INTEGER";
		bigintTypeName = "NUMERIC(32,0)";
		floatTypeName = "FLOAT";
		doubleTypeName = "DOUBLE PRECISION";
		bitTypeName = "BOOLEAN";
		blobTypeName = "BLOB";
		dateTypeName = "DATE";
		timeTypeName = "DATETIME HOUR TO SECOND";
		timestampTypeName = "DATETIME YEAR TO FRACTION(5)";
		// 避免字段名前后多一个"\"""
		supportsDelimitedIdentifiers = false;
		// 设置Boolean使用类型
		booleanRepresentation = BooleanRepresentationFactory.BOOLEAN;
		supportsLockingWithDistinctClause = false;
		supportsLockingWithMultipleTables = false;
		supportsLockingWithOrderClause = false;

		crossJoinClause = "JOIN";
		requiresConditionForCrossJoin = true;

		concatenateFunction = "CONCAT({0},{1})";
		nextSequenceQuery = "SELECT {0}.NEXTVAL FROM SYSTABLES WHERE TABID=1";
		supportsCorrelatedSubselect = false;
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

		for (int i = 0; cols != null && i < cols.length; i++) {
			// treat logvarchar as clob
			if (cols[i].getType() == Types.LONGVARCHAR) {
				cols[i].setType(Types.CLOB);
			}
			// 不知道为什么类型16的boolean识别为bit,显示为unknown(16)强制转换过去
			if (cols[i].getType() == Types.BOOLEAN) {
				cols[i].setType(Types.BIT);
			}
		}
		return cols;
	}

	// 必须重写这个方法,否则错误信息
	// <openjpa-3.2.0-r6f721f6 fatal general error>
	// org.apache.openjpa.persistence.PersistenceException: 尚未打开游标。
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

		// if we haven't already done so, initialize the lock mode of the
		// connection
		if (_seenConnections.add(conn)) {
			if (lockModeEnabled) {
				String sql = "SET LOCK MODE TO WAIT";
				if (lockWaitSeconds > 0)
					sql = sql + " " + lockWaitSeconds;
				execute(sql, conn, true);
			}

			if (!disableRetainUpdateLocksSQL) {
				String sql = "SET ENVIRONMENT RETAINUPDATELOCKS 'ALL'";
				execute(sql, conn, false);
			}
		}

		// the datadirect driver requires that we issue a rollback before using
		// each connection
		if (VENDOR_DATADIRECT.equalsIgnoreCase(driverVendor))
			try {
				conn.rollback();
			} catch (SQLException se) {
			}
		return conn;
	}

	// 由decorate方法调用
	private void execute(String sql, Connection conn, boolean throwExc) {
		Statement stmnt = null;
		try {
			stmnt = conn.createStatement();
			stmnt.executeUpdate(sql);
		} catch (SQLException se) {
			if (throwExc)
				throw SQLExceptions.getStore(se, this);
			else {
				if (log.isTraceEnabled())
					log.trace(_loc.get("can-not-execute", sql));
			}
		} finally {
			if (stmnt != null)
				try {
					stmnt.close();
				} catch (SQLException se) {
				}
		}
	}
}
