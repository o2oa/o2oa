package com.x.correlation.core.entity;

import com.x.base.core.entity.AbstractPersistenceProperties;

public final class PersistenceProperties extends AbstractPersistenceProperties {

	public static class Content {

		private Content() {
		}

		public static class Correlation {

			private Correlation() {
			}

			public static final String TABLE = "CORR_C_CORRELATION";
		}

	}
}