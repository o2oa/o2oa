package com.x.base.core.project;

import com.x.base.core.project.annotation.Module;
import com.x.base.core.project.annotation.ModuleCategory;
import com.x.base.core.project.annotation.ModuleType;

@Module(type = ModuleType.ASSEMBLE, category = ModuleCategory.OFFICIAL, name = "消息通讯")
public class x_message_assemble_communicate extends AssembleA {

	public x_message_assemble_communicate() {
		super();
		dependency.containerEntities.add("com.x.message.core.entity.Message");
		dependency.containerEntities.add("com.x.message.core.entity.Mass");
		dependency.storeJars.add(x_message_core_entity.class.getSimpleName());
		dependency.storeJars.add(x_meeting_core_entity.class.getSimpleName());
		dependency.storeJars.add(x_processplatform_core_entity.class.getSimpleName());
		dependency.storeJars.add(x_organization_core_express.class.getSimpleName());
	}

//	public static final String name = "消息通讯";
//	public static List<String> containerEntities = new ArrayList<>();
//	public static List<StorageType> usedStorageTypes = new ArrayList<>();
//	public static List<Class<? extends Compilable>> dependents = new ArrayList<>();
//
//	static {
//		containerEntities.add("com.x.message.core.entity.Message");
//		containerEntities.add("com.x.message.core.entity.Mass");
//		dependents.add(x_base_core_project.class);
//		dependents.add(x_message_core_entity.class);
//		dependents.add(x_meeting_core_entity.class);
//		dependents.add(x_processplatform_core_entity.class);
//		dependents.add(x_organization_core_express.class);
//	}
//

}
