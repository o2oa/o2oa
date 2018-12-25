package com.x.collaboration.core.entity;

import com.x.base.core.entity.AbstractPersistenceProperties;

public final class PersistenceProperties extends AbstractPersistenceProperties {

	public static class SMSMessage {
		public static final String table = "COL_SMSMESSAGE";
	}

    public static class Notification {
        public static final String table = "COL_NOTIFICATION";
    }

	public static class Dialog {
		public static final String table = "COL_DIALOG";
	}

	public static class Talk {
		public static final String table = "COL_TALK";
	}

}