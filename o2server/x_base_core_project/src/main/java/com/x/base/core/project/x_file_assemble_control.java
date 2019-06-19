package com.x.base.core.project;

import com.x.base.core.entity.StorageType;
import com.x.base.core.project.annotation.Module;
import com.x.base.core.project.annotation.ModuleCategory;
import com.x.base.core.project.annotation.ModuleType;

@Module(type = ModuleType.ASSEMBLE, category = ModuleCategory.OFFICIAL, name = "云文件", packageName = "com.x.file.assemble.control", containerEntities = {
		"com.x.file.core.entity.personal.Folder", "com.x.file.core.entity.personal.Attachment",
		"com.x.file.core.entity.open.File" }, storageTypes = { StorageType.file }, storeJars = { "x_file_core_entity",
				"x_organization_core_express", "x_organization_core_entity" })
public class x_file_assemble_control extends Deployable {

//	public x_file_assemble_control() {
//		super();
//		dependency.containerEntities.add("com.x.file.core.entity.personal.Folder");
//		dependency.containerEntities.add("com.x.file.core.entity.personal.Attachment");
//		dependency.containerEntities.add("com.x.file.core.entity.open.File");
//		dependency.storageTypes.add(StorageType.file.toString());
//		dependency.storeJars.add(x_file_core_entity.class.getSimpleName());
//		dependency.storeJars.add(x_organization_core_entity.class.getSimpleName());
//		dependency.storeJars.add(x_organization_core_express.class.getSimpleName());
//	}
}
