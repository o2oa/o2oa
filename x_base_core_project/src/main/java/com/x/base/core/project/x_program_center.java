package com.x.base.core.project;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.StorageType;

public class x_program_center extends Compilable {

	public static final String name = "中心";
	public static List<String> containerEntities = new ArrayList<>();
	public static List<StorageType> usedStorageTypes = new ArrayList<>();
	public static List<Class<? extends Compilable>> dependents = new ArrayList<>();

	static {
		containerEntities.add("com.x.program.center.core.entity.Captcha");
		containerEntities.add("com.x.program.center.core.entity.Code");
		containerEntities.add("com.x.program.center.core.entity.ClockSchedule");
		containerEntities.add("com.x.program.center.core.entity.ClockScheduleLog");
		containerEntities.add("com.x.program.center.core.entity.ClockTimer");
		containerEntities.add("com.x.program.center.core.entity.ClockTimerLog");
		containerEntities.add("com.x.program.center.core.entity.PromptErrorLog");
		containerEntities.add("com.x.program.center.core.entity.UnexpectedErrorLog");
		containerEntities.add("com.x.program.center.core.entity.WarnLog");
		containerEntities.add("com.x.organization.core.entity.Person");
		dependents.add(x_base_core_project.class);
		dependents.add(x_program_center_core_entity.class);
		dependents.add(x_attendance_core_entity.class);
		dependents.add(x_cms_core_entity.class);
		dependents.add(x_collaboration_core_entity.class);
		dependents.add(x_component_core_entity.class);
		dependents.add(x_file_core_entity.class);
		dependents.add(x_meeting_core_entity.class);
		dependents.add(x_okr_core_entity.class);
		dependents.add(x_organization_core_entity.class);
		dependents.add(x_processplatform_core_entity.class);
	}

}
