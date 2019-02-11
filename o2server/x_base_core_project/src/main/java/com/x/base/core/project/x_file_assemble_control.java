package com.x.base.core.project;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.entity.StorageType;
import com.x.base.core.project.annotation.Module;
import com.x.base.core.project.annotation.ModuleCategory;
import com.x.base.core.project.annotation.ModuleType;
import com.x.base.core.project.gson.XGsonBuilder;

@Module(type = ModuleType.ASSEMBLE, category = ModuleCategory.OFFICIAL, name = "云文件")
public class x_file_assemble_control extends AssembleA {
	
	public x_file_assemble_control() {
		super();
		dependency.containerEntities.add("com.x.file.core.entity.personal.Folder");
		dependency.containerEntities.add("com.x.file.core.entity.personal.Attachment");
		dependency.containerEntities.add("com.x.file.core.entity.open.File");
		dependency.storageTypes.add(StorageType.file.toString());
		dependency.storeJars.add(x_collaboration_core_entity.class.getSimpleName());
		dependency.storeJars.add(x_collaboration_core_message.class.getSimpleName());
		dependency.storeJars.add(x_file_core_entity.class.getSimpleName());
		dependency.storeJars.add(x_organization_core_entity.class.getSimpleName());
		dependency.storeJars.add(x_organization_core_express.class.getSimpleName());
	}

//	public static final Dependency dependency = new Dependency();
//
//	static {
//		dependency.containerEntities.add("com.x.file.core.entity.personal.Folder");
//		dependency.containerEntities.add("com.x.file.core.entity.personal.Attachment");
//		dependency.containerEntities.add("com.x.file.core.entity.open.File");
//		dependency.storageTypes.add(StorageType.file.toString());
//		dependency.storeJars.add(x_collaboration_core_entity.class.getSimpleName());
//		dependency.storeJars.add(x_collaboration_core_message.class.getSimpleName());
//		dependency.storeJars.add(x_file_core_entity.class.getSimpleName());
//		dependency.storeJars.add(x_organization_core_entity.class.getSimpleName());
//		dependency.storeJars.add(x_organization_core_express.class.getSimpleName());
//	}
//
//	public static final String name = "云文件";
//	public static List<String> containerEntities = new ArrayList<>();
//	public static List<StorageType> usedStorageTypes = new ArrayList<>();
//	public static List<Class<? extends Compilable>> dependents = new ArrayList<>();
//
//	static {
//		containerEntities.add("com.x.file.core.entity.personal.Folder");
//		containerEntities.add("com.x.file.core.entity.personal.Attachment");
//		containerEntities.add("com.x.file.core.entity.open.File");
//		usedStorageTypes.add(StorageType.file);
//		dependents.add(x_base_core_project.class);
//		dependents.add(x_collaboration_core_entity.class);
//		dependents.add(x_collaboration_core_message.class);
//		dependents.add(x_file_core_entity.class);
//		dependents.add(x_organization_core_entity.class);
//		dependents.add(x_organization_core_express.class);
//	}

	protected void custom(File lib, String xLib) throws Exception {

	}

}
