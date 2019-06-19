package com.x.base.core.project;

import com.x.base.core.project.annotation.Module;
import com.x.base.core.project.annotation.ModuleCategory;
import com.x.base.core.project.annotation.ModuleType;

@Module(type = ModuleType.ASSEMBLE, category = ModuleCategory.OFFICIAL, name = "门户设计", packageName = "com.x.portal.assemble.designer", containerEntities = {
		"com.x.portal.core.entity.Portal", "com.x.portal.core.entity.Widget", "com.x.portal.core.entity.Page",
		"com.x.portal.core.entity.Script", "com.x.portal.core.entity.File",
		"com.x.portal.core.entity.TemplatePage" }, storeJars = { "x_organization_core_entity",
				"x_organization_core_express", "x_portal_core_entity" })
public class x_portal_assemble_designer extends Deployable {

//	public x_portal_assemble_designer() {
//		super();
//		dependency.containerEntities.add("com.x.portal.core.entity.Portal");
//		dependency.containerEntities.add("com.x.portal.core.entity.Widget");
//		dependency.containerEntities.add("com.x.portal.core.entity.Page");
//		dependency.containerEntities.add("com.x.portal.core.entity.Script");
//		dependency.containerEntities.add("com.x.portal.core.entity.File");
//		dependency.containerEntities.add("com.x.portal.core.entity.TemplatePage");
//		dependency.storeJars.add(x_organization_core_entity.class.getSimpleName());
//		dependency.storeJars.add(x_organization_core_express.class.getSimpleName());
//		dependency.storeJars.add(x_portal_core_entity.class.getSimpleName());
//	}
}
