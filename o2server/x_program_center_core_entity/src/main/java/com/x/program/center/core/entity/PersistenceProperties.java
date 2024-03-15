package com.x.program.center.core.entity;

import com.x.base.core.entity.AbstractPersistenceProperties;

public final class PersistenceProperties extends AbstractPersistenceProperties {

	public static class Schedule {
		private Schedule() {
		}

		public static final String TABLE = "CTE_SCHEDULE";
	}

	public static class ScheduleLocal {
		private ScheduleLocal() {
		}

		public static final String TABLE = "CTE_SCHEDULELOCAL";
	}

	public static class ScheduleLog {
		private ScheduleLog() {
		}

		public static final String TABLE = "CTE_SCHEDULELOG";
	}

	public static class PromptErrorLog {
		private PromptErrorLog() {
		}

		public static final String TABLE = "CTE_PROMPTERRORLOG";
	}

	public static class UnexpectedErrorLog {
		private UnexpectedErrorLog() {
		}

		public static final String TABLE = "CTE_UNEXPECTEDERRORLOG";
	}

	public static class WarnLog {
		private WarnLog() {
		}

		public static final String TABLE = "CTE_WARNLOG";
	}

	public static class Captcha {
		private Captcha() {
		}

		public static final String TABLE = "CTE_CAPTCHA";
	}

	public static class Code {
		private Code() {
		}

		public static final String TABLE = "CTE_CODE";
	}

	public static class Agent {
		private Agent() {
		}

		public static final String TABLE = "CTE_AGENT";
	}

	public static class Structure {
		private Structure() {
		}

		public static final String TABLE = "CTE_STRUCTURE";
	}

	public static class Invoke {
		private Invoke() {
		}

		public static final String TABLE = "CTE_INVOKE";
	}

	public static class Application {
		private Application() {
		}

		public static final String TABLE = "CTE_APPLICATION";
	}

	public static class Attachment {
		private Attachment() {
		}

		public static final String TABLE = "CTE_ATTACHMENT";
	}

	public static class InstallLog {
		private InstallLog() {
		}

		public static final String TABLE = "CTE_INSTALL_LOG";
	}

	public static class MPWeixinMenu {
		private MPWeixinMenu() {
		}

		public static final String TABLE = "CTE_MP_WEIXIN_MENU";
	}

	public static class AppPackApkFile {
		private AppPackApkFile() {
		}

		public static final String TABLE = "CTE_APP_PACK_APK_FILE";
	}

	public static class Validation {
		private Validation() {
		}

		public static class Bar {
			private Bar() {
			}

			public static final String TABLE = "CTE_VAL_BAR";
		}

		public static class Foo {
			private Foo() {
			}

			public static final String TABLE = "CTE_VAL_FOO";
		}

		public static class Meta {
			private Meta() {
			}

			public static final String TABLE = "CTE_VAL_META";
		}

	}

	public static class Script {
		private Script() {
		}

		public static final String TABLE = "CTE_SCRIPT";
	}

}
