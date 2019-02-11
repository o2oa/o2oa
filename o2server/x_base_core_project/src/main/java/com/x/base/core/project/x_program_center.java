package com.x.base.core.project;

import com.x.base.core.project.annotation.Module;
import com.x.base.core.project.annotation.ModuleCategory;
import com.x.base.core.project.annotation.ModuleType;

@Module(type = ModuleType.CENTER, category = ModuleCategory.OFFICIAL, name = "中心")
public class x_program_center extends AssembleC {
	
	public x_program_center() {
		super();
		dependency.containerEntities.add("com.x.program.center.core.entity.Agent");
		dependency.containerEntities.add("com.x.program.center.core.entity.Invoke");
		dependency.containerEntities.add("com.x.program.center.core.entity.Captcha");
		dependency.containerEntities.add("com.x.program.center.core.entity.Code");
		dependency.containerEntities.add("com.x.program.center.core.entity.PromptErrorLog");
		dependency.containerEntities.add("com.x.program.center.core.entity.UnexpectedErrorLog");
		dependency.containerEntities.add("com.x.program.center.core.entity.Structure");
		dependency.containerEntities.add("com.x.program.center.core.entity.WarnLog");
		dependency.containerEntities.add("com.x.program.center.core.entity.validation.Meta");
		dependency.containerEntities.add("com.x.portal.core.entity.Page");
		dependency.containerEntities.add("com.x.portal.core.entity.Portal");
		dependency.containerEntities.add("com.x.organization.core.entity.Group");
		dependency.containerEntities.add("com.x.organization.core.entity.Custom");
		dependency.containerEntities.add("com.x.organization.core.entity.Role");
		dependency.containerEntities.add("com.x.organization.core.entity.Person");
		dependency.containerEntities.add("com.x.organization.core.entity.Identity");
		dependency.containerEntities.add("com.x.organization.core.entity.PersonAttribute");
		dependency.containerEntities.add("com.x.organization.core.entity.Unit");
		dependency.containerEntities.add("com.x.organization.core.entity.UnitAttribute");
		dependency.containerEntities.add("com.x.organization.core.entity.UnitDuty");
		dependency.containerEntities.add("com.x.general.core.entity.area.District");
		dependency.containerEntities.add("com.x.program.center.core.entity.Schedule");
		dependency.containerEntities.add("com.x.program.center.core.entity.ScheduleLocal");
		dependency.containerEntities.add("com.x.program.center.core.entity.ScheduleLog");
		dependency.storeJars.add(x_organization_core_express.class.getSimpleName());
		dependency.storeJars.add(x_program_center_core_entity.class.getSimpleName());
		dependency.storeJars.add(x_attendance_core_entity.class.getSimpleName());
		dependency.storeJars.add(x_cms_core_entity.class.getSimpleName());
		dependency.storeJars.add(x_collaboration_core_entity.class.getSimpleName());
		dependency.storeJars.add(x_component_core_entity.class.getSimpleName());
		dependency.storeJars.add(x_file_core_entity.class.getSimpleName());
		dependency.storeJars.add(x_meeting_core_entity.class.getSimpleName());
		dependency.storeJars.add(x_okr_core_entity.class.getSimpleName());
		dependency.storeJars.add(x_organization_core_entity.class.getSimpleName());
		dependency.storeJars.add(x_processplatform_core_entity.class.getSimpleName());
		dependency.storeJars.add(x_message_core_entity.class.getSimpleName());
		dependency.storeJars.add(x_query_core_entity.class.getSimpleName());
		dependency.storeJars.add(x_portal_core_entity.class.getSimpleName());
		dependency.storeJars.add(x_general_core_entity.class.getSimpleName());
	}

//	public static final String name = "中心";
//	public static List<String> containerEntities = new ArrayList<>();
//	public static List<StorageType> usedStorageTypes = new ArrayList<>();
//	public static List<Class<? extends Compilable>> dependents = new ArrayList<>();
//
//	static {
//		containerEntities.add("com.x.program.center.core.entity.Agent");
//		containerEntities.add("com.x.program.center.core.entity.Invoke");
//		containerEntities.add("com.x.program.center.core.entity.Captcha");
//		containerEntities.add("com.x.program.center.core.entity.Code");
//		containerEntities.add("com.x.program.center.core.entity.PromptErrorLog");
//		containerEntities.add("com.x.program.center.core.entity.UnexpectedErrorLog");
//		containerEntities.add("com.x.program.center.core.entity.Structure");
//		containerEntities.add("com.x.program.center.core.entity.WarnLog");
//		containerEntities.add("com.x.program.center.core.entity.validation.Meta");
//		containerEntities.add("com.x.portal.core.entity.Page");
//		containerEntities.add("com.x.portal.core.entity.Portal");
//		containerEntities.add("com.x.organization.core.entity.Group");
//		containerEntities.add("com.x.organization.core.entity.Custom");
//		containerEntities.add("com.x.organization.core.entity.Role");
//		containerEntities.add("com.x.organization.core.entity.Person");
//		containerEntities.add("com.x.organization.core.entity.Identity");
//		containerEntities.add("com.x.organization.core.entity.PersonAttribute");
//		containerEntities.add("com.x.organization.core.entity.Unit");
//		containerEntities.add("com.x.organization.core.entity.UnitAttribute");
//		containerEntities.add("com.x.organization.core.entity.UnitDuty");
//		containerEntities.add("com.x.general.core.entity.area.District");
//		containerEntities.add("com.x.program.center.core.entity.Schedule");
//		containerEntities.add("com.x.program.center.core.entity.ScheduleLocal");
//		containerEntities.add("com.x.program.center.core.entity.ScheduleLog");
//		dependents.add(x_base_core_project.class);
//		dependents.add(x_organization_core_express.class);
//		dependents.add(x_program_center_core_entity.class);
//		dependents.add(x_attendance_core_entity.class);
//		dependents.add(x_cms_core_entity.class);
//		dependents.add(x_collaboration_core_entity.class);
//		dependents.add(x_component_core_entity.class);
//		dependents.add(x_file_core_entity.class);
//		dependents.add(x_meeting_core_entity.class);
//		dependents.add(x_okr_core_entity.class);
//		dependents.add(x_organization_core_entity.class);
//		dependents.add(x_processplatform_core_entity.class);
//		dependents.add(x_message_core_entity.class);
//		dependents.add(x_query_core_entity.class);
//		dependents.add(x_portal_core_entity.class);
//		dependents.add(x_general_core_entity.class);
//	}
}
