package com.x.base.core.openjpa.jdbc.sql;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import org.apache.openjpa.jdbc.sql.DBDictionary;
import org.apache.openjpa.lib.util.Localizer;

public class KingbaseDictionary extends DBDictionary {

	public static final String VENDOR_DAMENG = "Kingbase";
	private static final Localizer _loc = Localizer.forPackage(KingbaseDictionary.class);

	public KingbaseDictionary() {
		this.platform = "Kingbase";
		supportsDeferredConstraints = false;
	}

	public void connectedConfiguration(Connection conn) throws SQLException {
		super.connectedConfiguration(conn);
		boolean requiresWarnings = true;
		DatabaseMetaData meta = conn.getMetaData();
		String driverName = meta.getDriverName();
		String url = meta.getURL();
		if (this.driverVendor == null) {
			if ((driverName != null) && (driverName.equalsIgnoreCase("com.kingbase.Driver"))) {
				this.driverVendor = "KingbaseJdbcDriver";
				if ((url != null) && (url.startsWith("jdbc:kingbase://"))) {
					requiresWarnings = false;
				}
			} else {
				this.driverVendor = "other";
			}
		}
		if (("KingbaseJdbcDriver".equalsIgnoreCase(this.driverVendor)) && (requiresWarnings)) {
			this.log.warn(_loc.get("kingbase Jdbc connection", url));
		}
	}

}
