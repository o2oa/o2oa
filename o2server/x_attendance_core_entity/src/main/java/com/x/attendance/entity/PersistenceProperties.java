package com.x.attendance.entity;

import com.x.base.core.entity.AbstractPersistenceProperties;

public final class PersistenceProperties extends AbstractPersistenceProperties {
	
	public static class AttendanceDetail {
		public static final String table = "ATDC_ATTENDANCE_DETAIL";
	}
	public static class AttendanceDetailMobile {
		public static final String table = "ATDC_ATTENDANCE_DETAIL_MOBILE";
	}
	public static class AttendanceStatisticRequireLog {
		public static final String table = "ATDC_ATTENDANCE_STATISTIC_REQUIRELOG";
	}
	
	public static class AttendanceEmployeeConfig {
		public static final String table = "ATDC_ATTENDANCE_EMPLOYEE_CONFIG";
	}
	
	public static class AttendanceAppealInfo{
		public static final String table = "ATDC_ATTENDANCE_APPEALINFO";
	}
	
	public static class AttendanceSetting {
		public static final String table = "ATDC_ATTENDANCE_SETTING";
	}
	
	public static class AttendanceWorkDayConfig {
		public static final String table = "ATDC_ATTENDANCE_WORKDAYCONFIG";
	}
	
	public static class AttendanceImportFileInfo {
		public static final String table = "ATDC_ATTENDANCE_IMPORTFILE";
	}
	
	public static class AttendanceAdmin {
		public static final String table = "ATDC_ATTENDANCE_ADMIN";
	}
	
	public static class AttendanceScheduleSetting {
		public static final String table = "ATDC_ATTENDANCE_SCHEDULE_SETTING";
	}
	
	public static class AttendanceSelfHoliday {
		public static final String table = "ATDC_ATTENDANCE_SELFHOLIDAYS";
	}
	
	public static class AttendanceStatisticalCycle {
		public static final String table = "ATDC_STATISTIC_CYCLE";
	}
	
	public static class StatisticTopUnitForDay {
		public static final String table = "ATDC_STATISTIC_TOPUNIT_FORDAY";
	}
	
	public static class StatisticTopUnitForMonth {
		public static final String table = "ATDC_STATISTIC_TOPUNIT_FORMONTH";
	}
	
	public static class StatisticUnitForDay {
		public static final String table = "ATDC_STATISTIC_UNIT_FORDAY";
	}
	
	public static class StatisticUnitForMonth {
		public static final String table = "ATDC_STATISTIC_UNIT_FORMONTH";
	}
	
	public static class StatisticPersonForMonth {
		public static final String table = "ATDC_STATISTIC_PERSON_FORMONTH";
	}
	
	public static class AttendanceWorkPlace {
		public static final String table = "ATDC_WORK_PLACE";
	}
}