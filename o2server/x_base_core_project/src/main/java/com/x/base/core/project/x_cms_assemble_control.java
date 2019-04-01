package com.x.base.core.project;

import com.x.base.core.entity.StorageType;
import com.x.base.core.project.annotation.Module;
import com.x.base.core.project.annotation.ModuleCategory;
import com.x.base.core.project.annotation.ModuleType;

@Module(type = ModuleType.ASSEMBLE, category = ModuleCategory.OFFICIAL, name = "内容管理")
public class x_cms_assemble_control extends AssembleA {

	public x_cms_assemble_control() {
		super();
		dependency.containerEntities.add("com.x.cms.core.entity.element.AppDict");
		dependency.containerEntities.add("com.x.cms.core.entity.element.AppDictItem");
		dependency.containerEntities.add("com.x.cms.core.entity.element.Form");
		dependency.containerEntities.add("com.x.cms.core.entity.element.FormField");
		dependency.containerEntities.add("com.x.cms.core.entity.element.QueryView");
		dependency.containerEntities.add("com.x.cms.core.entity.element.Script");
		dependency.containerEntities.add("com.x.cms.core.entity.element.TemplateForm");
		dependency.containerEntities.add("com.x.cms.core.entity.element.View");
		dependency.containerEntities.add("com.x.cms.core.entity.element.ViewCategory");
		dependency.containerEntities.add("com.x.cms.core.entity.element.ViewFieldConfig");
		dependency.containerEntities.add("com.x.cms.core.entity.AppInfo");
		dependency.containerEntities.add("com.x.cms.core.entity.CategoryInfo");
		dependency.containerEntities.add("com.x.cms.core.entity.CategoryExt");
		dependency.containerEntities.add("com.x.cms.core.entity.Document");
		dependency.containerEntities.add("com.x.cms.core.entity.DocumentViewRecord");
		dependency.containerEntities.add("com.x.cms.core.entity.element.File");
		dependency.containerEntities.add("com.x.cms.core.entity.FileInfo");
		dependency.containerEntities.add("com.x.cms.core.entity.Log");
		dependency.containerEntities.add("com.x.processplatform.core.entity.content.Attachment");
		dependency.containerEntities.add("com.x.query.core.entity.Item");
		dependency.containerEntities.add("com.x.query.core.entity.View");
		dependency.containerEntities.add("com.x.cms.core.entity.ReadRemind");

		dependency.storageTypes.add(StorageType.cms.toString());
		dependency.storageTypes.add(StorageType.processPlatform.toString());
		dependency.storeJars.add(x_processplatform_core_entity.class.getSimpleName());
		dependency.storeJars.add(x_organization_core_entity.class.getSimpleName());
		dependency.storeJars.add(x_organization_core_express.class.getSimpleName());
		dependency.storeJars.add(x_cms_core_entity.class.getSimpleName());
		dependency.storeJars.add(x_query_core_entity.class.getSimpleName());
		dependency.storeJars.add(x_query_core_express.class.getSimpleName());
	}

}
