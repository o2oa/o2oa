package com.x.base.core.project;

import com.x.base.core.project.annotation.Module;
import com.x.base.core.project.annotation.ModuleCategory;
import com.x.base.core.project.annotation.ModuleType;

@Module(type = ModuleType.CENTER, category = ModuleCategory.OFFICIAL, name = "中心", packageName = "com.x.program.center", containerEntities = {
		"com.x.program.center.core.entity.Agent", "com.x.program.center.core.entity.Invoke",
		"com.x.program.center.core.entity.Captcha", "com.x.program.center.core.entity.Code",
		"com.x.program.center.core.entity.PromptErrorLog", "com.x.program.center.core.entity.UnexpectedErrorLog",
		"com.x.program.center.core.entity.Structure", "com.x.program.center.core.entity.WarnLog",
		"com.x.program.center.core.entity.validation.Meta", "com.x.portal.core.entity.Page",
		"com.x.portal.core.entity.Portal", "com.x.organization.core.entity.Group",
		"com.x.organization.core.entity.Custom", "com.x.organization.core.entity.Role",
		"com.x.organization.core.entity.Person", "com.x.organization.core.entity.Identity",
		"com.x.organization.core.entity.PersonAttribute", "com.x.organization.core.entity.Unit",
		"com.x.organization.core.entity.UnitAttribute", "com.x.organization.core.entity.UnitDuty",
		"com.x.general.core.entity.area.District", "com.x.program.center.core.entity.Schedule",
		"com.x.program.center.core.entity.ScheduleLocal",
		"com.x.program.center.core.entity.ScheduleLog" }, storeJars = { "x_organization_core_express",
				"x_program_center_core_entity", "x_attendance_core_entity", "x_cms_core_entity",
				"x_message_core_entity", "x_component_core_entity", "x_file_core_entity", "x_meeting_core_entity",
				"x_okr_core_entity", "x_organization_core_entity", "x_processplatform_core_entity",
				"x_query_core_entity", "x_portal_core_entity",
				"x_general_core_entity" }, dynamicJars = { "x_query_dynamic_entity" })
public class x_program_center extends Deployable {

//	public x_program_center() {
//		super();
//		dependency.containerEntities.add("com.x.program.center.core.entity.Agent");
//		dependency.containerEntities.add("com.x.program.center.core.entity.Invoke");
//		dependency.containerEntities.add("com.x.program.center.core.entity.Captcha");
//		dependency.containerEntities.add("com.x.program.center.core.entity.Code");
//		dependency.containerEntities.add("com.x.program.center.core.entity.PromptErrorLog");
//		dependency.containerEntities.add("com.x.program.center.core.entity.UnexpectedErrorLog");
//		dependency.containerEntities.add("com.x.program.center.core.entity.Structure");
//		dependency.containerEntities.add("com.x.program.center.core.entity.WarnLog");
//		dependency.containerEntities.add("com.x.program.center.core.entity.validation.Meta");
//		dependency.containerEntities.add("com.x.portal.core.entity.Page");
//		dependency.containerEntities.add("com.x.portal.core.entity.Portal");
//		dependency.containerEntities.add("com.x.organization.core.entity.Group");
//		dependency.containerEntities.add("com.x.organization.core.entity.Custom");
//		dependency.containerEntities.add("com.x.organization.core.entity.Role");
//		dependency.containerEntities.add("com.x.organization.core.entity.Person");
//		dependency.containerEntities.add("com.x.organization.core.entity.Identity");
//		dependency.containerEntities.add("com.x.organization.core.entity.PersonAttribute");
//		dependency.containerEntities.add("com.x.organization.core.entity.Unit");
//		dependency.containerEntities.add("com.x.organization.core.entity.UnitAttribute");
//		dependency.containerEntities.add("com.x.organization.core.entity.UnitDuty");
//		dependency.containerEntities.add("com.x.general.core.entity.area.District");
//		dependency.containerEntities.add("com.x.program.center.core.entity.Schedule");
//		dependency.containerEntities.add("com.x.program.center.core.entity.ScheduleLocal");
//		dependency.containerEntities.add("com.x.program.center.core.entity.ScheduleLog");
//		dependency.storeJars.add(x_organization_core_express.class.getSimpleName());
//		dependency.storeJars.add(x_program_center_core_entity.class.getSimpleName());
//		dependency.storeJars.add(x_attendance_core_entity.class.getSimpleName());
//		dependency.storeJars.add(x_cms_core_entity.class.getSimpleName());
//		dependency.storeJars.add(x_message_core_entity.class.getSimpleName());
//		dependency.storeJars.add(x_component_core_entity.class.getSimpleName());
//		dependency.storeJars.add(x_file_core_entity.class.getSimpleName());
//		dependency.storeJars.add(x_meeting_core_entity.class.getSimpleName());
//		dependency.storeJars.add(x_okr_core_entity.class.getSimpleName());
//		dependency.storeJars.add(x_organization_core_entity.class.getSimpleName());
//		dependency.storeJars.add(x_processplatform_core_entity.class.getSimpleName());
//		dependency.storeJars.add(x_query_core_entity.class.getSimpleName());
//		dependency.storeJars.add(x_portal_core_entity.class.getSimpleName());
//		dependency.storeJars.add(x_general_core_entity.class.getSimpleName());
//		dependency.dynamicJars.add("x_query_dynamic_entity");
//	}
}
