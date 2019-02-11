package com.x.base.core.project;

import com.x.base.core.entity.StorageType;
import com.x.base.core.project.annotation.Module;
import com.x.base.core.project.annotation.ModuleCategory;
import com.x.base.core.project.annotation.ModuleType;

@Module(type = ModuleType.ASSEMBLE, category = ModuleCategory.OFFICIAL, name = "日程管理")
public class x_calendar_assemble_control extends AssembleA {

	public x_calendar_assemble_control() {
		super();
		dependency.containerEntities.add("com.x.calendar.core.entity.Calendar");
		dependency.containerEntities.add("com.x.calendar.core.entity.Calendar_Event");
		dependency.containerEntities.add("com.x.calendar.core.entity.Calendar_EventRepeatMaster");
		dependency.containerEntities.add("com.x.calendar.core.entity.Calendar_Setting");
		dependency.containerEntities.add("com.x.calendar.core.entity.Calendar_SettingLobValue");
		dependency.storageTypes.add(StorageType.calendar.toString());
		dependency.storeJars.add(x_organization_core_express.class.getSimpleName());
		dependency.storeJars.add(x_organization_core_entity.class.getSimpleName());
		dependency.storeJars.add(x_calendar_core_entity.class.getSimpleName());
	}

//	public static final Dependency dependency = new Dependency();
//
//	static {
//		dependency.containerEntities.add("com.x.calendar.core.entity.Calendar");
//		dependency.containerEntities.add("com.x.calendar.core.entity.Calendar_Event");
//		dependency.containerEntities.add("com.x.calendar.core.entity.Calendar_EventRepeatMaster");
//		dependency.containerEntities.add("com.x.calendar.core.entity.Calendar_Setting");
//		dependency.containerEntities.add("com.x.calendar.core.entity.Calendar_SettingLobValue");
//		dependency.storageTypes.add(StorageType.calendar.toString());
//		dependency.storeJars.add(x_bbs_core_entity.class.getName());
//		dependency.storeJars.add(x_organization_core_express.class.getName());
//		dependency.storeJars.add(x_organization_core_entity.class.getName());
//		dependency.storeJars.add(x_collaboration_core_message.class.getName());
//	}

//	public static final String name = "日程管理";
//	public static List<String> containerEntities = new ArrayList<>();
//	public static List<StorageType> usedStorageTypes = new ArrayList<>();
//	public static List<Class<? extends Compilable>> dependents = new ArrayList<>();
//
//	static {
//		containerEntities.add("com.x.calendar.core.entity.Calendar");
//		containerEntities.add("com.x.calendar.core.entity.Calendar_Event");
//		containerEntities.add("com.x.calendar.core.entity.Calendar_EventRepeatMaster");
//		containerEntities.add("com.x.calendar.core.entity.Calendar_Setting");
//		containerEntities.add("com.x.calendar.core.entity.Calendar_SettingLobValue");
//
//		usedStorageTypes.add(StorageType.calendar);
//
//		dependents.add(x_base_core_project.class);
//		dependents.add(x_organization_core_entity.class);
//		dependents.add(x_organization_core_express.class);
//		dependents.add(x_calendar_core_entity.class);
//	}

//	protected void custom(File lib, String xLib) throws Exception {
//	}
//
//	public static void main(String[] args) {
//	}

}
