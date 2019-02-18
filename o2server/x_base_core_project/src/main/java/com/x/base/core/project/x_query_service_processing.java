package com.x.base.core.project;

import com.x.base.core.entity.StorageType;
import com.x.base.core.project.annotation.Module;
import com.x.base.core.project.annotation.ModuleCategory;
import com.x.base.core.project.annotation.ModuleType;

@Module(type = ModuleType.SERVICE, category = ModuleCategory.OFFICIAL, name = "数据查询服务")
public class x_query_service_processing extends ServiceA {

	public x_query_service_processing() {
		super();
		dependency.containerEntities.add("com.x.query.core.entity.Item");
		dependency.containerEntities.add("com.x.query.core.entity.Query");
		dependency.containerEntities.add("com.x.query.core.entity.View");
		dependency.containerEntities.add("com.x.query.core.entity.Stat");
		dependency.containerEntities.add("com.x.query.core.entity.Reveal");
		dependency.containerEntities.add("com.x.query.core.entity.segment.Word");
		dependency.containerEntities.add("com.x.query.core.entity.segment.Entry");
		dependency.containerEntities.add("com.x.query.core.entity.neural.Entry");
		dependency.containerEntities.add("com.x.query.core.entity.neural.InText");
		dependency.containerEntities.add("com.x.query.core.entity.neural.OutText");
		dependency.containerEntities.add("com.x.query.core.entity.neural.InValue");
		dependency.containerEntities.add("com.x.query.core.entity.neural.OutValue");
		dependency.containerEntities.add("com.x.query.core.entity.neural.Model");
		dependency.containerEntities.add("com.x.query.core.entity.schema.*");
		dependency.containerEntities.add("com.x.processplatform.core.entity.content.Review");
		dependency.containerEntities.add("com.x.processplatform.core.entity.content.Work");
		dependency.containerEntities.add("com.x.processplatform.core.entity.content.WorkCompleted");
		dependency.containerEntities.add("com.x.processplatform.core.entity.content.Attachment");
		dependency.containerEntities.add("com.x.cms.core.entity.Document");
		dependency.containerEntities.add("com.x.cms.core.entity.FileInfo");
		dependency.containerEntities.add("com.x.cms.core.entity.AppInfo");
		dependency.containerEntities.add("com.x.cms.core.entity.CategoryInfo");
		dependency.storageTypes.add(StorageType.processPlatform.toString());
		dependency.storageTypes.add(StorageType.cms.toString());
		dependency.storeJars.add(x_query_core_entity.class.getSimpleName());
		dependency.storeJars.add(x_organization_core_entity.class.getSimpleName());
		dependency.storeJars.add(x_organization_core_express.class.getSimpleName());
		dependency.storeJars.add(x_processplatform_core_entity.class.getSimpleName());
		dependency.storeJars.add(x_cms_core_entity.class.getSimpleName());
	}
}
