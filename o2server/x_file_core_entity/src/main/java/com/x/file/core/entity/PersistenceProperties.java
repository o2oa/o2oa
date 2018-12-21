package com.x.file.core.entity;

import com.x.base.core.entity.AbstractPersistenceProperties;

public final class PersistenceProperties extends AbstractPersistenceProperties {

	public static class Personal {
		public static class Attachment {
			public static final String table = "FILE_ATTACHMENT";
		}

		public static class Folder {
			public static final String table = "FILE_FOLDER";
		}
	}

	public static class Open {
		public static class File {
			public static final String table = "FILE_FILE";
		}

	}

}