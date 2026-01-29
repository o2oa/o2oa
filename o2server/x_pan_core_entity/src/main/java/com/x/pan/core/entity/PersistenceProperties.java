package com.x.pan.core.entity;

import com.x.base.core.entity.AbstractPersistenceProperties;

public final class PersistenceProperties extends AbstractPersistenceProperties {

	public static class SampleEntityClassName {
		public static final String table = "CUSTOM_SAMPLE";
	}

	public static class Attachment3 {
		public static final String table = "FILE_ATTACHMENT3";
	}

	public static class AttachmentVersion {
		public static final String table = "FILE_ATTACHMENT_VERSION";
	}

	public static class Folder3 {
		public static final String table = "FILE_FOLDER3";
	}

	public static class Recycle3 {
		public static final String table = "FILE_RECYCLE3";
	}

	public static class ZonePermission {
		public static final String table = "FILE_ZONE_PERMISSION";
	}

	public static class Favorite {
		public static final String table = "FILE_FAVORITE";
	}

	public static class LockInfo {
		public static final String table = "FILE_LOCK_INFO";
	}

	public static class FileConfig3 {
		public static final String table = "FILE_FILECONFIG3";
	}
}
