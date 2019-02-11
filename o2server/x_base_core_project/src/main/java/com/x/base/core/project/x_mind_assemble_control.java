package com.x.base.core.project;

import com.x.base.core.project.annotation.Module;
import com.x.base.core.project.annotation.ModuleCategory;
import com.x.base.core.project.annotation.ModuleType;

@Module(type = ModuleType.ASSEMBLE, category = ModuleCategory.OFFICIAL, name = "脑图")
public class x_mind_assemble_control extends AssembleA {

	public x_mind_assemble_control() {
		super();
		dependency.containerEntities.add("com.x.mind.entity.MindBaseInfo");
		dependency.containerEntities.add("com.x.mind.entity.MindContentInfo");
		dependency.containerEntities.add("com.x.mind.entity.MindFolderInfo");
		dependency.containerEntities.add("com.x.mind.entity.MindIconInfo");
		dependency.containerEntities.add("com.x.mind.entity.MindRecycleInfo");
		dependency.containerEntities.add("com.x.mind.entity.MindShareRecord");
		dependency.containerEntities.add("com.x.mind.entity.MindVersionInfo");
		dependency.containerEntities.add("com.x.mind.entity.MindVersionContent");
		dependency.storeJars.add(x_organization_core_entity.class.getSimpleName());
		dependency.storeJars.add(x_organization_core_express.class.getSimpleName());
		dependency.storeJars.add(x_mind_core_entity.class.getSimpleName());
		dependency.storeJars.add(x_collaboration_core_message.class.getSimpleName());
	}

//	public static final String name = "脑图";
//	public static List<String> containerEntities = new ArrayList<>();
//	public static List<StorageType> usedStorageTypes = new ArrayList<>();
//	public static List<Class<? extends Compilable>> dependents = new ArrayList<>();
//
//	static {
//		containerEntities.add("com.x.mind.entity.MindBaseInfo");
//		containerEntities.add("com.x.mind.entity.MindContentInfo");
//		containerEntities.add("com.x.mind.entity.MindFolderInfo");
//		containerEntities.add("com.x.mind.entity.MindIconInfo");
//		containerEntities.add("com.x.mind.entity.MindRecycleInfo");
//		containerEntities.add("com.x.mind.entity.MindShareRecord");
//		containerEntities.add("com.x.mind.entity.MindVersionInfo");
//		containerEntities.add("com.x.mind.entity.MindVersionContent");
//		dependents.add(x_base_core_project.class);
//		dependents.add(x_organization_core_entity.class);
//		dependents.add(x_organization_core_express.class);
//		dependents.add(x_mind_core_entity.class);
//		dependents.add(x_collaboration_core_message.class);
//	}

}
