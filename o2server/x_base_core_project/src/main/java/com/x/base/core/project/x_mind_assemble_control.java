package com.x.base.core.project;

import com.x.base.core.project.annotation.Module;
import com.x.base.core.project.annotation.ModuleCategory;
import com.x.base.core.project.annotation.ModuleType;

@Module(type = ModuleType.ASSEMBLE, category = ModuleCategory.OFFICIAL, name = "脑图", packageName = "com.x.mind.assemble.control", containerEntities = {
		"com.x.mind.entity.MindBaseInfo", "com.x.mind.entity.MindContentInfo", "com.x.mind.entity.MindFolderInfo",
		"com.x.mind.entity.MindIconInfo", "com.x.mind.entity.MindRecycleInfo", "com.x.mind.entity.MindShareRecord",
		"com.x.mind.entity.MindVersionInfo", "com.x.mind.entity.MindVersionContent" }, storeJars = {
				"x_organization_core_entity", "x_organization_core_express", "x_mind_core_entity" })
public class x_mind_assemble_control extends Deployable {

//	public x_mind_assemble_control() {
//		super();
//		dependency.containerEntities.add("com.x.mind.entity.MindBaseInfo");
//		dependency.containerEntities.add("com.x.mind.entity.MindContentInfo");
//		dependency.containerEntities.add("com.x.mind.entity.MindFolderInfo");
//		dependency.containerEntities.add("com.x.mind.entity.MindIconInfo");
//		dependency.containerEntities.add("com.x.mind.entity.MindRecycleInfo");
//		dependency.containerEntities.add("com.x.mind.entity.MindShareRecord");
//		dependency.containerEntities.add("com.x.mind.entity.MindVersionInfo");
//		dependency.containerEntities.add("com.x.mind.entity.MindVersionContent");
//		dependency.storeJars.add(x_organization_core_entity.class.getSimpleName());
//		dependency.storeJars.add(x_organization_core_express.class.getSimpleName());
//		dependency.storeJars.add(x_mind_core_entity.class.getSimpleName());
//	}
}
