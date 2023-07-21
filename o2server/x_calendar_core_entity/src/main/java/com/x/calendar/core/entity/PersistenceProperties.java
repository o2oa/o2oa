package com.x.calendar.core.entity;

import com.x.base.core.entity.AbstractPersistenceProperties;

public final class PersistenceProperties extends AbstractPersistenceProperties {
	
	public static class Calendar_Setting {
		public static final String table = "CAL_SETTING";
	}
	
	public static class Calendar_SettingLobValue {
		public static final String table = "CAL_SETTING_LOBVALUE";
	}

	public static class Calendar {
		public static final String table = "CAL_CALENDAR";
	}
	
	public static class Calendar_Event {
		public static final String table = "CAL_EVENT";
	}

	public static class Calendar_EventComment {
		public static final String table = "CAL_EVENT_COMMENT";
	}

	public static class Calendar_EventRepeatMaster {
		public static final String table = "CAL_EVENT_REPM";
	}
	
}