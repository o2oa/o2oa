package com.x.base.core.project;

import com.x.base.core.entity.StorageType;
import com.x.base.core.project.annotation.Module;
import com.x.base.core.project.annotation.ModuleCategory;
import com.x.base.core.project.annotation.ModuleType;

@Module(type = ModuleType.ASSEMBLE, category = ModuleCategory.OFFICIAL, name = "公共模块", packageName = "com.x.general.assemble.control", containerEntities = {
		"com.x.general.core.entity.area.District", "com.x.general.core.entity.GeneralFile",
		"com.x.processplatform.core.entity.element.Route" }, storageTypes = { StorageType.file,
				StorageType.general }, storeJars = { "x_organization_core_entity", "x_organization_core_express",
						"x_general_core_entity", "x_processplatform_core_entity" })
public class x_general_assemble_control extends Deployable {
}
