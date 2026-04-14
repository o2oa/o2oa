package com.x.custom.index.core.entity;

import com.x.base.core.entity.AbstractPersistenceProperties;

public final class PersistenceProperties extends AbstractPersistenceProperties {

	public static class Reveal {

		private Reveal() {
			// nothing
		}

		public static final String TABLE = "CUS_INDEX_REVEAL";
	}
	
	public static class Custom {

		private Custom() {
			// nothing
		}

		public static final String TABLE = "CUS_INDEX_CUSTOM";
	}

}
