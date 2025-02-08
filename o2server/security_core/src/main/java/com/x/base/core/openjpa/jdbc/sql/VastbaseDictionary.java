package com.x.base.core.openjpa.jdbc.sql;

import org.apache.openjpa.jdbc.sql.PostgresDictionary;

public class VastbaseDictionary extends PostgresDictionary {
	public static final String VENDOR_DAMENG = "vastbase";

	public VastbaseDictionary() {
		this.schemaCase = SCHEMA_CASE_PRESERVE;
	}

}
