package com.x.jpush.core.entity;

import com.x.base.core.entity.AbstractPersistenceProperties;

public final class PersistenceProperties extends AbstractPersistenceProperties {
	
	public static class SampleEntityClassName {
		public static final String table = "SAMPLE_JPUSH_TABLENAME";
	}

	public static class PushDevice {
		public static final String table = "JPUSH_DEVICE";
	}
}