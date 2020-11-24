package com.x.general.core.entity;

import com.x.base.core.entity.AbstractPersistenceProperties;

public final class PersistenceProperties extends AbstractPersistenceProperties {

	public static class Area {

		public static class District {
			public static final String table = "GEN_ARA_DISTRICT";
		}

	}

	public static class GeneralFile {
		public static final String table = "GEN_GENERAL_FILE";

	}
}
