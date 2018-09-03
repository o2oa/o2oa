package com.x.query.core.entity;

import com.x.base.core.entity.AbstractPersistenceProperties;

public final class PersistenceProperties extends AbstractPersistenceProperties {

	public static class Query {
		public static final String table = "QRY_QUERY";
	}

	public static class View {
		public static final String table = "QRY_VIEW";
	}

	public static class Stat {
		public static final String table = "QRY_STAT";
	}

	public static class Reveal {
		public static final String table = "QRY_REVEAL";
	}

	public static class Item {
		public static final String table = "QRY_ITEM";
	}

}