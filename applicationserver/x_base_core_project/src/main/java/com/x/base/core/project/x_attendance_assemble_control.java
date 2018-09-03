package com.x.base.core.project;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.entity.StorageType;
import com.x.base.core.project.gson.XGsonBuilder;

public class x_attendance_assemble_control extends AssembleA {

	public static final String name = "考勤";
	public static List<String> containerEntities = new ArrayList<>();
	public static List<StorageType> usedStorageTypes = new ArrayList<>();
	public static List<Class<? extends Compilable>> dependents = new ArrayList<>();

	static {
		
		containerEntities.add("com.x.attendance.entity.AttendanceAdmin");
		containerEntities.add("com.x.attendance.entity.AttendanceAppealInfo");
		containerEntities.add("com.x.attendance.entity.AttendanceDetail");
		containerEntities.add("com.x.attendance.entity.AttendanceDetailMobile");
		containerEntities.add("com.x.attendance.entity.AttendanceEmployeeConfig");
		containerEntities.add("com.x.attendance.entity.AttendanceImportFileInfo");
		containerEntities.add("com.x.attendance.entity.AttendanceScheduleSetting");
		containerEntities.add("com.x.attendance.entity.AttendanceSetting");
		containerEntities.add("com.x.attendance.entity.AttendanceSelfHoliday");
		containerEntities.add("com.x.attendance.entity.AttendanceStatisticalCycle");
		containerEntities.add("com.x.attendance.entity.AttendanceStatisticRequireLog");
		containerEntities.add("com.x.attendance.entity.AttendanceWorkDayConfig");
		containerEntities.add("com.x.attendance.entity.AttendanceWorkPlace");
		containerEntities.add("com.x.attendance.entity.StatisticPersonForMonth");
		containerEntities.add("com.x.attendance.entity.StatisticTopUnitForDay");
		containerEntities.add("com.x.attendance.entity.StatisticTopUnitForMonth");
		containerEntities.add("com.x.attendance.entity.StatisticUnitForDay");
		containerEntities.add("com.x.attendance.entity.StatisticUnitForMonth");

		dependents.add(x_base_core_project.class);
		dependents.add(x_organization_core_entity.class);
		dependents.add(x_organization_core_express.class);
		dependents.add(x_attendance_core_entity.class);
		dependents.add(x_collaboration_core_message.class);
	}

	protected void custom(File lib, String xLib) throws Exception {
		// File xLibDir = new File(xLib);
		// File libDir = new File(lib, "WEB-INF/lib");
		// for (Class<? extends Compilable> clz : dependents) {
		// FileUtils.copyDirectory(xLibDir, libDir, new
		// NameFileFilter(clz.getSimpleName() + "-" + VERSION + ".jar"));
		// }
	}

	public static void main(String[] args) {
		try {
			String str = args[0];
			str = StringUtils.replace(str, "\\", "/");
			Argument arg = XGsonBuilder.instance().fromJson(str, Argument.class);
			x_attendance_assemble_control o = new x_attendance_assemble_control();
			o.pack(arg.getDistPath(), arg.getRepositoryPath());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
