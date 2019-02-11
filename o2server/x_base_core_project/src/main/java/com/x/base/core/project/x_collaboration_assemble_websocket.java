package com.x.base.core.project;

import com.x.base.core.project.annotation.Module;
import com.x.base.core.project.annotation.ModuleCategory;
import com.x.base.core.project.annotation.ModuleType;

@Module(type = ModuleType.ASSEMBLE, category = ModuleCategory.OFFICIAL, name = "协作")
public class x_collaboration_assemble_websocket extends AssembleA {

	public x_collaboration_assemble_websocket() {
		super();
		dependency.containerEntities.add("com.x.collaboration.core.entity.SMSMessage");
		dependency.containerEntities.add("com.x.collaboration.core.entity.Notification");
		dependency.containerEntities.add("com.x.collaboration.core.entity.Dialog");
		dependency.containerEntities.add("com.x.collaboration.core.entity.Talk");
		dependency.storeJars.add(x_collaboration_core_entity.class.getSimpleName());
		dependency.storeJars.add(x_collaboration_core_message.class.getSimpleName());
		dependency.storeJars.add(x_collaboration_service_message.class.getSimpleName());
		dependency.storeJars.add(x_organization_core_express.class.getSimpleName());
	}

//	public static final Dependency dependency = new Dependency();
//
//	static {
//
//		dependency.containerEntities.add("com.x.collaboration.core.entity.SMSMessage");
//		dependency.containerEntities.add("com.x.collaboration.core.entity.Notification");
//		dependency.containerEntities.add("com.x.collaboration.core.entity.Dialog");
//		dependency.containerEntities.add("com.x.collaboration.core.entity.Talk");
//
////		dependency.storageTypes.add(StorageType.cms.toString());
////		dependency.storageTypes.add(StorageType.processPlatform.toString());
//		dependency.storeJars.add(x_collaboration_core_entity.class.getSimpleName());
//		dependency.storeJars.add(x_collaboration_core_message.class.getSimpleName());
//		dependency.storeJars.add(x_collaboration_service_message.class.getSimpleName());
//		dependency.storeJars.add(x_organization_core_express.class.getSimpleName());
//	}

//	public static final String name = "协作";
//	public static List<String> containerEntities = new ArrayList<>();
//	public static List<StorageType> usedStorageTypes = new ArrayList<>();
//	public static List<Class<? extends Compilable>> dependents = new ArrayList<>();
//
//	static {
//
//		dependents.add(x_base_core_project.class);
//		dependents.add(x_collaboration_core_message.class);
//		dependents.add(x_collaboration_core_entity.class);
//		dependents.add(x_collaboration_service_message.class);
//		dependents.add(x_organization_core_express.class);
//	}

}
