package com.x.base.core.openjpa.jdbc.sql;

import org.apache.openjpa.jdbc.sql.BooleanRepresentationFactory;
import org.apache.openjpa.jdbc.sql.DBDictionary;
import org.apache.openjpa.lib.util.Localizer;

public class OscarDictionary extends DBDictionary {

	public static final String VENDOR_OSCAR = "Oscar";
//	private static final Localizer _loc = Localizer.forPackage(OscarDictionary.class);

	public OscarDictionary() {
		this.platform = VENDOR_OSCAR;
		supportsDeferredConstraints = false;
		maxTableNameLength = 128;
		maxColumnNameLength = 128;
		maxIndexNameLength = 128;
		maxConstraintNameLength = 128;
		maxEmbeddedClobSize = -1;
		maxEmbeddedBlobSize = -1;
		doubleTypeName = "DOUBLE PRECISION";
		booleanTypeName = "BOOL";
		useGetStringForClobs = true;
		useSetStringForClobs = true;
		booleanRepresentation = BooleanRepresentationFactory.BOOLEAN;
	}

}
