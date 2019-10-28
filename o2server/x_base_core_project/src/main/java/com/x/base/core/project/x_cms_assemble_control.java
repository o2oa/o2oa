package com.x.base.core.project;

import com.x.base.core.entity.StorageType;
import com.x.base.core.project.annotation.Module;
import com.x.base.core.project.annotation.ModuleCategory;
import com.x.base.core.project.annotation.ModuleType;

@Module( type = ModuleType.ASSEMBLE, category = ModuleCategory.OFFICIAL, name = "内容管理", packageName = "com.x.cms.assemble.control", containerEntities = {
		"com.x.cms.core.entity.element.AppDict", "com.x.cms.core.entity.element.AppDictItem",
		"com.x.cms.core.entity.element.Form", "com.x.cms.core.entity.element.FormField",
		"com.x.cms.core.entity.element.QueryView", "com.x.cms.core.entity.element.Script",
		"com.x.cms.core.entity.element.TemplateForm", "com.x.cms.core.entity.element.View",
		"com.x.cms.core.entity.element.ViewCategory", "com.x.cms.core.entity.element.ViewFieldConfig",
		"com.x.cms.core.entity.AppInfo", "com.x.cms.core.entity.CategoryInfo", "com.x.cms.core.entity.CategoryExt",
		"com.x.cms.core.entity.Document", "com.x.cms.core.entity.DocumentViewRecord",
		"com.x.cms.core.entity.element.File", "com.x.cms.core.entity.FileInfo", "com.x.cms.core.entity.Log",
		"com.x.processplatform.core.entity.content.Attachment", "com.x.query.core.entity.Item",
		"com.x.query.core.entity.View", "com.x.cms.core.entity.ReadRemind", "com.x.cms.core.entity.DocumentCommend",
		"com.x.cms.core.entity.DocumentCommentInfo", "com.x.cms.core.entity.CmsBatchOperation", "com.x.cms.core.entity.Review",
		"com.x.cms.core.entity.DocumentCommentContent", "com.x.cms.core.entity.DocumentCommentCommend"}, storageTypes = {
				StorageType.cms, StorageType.processPlatform }, storeJars = { "x_processplatform_core_entity",
						"x_organization_core_entity", "x_organization_core_express", "x_cms_core_entity","x_cms_core_express",
						"x_query_core_entity", "x_query_core_express" })
public class x_cms_assemble_control extends Deployable {

//	public x_cms_assemble_control() {
//		super();
//		dependency.containerEntities.add("com.x.cms.core.entity.element.AppDict");
//		dependency.containerEntities.add("com.x.cms.core.entity.element.AppDictItem");
//		dependency.containerEntities.add("com.x.cms.core.entity.element.Form");
//		dependency.containerEntities.add("com.x.cms.core.entity.element.FormField");
//		dependency.containerEntities.add("com.x.cms.core.entity.element.QueryView");
//		dependency.containerEntities.add("com.x.cms.core.entity.element.Script");
//		dependency.containerEntities.add("com.x.cms.core.entity.element.TemplateForm");
//		dependency.containerEntities.add("com.x.cms.core.entity.element.View");
//		dependency.containerEntities.add("com.x.cms.core.entity.element.ViewCategory");
//		dependency.containerEntities.add("com.x.cms.core.entity.element.ViewFieldConfig");
//		dependency.containerEntities.add("com.x.cms.core.entity.AppInfo");
//		dependency.containerEntities.add("com.x.cms.core.entity.CategoryInfo");
//		dependency.containerEntities.add("com.x.cms.core.entity.CategoryExt");
//		dependency.containerEntities.add("com.x.cms.core.entity.Document");
//		dependency.containerEntities.add("com.x.cms.core.entity.DocumentViewRecord");
//		dependency.containerEntities.add("com.x.cms.core.entity.element.File");
//		dependency.containerEntities.add("com.x.cms.core.entity.FileInfo");
//		dependency.containerEntities.add("com.x.cms.core.entity.Log");
//		dependency.containerEntities.add("com.x.processplatform.core.entity.content.Attachment");
//		dependency.containerEntities.add("com.x.query.core.entity.Item");
//		dependency.containerEntities.add("com.x.query.core.entity.View");
//		dependency.containerEntities.add("com.x.cms.core.entity.ReadRemind");
//		dependency.containerEntities.add("com.x.cms.core.entity.DocumentCommend");
//		dependency.containerEntities.add("com.x.cms.core.entity.DocumentCommentInfo");
//		dependency.containerEntities.add("com.x.cms.core.entity.CmsBatchOperation");
//
//		dependency.storageTypes.add(StorageType.cms.toString());
//		dependency.storageTypes.add(StorageType.processPlatform.toString());
//		dependency.storeJars.add(x_processplatform_core_entity.class.getSimpleName());
//		dependency.storeJars.add(x_organization_core_entity.class.getSimpleName());
//		dependency.storeJars.add(x_organization_core_express.class.getSimpleName());
//		dependency.storeJars.add(x_cms_core_entity.class.getSimpleName());
//		dependency.storeJars.add(x_query_core_entity.class.getSimpleName());
//		dependency.storeJars.add(x_query_core_express.class.getSimpleName());
//	}

}
