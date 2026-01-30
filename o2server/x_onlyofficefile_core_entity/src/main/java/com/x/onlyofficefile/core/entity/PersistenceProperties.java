package com.x.onlyofficefile.core.entity;

import com.x.base.core.entity.AbstractPersistenceProperties;

public final class PersistenceProperties extends AbstractPersistenceProperties {

	public static class OnlyOfficeFile {
		public static final String table = "ONLYOFFICE_FILE";
	}

	public static class Template {
		public static final String table = "ONLYOFFICE_FILE_TEMPLATE";
	}

	public static class Seal {
		public static final String table = "ONLYOFFICE_FILE_SEAL";
	}

	public static class OnlyOfficeFileVersion {
		public static final String table = "ONLYOFFICE_FILE_VERSION";
	}

}
