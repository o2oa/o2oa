package com.x.meeting.core.entity;

import com.x.base.core.entity.AbstractPersistenceProperties;
import com.x.base.core.entity.JpaObject;

public final class PersistenceProperties extends AbstractPersistenceProperties {

	public static final int length_unique = JpaObject.length_255B;

	public static class Room {
		public static final String table = "MT_ROOM";
	}

	public static class Building {
		public static final String table = "MT_BUILDING";
	}

	public static class Meeting {
		public static final String table = "MT_MEETING";
	}

	public static class Attachment {
		public static final String table = "MT_ATTACHMENT";
	}

	public static class MeetingConfig {
		public static final String table = "MT_CONFIG";
	}
}
