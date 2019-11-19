package com.x.base.core.project;

import com.x.base.core.project.annotation.Module;
import com.x.base.core.project.annotation.ModuleCategory;
import com.x.base.core.project.annotation.ModuleType;

@Module(type = ModuleType.ASSEMBLE, category = ModuleCategory.OFFICIAL, name = "门户", packageName = "com.x.portal.assemble.surface", containerEntities = {
		"com.x.portal.core.entity.Portal", "com.x.portal.core.entity.Widget", "com.x.portal.core.entity.Page",
		"com.x.portal.core.entity.Script", "com.x.portal.core.entity.File" }, storeJars = {
				"x_organization_core_entity", "x_organization_core_express", "x_portal_core_entity" })
public class x_portal_assemble_surface extends Deployable {
}