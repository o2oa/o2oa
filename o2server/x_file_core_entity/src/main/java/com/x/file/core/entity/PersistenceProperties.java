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

		public static class Attachment2 {
			public static final String table = "FILE_ATTACHMENT2";
		}

		public static class Folder2 {
			public static final String table = "FILE_FOLDER2";
		}

		public static class Share {
			public static final String table = "FILE_SHARE";
		}

		public static class Recycle {
			public static final String table = "FILE_RECYCLE";
		}
	}

	public static class Open {
		public static class File {
			public static final String table = "FILE_FILE";
		}
		public static class OriginFile {
			public static final String table = "FILE_ORIGINFILE";
		}
		public static class Link {
			public static final String table = "FILE_LINK";
		}
		public static class FileConfig {
			public static final String table = "FILE_CONFIG";
		}

	}

}