package com.x.base.core.project;

import com.x.base.core.entity.StorageType;
import com.x.base.core.project.annotation.Module;
import com.x.base.core.project.annotation.ModuleCategory;
import com.x.base.core.project.annotation.ModuleType;

@Module(type = ModuleType.ASSEMBLE, category = ModuleCategory.OFFICIAL, name = "公共模块", packageName = "com.x.general.assemble.control", containerEntities = {
		"com.x.general.core.entity.area.District" }, storageTypes = {
				StorageType.file }, storeJars = { "x_general_core_entity" })
public class x_general_assemble_control extends Deployable {

//	public x_general_assemble_control() {
//		super();
//		dependency.containerEntities.add("com.x.general.core.entity.area.District");
//		dependency.storageTypes.add(StorageType.file.toString());
//		dependency.storeJars.add(x_general_core_entity.class.getSimpleName());
//	}
}
