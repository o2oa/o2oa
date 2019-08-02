package com.x.base.core.project;

import com.x.base.core.project.annotation.Module;
import com.x.base.core.project.annotation.ModuleCategory;
import com.x.base.core.project.annotation.ModuleType;

@Module(type = ModuleType.ASSEMBLE, category = ModuleCategory.OFFICIAL, name = "考勤", packageName = "com.x.attendance.assemble.control", containerEntities = {
		"com.x.attendance.entity.AttendanceAdmin", "com.x.attendance.entity.AttendanceAppealInfo",
		"com.x.attendance.entity.AttendanceDetail", "com.x.attendance.entity.AttendanceDetailMobile",
		"com.x.attendance.entity.AttendanceEmployeeConfig", "com.x.attendance.entity.AttendanceImportFileInfo",
		"com.x.attendance.entity.AttendanceScheduleSetting", "com.x.attendance.entity.AttendanceSetting",
		"com.x.attendance.entity.AttendanceSelfHoliday", "com.x.attendance.entity.AttendanceStatisticalCycle",
		"com.x.attendance.entity.AttendanceStatisticRequireLog", "com.x.attendance.entity.AttendanceWorkDayConfig",
		"com.x.attendance.entity.AttendanceWorkPlace", "com.x.attendance.entity.StatisticPersonForMonth",
		"com.x.attendance.entity.StatisticTopUnitForDay", "com.x.attendance.entity.StatisticTopUnitForMonth",
		"com.x.attendance.entity.StatisticUnitForDay", "com.x.attendance.entity.StatisticUnitForMonth" }, storeJars = {
				"x_attendance_core_entity", "x_organization_core_express", "x_organization_core_entity" })
public class x_attendance_assemble_control extends Deployable {
}
