package com.x.base.core.project;

import com.x.base.core.entity.StorageType;
import com.x.base.core.project.annotation.Module;
import com.x.base.core.project.annotation.ModuleCategory;
import com.x.base.core.project.annotation.ModuleType;

@Module(type = ModuleType.SERVICE, category = ModuleCategory.OFFICIAL, name = "数据查询服务", packageName = "com.x.query.service.processing", containerEntities = {

		"com.x.query.core.entity.Item", "com.x.query.core.entity.Query", "com.x.query.core.entity.View",
		"com.x.query.core.entity.Stat", "com.x.query.core.entity.Reveal", "com.x.query.core.entity.segment.Word",
		"com.x.query.core.entity.segment.Entry", "com.x.query.core.entity.neural.Entry",
		"com.x.query.core.entity.neural.InText", "com.x.query.core.entity.neural.OutText",
		"com.x.query.core.entity.neural.InValue", "com.x.query.core.entity.neural.OutValue",
		"com.x.query.core.entity.neural.Model", "com.x.query.core.entity.schema.*",
		"com.x.processplatform.core.entity.content.Review", "com.x.processplatform.core.entity.content.Work",
		"com.x.processplatform.core.entity.content.WorkCompleted",
		"com.x.processplatform.core.entity.content.Attachment", "com.x.cms.core.entity.Document",
		"com.x.cms.core.entity.FileInfo", "com.x.cms.core.entity.AppInfo",
		"com.x.cms.core.entity.CategoryInfo" }, storageTypes = { StorageType.processPlatform,
				StorageType.cms }, storeJars = { "x_query_core_entity", "x_organization_core_entity",
						"x_organization_core_express", "x_processplatform_core_entity", "x_cms_core_entity" })
public class x_query_service_processing extends Deployable {

}
