package o2.collect.core.entity;

import com.x.base.core.entity.AbstractPersistenceProperties;

public final class PersistenceProperties extends AbstractPersistenceProperties {

	public static class Account {
		public static final String table = "O2_ACCOUNT";
	}

	public static class Unit {
		public static final String table = "O2_UNIT";
	}

	public static class Device {
		public static final String table = "O2_DEVICE";
	}

	public static class Code {
		public static final String table = "O2_CODE";
	}

	public static class Module {
		public static final String table = "O2_MODULE";
	}

	public static class Log {
		public static class PromptErrorLog {
			public static final String table = "O2_PROMPTERRORLOG";
		}

		public static class UnexpectedErrorLog {
			public static final String table = "O2_UNEXPECTEDERRORLOG";
		}

		public static class WarnLog {
			public static final String table = "O2_WARNLOG";
		}

		public static class AppLog {
			public static final String table = "O2_APPLOG";
		}

	}

}
