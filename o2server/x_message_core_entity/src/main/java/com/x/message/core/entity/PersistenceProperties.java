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
	
	public static class Org {
		public static final String table = "MSG_ORG";
		
	}


	public static class IMConversation {
		public static final String table = "MSG_IM_CONVERSATION";
	}
	public static class IMConversationExt {
		public static final String table = "MSG_IM_CONVERSATION_EXT";
	}

	public static class IMMsg {
		public static final String table = "MSG_IM_MESSAGE";
	}
	public static class IMMsgFile {
		public static final String table = "MSG_IM_MESSAGE_FILE";
	}

	public static class IMMsgCollection {
		public static final String table = "MSG_IM_MESSAGE_COLLECTION";
	}
}