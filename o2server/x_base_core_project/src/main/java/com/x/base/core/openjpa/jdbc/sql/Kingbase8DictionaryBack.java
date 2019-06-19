package com.x.base.core.openjpa.jdbc.sql;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.openjpa.jdbc.schema.Column;
import org.apache.openjpa.jdbc.sql.BooleanRepresentation;
import org.apache.openjpa.jdbc.sql.BooleanRepresentationFactory;
import org.apache.openjpa.jdbc.sql.DBDictionary;
import org.apache.openjpa.lib.util.Localizer;

public class Kingbase8DictionaryBack extends DBDictionary {

	public static final String VENDOR_DAMENG = "Kingbase8";
	private static final Localizer _loc = Localizer.forPackage(DMDictionary.class);

	public Kingbase8DictionaryBack() {
		this.platform = "Kingbase8";
		supportsDeferredConstraints = false;
	}

	public void connectedConfiguration(Connection conn) throws SQLException {
		super.connectedConfiguration(conn);
		boolean requiresWarnings = true;
		DatabaseMetaData meta = conn.getMetaData();
		String driverName = meta.getDriverName();
		String url = meta.getURL();
		if (this.driverVendor == null) {
			if ((driverName != null) && (driverName.equalsIgnoreCase("com.kingbase8.Driver"))) {
				this.driverVendor = "Kingbase8JdbcDriver";
				if ((url != null) && (url.startsWith("jdbc:kingbase8://"))) {
					requiresWarnings = false;
				}
			} else {
				this.driverVendor = "other";
			}
		}
		if (("Kingbase8JdbcDriver".equalsIgnoreCase(this.driverVendor)) && (requiresWarnings)) {
			this.log.warn(_loc.get("kingbase8 Jdbc connection", url));
		}
	}

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

	public String booleanTypeName = "BOOL";

}
