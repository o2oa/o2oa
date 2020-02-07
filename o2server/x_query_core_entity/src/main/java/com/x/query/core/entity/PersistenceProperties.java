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

	public static class Segment {

		public static class Entry {
			public static final String table = "QRY_SEG_ENTRY";
		}

		public static class Word {
			public static final String table = "QRY_SEG_WORD";
		}

	}

	public static class Schema {

		public static class Table {
			public static final String table = "QRY_SCH_TABLE";
		}

		public static class Statement {
			public static final String table = "QRY_SCH_STATEMENT";
		}

	}

	public static class Program {

		public static class Argument {
			public static final String table = "QRY_PGM_ARGUMENT";
		}
	}

	public static class Neural {

		public static class Model {

			public static final String table = "QRY_NRL_MODEL";
		}

		public static class OutValue {

			public static final String table = "QRY_NRL_OUTVALUE";
		}

		public static class InValue {

			public static final String table = "QRY_NRL_INVALUE";
		}

		public static class Entry {

			public static final String table = "QRY_NRL_ENTRY";
		}

		public static class InText {

			public static final String table = "QRY_NRL_INTEXT";
		}

		public static class OutText {

			public static final String table = "QRY_NRL_OUTTEXT";
		}

	}
}