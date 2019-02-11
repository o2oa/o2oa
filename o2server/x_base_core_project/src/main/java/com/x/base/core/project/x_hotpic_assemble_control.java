package com.x.base.core.project;

import java.io.File;

import com.x.base.core.entity.StorageType;
import com.x.base.core.project.annotation.Module;
import com.x.base.core.project.annotation.ModuleCategory;
import com.x.base.core.project.annotation.ModuleType;

@Module(type = ModuleType.ASSEMBLE, category = ModuleCategory.OFFICIAL, name = "热点图片")
public class x_hotpic_assemble_control extends AssembleA {

	public x_hotpic_assemble_control() {
		super();
		dependency.containerEntities.add("com.x.hotpic.entity.HotPictureInfo");
		dependency.containerEntities.add("com.x.bbs.entity.BBSSubjectInfo");
		dependency.containerEntities.add("com.x.cms.core.entity.Document");
		dependency.storageTypes.add(StorageType.file.toString());
		dependency.storeJars.add(x_organization_core_entity.class.getSimpleName());
		dependency.storeJars.add(x_organization_core_express.class.getSimpleName());
		dependency.storeJars.add(x_hotpic_core_entity.class.getSimpleName());
		dependency.storeJars.add(x_collaboration_core_message.class.getSimpleName());
		dependency.storeJars.add(x_cms_core_entity.class.getSimpleName());
		dependency.storeJars.add(x_bbs_core_entity.class.getSimpleName());
	}

//	public static final Dependency dependency = new Dependency();
//
//	static {
//		dependency.containerEntities.add("com.x.hotpic.entity.HotPictureInfo");
//		dependency.containerEntities.add("com.x.bbs.entity.BBSSubjectInfo");
//		dependency.containerEntities.add("com.x.cms.core.entity.Document");
//		dependency.storageTypes.add(StorageType.file.toString());
//		dependency.storeJars.add(x_organization_core_entity.class.getSimpleName());
//		dependency.storeJars.add(x_organization_core_express.class.getSimpleName());
//		dependency.storeJars.add(x_hotpic_core_entity.class.getSimpleName());
//		dependency.storeJars.add(x_collaboration_core_message.class.getSimpleName());
//		dependency.storeJars.add(x_cms_core_entity.class.getSimpleName());
//		dependency.storeJars.add(x_bbs_core_entity.class.getSimpleName());
//	}

//	public static final String name = "热点图片";
//	public static List<String> containerEntities = new ArrayList<>();
//	public static List<StorageType> usedStorageTypes = new ArrayList<>();
//	public static List<Class<? extends Compilable>> dependents = new ArrayList<>();
//
//	static {
//		containerEntities.add("com.x.hotpic.entity.HotPictureInfo");
//		containerEntities.add("com.x.bbs.entity.BBSSubjectInfo");
//		containerEntities.add("com.x.cms.core.entity.Document");
//		dependents.add(x_base_core_project.class);
//		dependents.add(x_organization_core_entity.class);
//		dependents.add(x_organization_core_express.class);
//		dependents.add(x_hotpic_core_entity.class);
//		dependents.add(x_collaboration_core_message.class);
//		dependents.add(x_cms_core_entity.class);
//		dependents.add(x_bbs_core_entity.class);
//	}

	protected void custom(File lib, String xLib) throws Exception {
	}

}
