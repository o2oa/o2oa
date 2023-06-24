package com.x.base.core.project;

import com.x.base.core.project.annotation.Module;
import com.x.base.core.project.annotation.ModuleCategory;
import com.x.base.core.project.annotation.ModuleType;

@Module(type = ModuleType.ASSEMBLE, category = ModuleCategory.OFFICIAL, name = "关联内容", packageName = "com.x.correlation.assemble.surface", containerEntities = {}, storageTypes = {}, storeJars = {
		"x_correlation_core_entity" })
public class x_correlation_assemble_surface extends Deployable {
}
