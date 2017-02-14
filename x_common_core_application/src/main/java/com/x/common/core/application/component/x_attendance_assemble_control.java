package com.x.common.core.application.component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.gson.XGsonBuilder;

public class x_attendance_assemble_control extends Assemble {

	public static List<String> containerEntities = new ArrayList<>();

	static {
		containerEntities.add("com.x.attendance.entity.AttendanceAdmin");
		containerEntities.add("com.x.attendance.entity.AttendanceDetail");
		containerEntities.add("com.x.attendance.entity.AttendanceDetailBakup");
		containerEntities.add("com.x.attendance.entity.AttendanceDetailAppeal");
		containerEntities.add("com.x.attendance.entity.AttendanceAppealInfo");
		containerEntities.add("com.x.attendance.entity.AttendanceImportFileInfo");
		containerEntities.add("com.x.attendance.entity.AttendanceScheduleSetting");
		containerEntities.add("com.x.attendance.entity.AttendanceSetting");
		containerEntities.add("com.x.attendance.entity.AttendanceWorkDayConfig");
		containerEntities.add("com.x.attendance.entity.AttendanceSelfHoliday");
		containerEntities.add("com.x.attendance.entity.StatisticCompanyForDay");
		containerEntities.add("com.x.attendance.entity.StatisticCompanyForMonth");
		containerEntities.add("com.x.attendance.entity.StatisticDepartmentForDay");
		containerEntities.add("com.x.attendance.entity.StatisticDepartmentForMonth");
		containerEntities.add("com.x.attendance.entity.StatisticPersonForMonth");
		containerEntities.add("com.x.attendance.entity.AttendanceStatisticalCycle");
		containerEntities.add("com.x.attendance.entity.AttendanceEmployeeConfig");
		containerEntities.add("com.x.attendance.entity.AttendanceStatisticRequireLog");
	}

	protected void custom(File dir, String repositoryPath) throws Exception {
		File repository = new File(repositoryPath);
		File lib = new File(dir, "WEB-INF/lib");
		FileUtils.copyDirectory(repository, lib, new WildcardFileFilter("x_organization_core_entity*.jar"));
		FileUtils.copyDirectory(repository, lib, new WildcardFileFilter("x_organization_core_express*.jar"));
		FileUtils.copyDirectory(repository, lib, new WildcardFileFilter("x_attendance_core_entity*.jar"));
		FileUtils.copyDirectory(repository, lib, new WildcardFileFilter("x_collaboration_core_message*.jar"));
	}

	public static void main(String[] args) {
		try {
			String str = args[0];
			str = StringUtils.replace(str, "\\", "/");
			Argument arg = XGsonBuilder.instance().fromJson(str, Argument.class);
			x_attendance_assemble_control o = new x_attendance_assemble_control();
			o.pack(arg.getDistPath(), arg.getRepositoryPath(), arg.getCenterHost(), arg.getCenterPort(),
					arg.getCenterCipher(), arg.getConfigApplicationServer());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
