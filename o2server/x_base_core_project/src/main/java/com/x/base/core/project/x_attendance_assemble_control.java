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
				"x_attendance_core_entity", "x_organization_core_express",
				"x_organization_core_entity" })
public class x_attendance_assemble_control extends Deployable {

	public x_attendance_assemble_control() {
		super();
//		dependency.containerEntities.add("com.x.attendance.entity.AttendanceAdmin");
//		dependency.containerEntities.add("com.x.attendance.entity.AttendanceAppealInfo");
//		dependency.containerEntities.add("com.x.attendance.entity.AttendanceDetail");
//		dependency.containerEntities.add("com.x.attendance.entity.AttendanceDetailMobile");
//		dependency.containerEntities.add("com.x.attendance.entity.AttendanceEmployeeConfig");
//		dependency.containerEntities.add("com.x.attendance.entity.AttendanceImportFileInfo");
//		dependency.containerEntities.add("com.x.attendance.entity.AttendanceScheduleSetting");
//		dependency.containerEntities.add("com.x.attendance.entity.AttendanceSetting");
//		dependency.containerEntities.add("com.x.attendance.entity.AttendanceSelfHoliday");
//		dependency.containerEntities.add("com.x.attendance.entity.AttendanceStatisticalCycle");
//		dependency.containerEntities.add("com.x.attendance.entity.AttendanceStatisticRequireLog");
//		dependency.containerEntities.add("com.x.attendance.entity.AttendanceWorkDayConfig");
//		dependency.containerEntities.add("com.x.attendance.entity.AttendanceWorkPlace");
//		dependency.containerEntities.add("com.x.attendance.entity.StatisticPersonForMonth");
//		dependency.containerEntities.add("com.x.attendance.entity.StatisticTopUnitForDay");
//		dependency.containerEntities.add("com.x.attendance.entity.StatisticTopUnitForMonth");
//		dependency.containerEntities.add("com.x.attendance.entity.StatisticUnitForDay");
//		dependency.containerEntities.add("com.x.attendance.entity.StatisticUnitForMonth");
//		dependency.storeJars.add(x_attendance_core_entity.class.getSimpleName());
//		dependency.storeJars.add(x_organization_core_express.class.getSimpleName());
//		dependency.storeJars.add(x_organization_core_entity.class.getSimpleName());
	}
}
