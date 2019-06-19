package com.x.base.core.project;

import com.x.base.core.entity.StorageType;
import com.x.base.core.project.annotation.Module;
import com.x.base.core.project.annotation.ModuleCategory;
import com.x.base.core.project.annotation.ModuleType;

@Module(type = ModuleType.ASSEMBLE, category = ModuleCategory.OFFICIAL, name = "数据查询", packageName = "com.x.query.assemble.surface", containerEntities = {
		"com.x.query.core.entity.Item", "com.x.query.core.entity.Query", "com.x.query.core.entity.View",
		"com.x.query.core.entity.Stat", "com.x.query.core.entity.Reveal", "com.x.query.core.entity.segment.Word",
		"com.x.query.core.entity.segment.Entry", "com.x.query.core.entity.neural.Entry",
		"com.x.query.core.entity.neural.InText", "com.x.query.core.entity.neural.OutText",
		"com.x.query.core.entity.neural.InValue", "com.x.query.core.entity.neural.OutValue",
		"com.x.query.core.entity.neural.Model", "com.x.query.core.entity.schema.Table",
		"com.x.query.core.entity.schema.Statement", "com.x.processplatform.core.entity.content.Review",
		"com.x.processplatform.core.entity.content.Work", "com.x.processplatform.core.entity.content.WorkCompleted",
		"com.x.processplatform.core.entity.content.Attachment", "com.x.cms.core.entity.Document",
		"com.x.cms.core.entity.AppInfo", "com.x.cms.core.entity.CategoryInfo",
		"com.x.query.dynamic.entity.*" }, storageTypes = { StorageType.processPlatform, StorageType.cms }, storeJars = {
				"x_query_core_entity", "x_organization_core_entity", "x_organization_core_express",
				"x_processplatform_core_entity", "x_cms_core_entity",
				"x_query_core_express" }, dynamicJars = { "x_query_dynamic_entity" })
public class x_query_assemble_surface extends Deployable {

//	public x_query_assemble_surface() {
//		super();
//		dependency.containerEntities.add("com.x.query.core.entity.Item");
//		dependency.containerEntities.add("com.x.query.core.entity.Query");
//		dependency.containerEntities.add("com.x.query.core.entity.View");
//		dependency.containerEntities.add("com.x.query.core.entity.Stat");
//		dependency.containerEntities.add("com.x.query.core.entity.Reveal");
//		dependency.containerEntities.add("com.x.query.core.entity.segment.Word");
//		dependency.containerEntities.add("com.x.query.core.entity.segment.Entry");
//		dependency.containerEntities.add("com.x.query.core.entity.neural.Entry");
//		dependency.containerEntities.add("com.x.query.core.entity.neural.InText");
//		dependency.containerEntities.add("com.x.query.core.entity.neural.OutText");
//		dependency.containerEntities.add("com.x.query.core.entity.neural.InValue");
//		dependency.containerEntities.add("com.x.query.core.entity.neural.OutValue");
//		dependency.containerEntities.add("com.x.query.core.entity.neural.Model");
//		dependency.containerEntities.add("com.x.query.core.entity.schema.Table");
//		dependency.containerEntities.add("com.x.query.core.entity.schema.Statement");
//		dependency.containerEntities.add("com.x.processplatform.core.entity.content.Review");
//		dependency.containerEntities.add("com.x.processplatform.core.entity.content.Work");
//		dependency.containerEntities.add("com.x.processplatform.core.entity.content.WorkCompleted");
//		dependency.containerEntities.add("com.x.processplatform.core.entity.content.Attachment");
//		dependency.containerEntities.add("com.x.cms.core.entity.Document");
//		dependency.containerEntities.add("com.x.cms.core.entity.AppInfo");
//		dependency.containerEntities.add("com.x.cms.core.entity.CategoryInfo");
//		dependency.containerEntities.add("com.x.query.dynamic.entity.*");
//		dependency.storageTypes.add(StorageType.processPlatform.toString());
//		dependency.storageTypes.add(StorageType.cms.toString());
//		dependency.storeJars.add(x_query_core_entity.class.getSimpleName());
//		dependency.storeJars.add(x_organization_core_entity.class.getSimpleName());
//		dependency.storeJars.add(x_organization_core_express.class.getSimpleName());
//		dependency.storeJars.add(x_processplatform_core_entity.class.getSimpleName());
//		dependency.storeJars.add(x_cms_core_entity.class.getSimpleName());
//		dependency.storeJars.add(x_query_core_express.class.getSimpleName());
//		dependency.dynamicJars.add("x_query_dynamic_entity");
//	}
}
