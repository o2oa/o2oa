package com.x.base.core.project;

import com.x.base.core.project.annotation.Module;
import com.x.base.core.project.annotation.ModuleCategory;
import com.x.base.core.project.annotation.ModuleType;

@Module(type = ModuleType.ASSEMBLE, category = ModuleCategory.OFFICIAL, name = "考勤管理", packageName = "com.x.attendance.assemble.control", containerEntities = {
		"com.x.attendance.entity.AttendanceAdmin", "com.x.attendance.entity.AttendanceAppealInfo", "com.x.attendance.entity.AttendanceAppealAuditInfo",
		"com.x.attendance.entity.AttendanceDetail", "com.x.attendance.entity.AttendanceDetailMobile",
		"com.x.attendance.entity.AttendanceEmployeeConfig", "com.x.attendance.entity.AttendanceImportFileInfo",
		"com.x.attendance.entity.AttendanceScheduleSetting", "com.x.attendance.entity.AttendanceSetting",
		"com.x.attendance.entity.AttendanceSelfHoliday", "com.x.attendance.entity.AttendanceStatisticalCycle",
		"com.x.attendance.entity.AttendanceStatisticRequireLog", "com.x.attendance.entity.AttendanceWorkDayConfig",
		"com.x.attendance.entity.AttendanceWorkPlace", "com.x.attendance.entity.StatisticPersonForMonth",
		"com.x.attendance.entity.StatisticTopUnitForDay", "com.x.attendance.entity.StatisticTopUnitForMonth",
		"com.x.attendance.entity.StatisticUnitForDay", "com.x.attendance.entity.StatisticUnitForMonth",
		"com.x.attendance.entity.AttendanceDingtalkDetail", "com.x.attendance.entity.AttendanceQywxDetail",
		"com.x.attendance.entity.DingdingQywxSyncRecord", "com.x.attendance.entity.StatisticDingdingPersonForMonth",
		"com.x.attendance.entity.StatisticDingdingUnitForDay", "com.x.attendance.entity.StatisticDingdingUnitForMonth",
		"com.x.attendance.entity.StatisticQywxPersonForMonth", "com.x.attendance.entity.StatisticQywxUnitForDay",
		"com.x.attendance.entity.StatisticQywxUnitForMonth",
		"com.x.attendance.entity.v2.AttendanceV2Group", "com.x.attendance.entity.v2.AttendanceV2Shift",
		"com.x.attendance.entity.v2.AttendanceV2WorkPlace", "com.x.attendance.entity.v2.AttendanceV2CheckInRecord",
		"com.x.attendance.entity.v2.AttendanceV2Detail", "com.x.attendance.entity.v2.AttendanceV2Config",
		"com.x.attendance.entity.v2.AttendanceV2AppealInfo", "com.x.attendance.entity.v2.AttendanceV2PersonConfig",
		"com.x.attendance.entity.v2.AttendanceV2AlertMessage",
		"com.x.attendance.entity.v2.AttendanceV2GroupSchedule",
		"com.x.attendance.entity.v2.AttendanceV2GroupScheduleConfig",
		"com.x.attendance.entity.v2.AttendanceV2LeaveData", "com.x.general.core.entity.GeneralFile" }, storeJars = {
				"x_attendance_core_entity", "x_organization_core_express", "x_organization_core_entity", "x_general_core_entity" })
public class x_attendance_assemble_control extends Deployable {
}
