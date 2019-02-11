package com.x.base.core.project;

import com.x.base.core.entity.StorageType;
import com.x.base.core.project.annotation.Module;
import com.x.base.core.project.annotation.ModuleCategory;
import com.x.base.core.project.annotation.ModuleType;

@Module(type = ModuleType.ASSEMBLE, category = ModuleCategory.OFFICIAL, name = "会议管理")
public class x_meeting_assemble_control extends AssembleA {

	public x_meeting_assemble_control() {
		super();
		dependency.containerEntities.add("com.x.meeting.core.entity.Building");
		dependency.containerEntities.add("com.x.meeting.core.entity.Room");
		dependency.containerEntities.add("com.x.meeting.core.entity.Meeting");
		dependency.containerEntities.add("com.x.meeting.core.entity.Attachment");
		dependency.storageTypes.add(StorageType.meeting.toString());
		dependency.storeJars.add(x_organization_core_entity.class.getSimpleName());
		dependency.storeJars.add(x_organization_core_express.class.getSimpleName());
		dependency.storeJars.add(x_meeting_core_entity.class.getSimpleName());
		dependency.storeJars.add(x_collaboration_core_message.class.getSimpleName());
	}

//	public static final String name = "会议管理";
//	public static List<String> containerEntities = new ArrayList<>();
//	public static List<StorageType> usedStorageTypes = new ArrayList<>();
//	public static List<Class<? extends Compilable>> dependents = new ArrayList<>();
//
//	static {
//		containerEntities.add("com.x.meeting.core.entity.Building");
//		containerEntities.add("com.x.meeting.core.entity.Room");
//		containerEntities.add("com.x.meeting.core.entity.Meeting");
//		containerEntities.add("com.x.meeting.core.entity.Attachment");
//		usedStorageTypes.add(StorageType.meeting);
//		dependents.add(x_base_core_project.class);
//		dependents.add(x_organization_core_express.class);
//		dependents.add(x_organization_core_entity.class);
//		dependents.add(x_meeting_core_entity.class);
//		dependents.add(x_collaboration_core_message.class);
//	}

}
