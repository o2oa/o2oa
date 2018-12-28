package com.x.program.center.core.entity;

import com.x.base.core.entity.AbstractPersistenceProperties;

public final class PersistenceProperties extends AbstractPersistenceProperties {

	public static class Schedule {
		public static final String table = "CTE_SCHEDULE";
	}

	public static class ScheduleLocal {
		public static final String table = "CTE_SCHEDULELOCAL";
	}

	public static class ScheduleLog {
		public static final String table = "CTE_SCHEDULELOG";
	}

	public static class ClockTimer {
		public static final String table = "CTE_CLOCKTIMER";
	}

	public static class ClockTimerLog {
		public static final String table = "CTE_CLOCKTIMERLOG";
	}

	public static class ClockSchedule {
		public static final String table = "CTE_CLOCKSCHEDULE";
	}

	public static class ClockScheduleLog {
		public static final String table = "CTE_CLOCKSCHEDULELOG";
	}

	public static class PromptErrorLog {
		public static final String table = "CTE_PROMPTERRORLOG";
	}

	public static class UnexpectedErrorLog {
		public static final String table = "CTE_UNEXPECTEDERRORLOG";
	}

	public static class WarnLog {
		public static final String table = "CTE_WARNLOG";
	}

	public static class Captcha {
		public static final String table = "CTE_CAPTCHA";
	}

	public static class Code {
		public static final String table = "CTE_CODE";
	}

	public static class Agent {
		public static final String table = "CTE_AGENT";
	}

	public static class Structure {
		public static final String table = "CTE_STRUCTURE";
	}

	public static class Invoke {
		public static final String table = "CTE_INVOKE";
	}

	public static class Validation {
		public static class Meta {
			public static final String table = "VAL_META";
		}
	}
}