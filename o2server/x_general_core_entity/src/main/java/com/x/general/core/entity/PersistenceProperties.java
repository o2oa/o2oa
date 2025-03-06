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

	public static class Invoice {
		public static final String table = "GEN_INVOICE";

	}

	public static class ApplicationDict {
		public static final String table = "GEN_DICT";
	}

	public static class ApplicationDictItem {
		public static final String table = "GEN_DICT_ITEM";
	}
}
