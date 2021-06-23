package com.x.base.core.openjpa.jdbc.sql;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.openjpa.jdbc.identifier.DBIdentifier;
import org.apache.openjpa.jdbc.identifier.DBIdentifier.DBIdentifierType;
import org.apache.openjpa.jdbc.schema.Column;
import org.apache.openjpa.jdbc.schema.ForeignKey;
import org.apache.openjpa.jdbc.schema.Index;
import org.apache.openjpa.jdbc.schema.PrimaryKey;
import org.apache.openjpa.jdbc.schema.Table;
import org.apache.openjpa.jdbc.sql.DBDictionary;
import org.apache.openjpa.jdbc.sql.Select;

public class GBaseDictionary extends DBDictionary {
	public static final String VENDOR_OTHER = "gbase";

	public boolean swapSchemaAndCatalog = true;

	public GBaseDictionary() {
		platform = VENDOR_OTHER;
		validationSQL = "SELECT 1 FROM DUAL";
		supportsDeferredConstraints = false;
		constraintNameMode = CONS_NAME_AFTER;

		useGetStringForClobs = true;
		longVarcharTypeName = "CLOB";
		clobTypeName = "CLOB";
		smallintTypeName = "INTEGER";
		tinyintTypeName = "INTEGER";
		floatTypeName = "FLOAT";
		bitTypeName = "BOOLEAN";
		blobTypeName = "BYTE";
		doubleTypeName = "NUMERIC(32,20)";
		dateTypeName = "DATE";
		timeTypeName = "DATETIME HOUR TO SECOND";
		timestampTypeName = "DATETIME YEAR TO FRACTION(5)";
		doubleTypeName = "NUMERIC(32,20)";
		bigintTypeName = "NUMERIC(32,0)";
		doubleTypeName = "DOUBLE PRECISION";
		booleanTypeName = "BOOLEAN";
		fixedSizeTypeNameSet.addAll(Arrays.asList(new String[] { "BIT", "DISTINCT", "JAVA_OBJECT", "NULL", "OTHER",
				"REAL", "REF", "SMALLINT", "STRUCT", "TINYINT", }));

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
	public void setDate(PreparedStatement stmnt, int idx, Date val, Column col) throws SQLException {
		if (col != null && col.getType() == Types.DATE) {
			if (null != val) {
				val = DateUtils.setMilliseconds(val, 0);
				val = DateUtils.setSeconds(val, 0);
				val = DateUtils.setMinutes(val, 0);
				val = DateUtils.setHours(val, 0);
			}
			setDate(stmnt, idx, new java.sql.Date(val.getTime()), null, col);
		} else if (col != null && col.getType() == Types.TIME) {
			if (null != val) {
				val = DateUtils.setYears(val, 1970);
				val = DateUtils.setMonths(val, 0);
				val = DateUtils.setDays(val, 1);
			}
			setTime(stmnt, idx, new Time(val.getTime()), null, col);
		} else if (val instanceof Timestamp) {
			setTimestamp(stmnt, idx, (Timestamp) val, null, col);
		} else {
			setTimestamp(stmnt, idx, new Timestamp(val.getTime()), null, col);
		}
	}

	@Override
	public Date getDate(ResultSet rs, int column) throws SQLException {
		Timestamp tstamp = getTimestamp(rs, column, null);
		if (tstamp == null) {
			return null;
		}
		long millis = (tstamp.getTime() / 1000L) * 1000L;
		return new Date(millis);
	}

	@Override
	public Time getTime(ResultSet rs, int column, Calendar cal) throws SQLException {
		Time time = null;
		if (cal == null) {
			time = rs.getTime(column);
		} else {
			time = rs.getTime(column, cal);
		}
		if (null != time) {
			Calendar calendar = DateUtils.toCalendar(time);
			calendar.set(Calendar.YEAR, 1970);
			calendar.set(Calendar.MONTH, 0);
			calendar.set(Calendar.DATE, 1);
			time = new Time(calendar.getTime().getTime());
		}
		return time;
	}

	/**
	 * Convert the specified column of the SQL ResultSet to the proper java type.
	 */
	@Override
	public Timestamp getTimestamp(ResultSet rs, int column, Calendar cal) throws SQLException {
		if (cal == null) {
			return rs.getTimestamp(column);
		} else {
			return rs.getTimestamp(column, cal);
		}
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
	public boolean needsToCreateIndex(Index idx, Table table) {
		// Informix will automatically create a unique index for the
		// primary key, so don't create another index again
		PrimaryKey pk = table.getPrimaryKey();
		if (pk != null && idx.columnsMatch(pk.getColumns()))
			return false;
		return true;
	}

}
