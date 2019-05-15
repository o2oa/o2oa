package com.x.message.core.entity;

import com.x.base.core.entity.AbstractPersistenceProperties;

public final class PersistenceProperties extends AbstractPersistenceProperties {

	public static class Instant {
		public static final String table = "MSG_INSTANT";
	}
	
	public static class Message {
		public static final String table = "MSG_MESSAGE";
	}

	public static class Mass {
		public static final String table = "MSG_MASS";
	}

}