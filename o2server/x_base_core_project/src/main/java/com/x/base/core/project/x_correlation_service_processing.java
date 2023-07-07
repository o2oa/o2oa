package com.x.base.core.project;

import com.x.base.core.project.annotation.Module;
import com.x.base.core.project.annotation.ModuleCategory;
import com.x.base.core.project.annotation.ModuleType;

@Module(type = ModuleType.SERVICE, category = ModuleCategory.OFFICIAL, name = "关联内容服务", packageName = "com.x.correlation.service.processing", containerEntities = {
		"com.x.correlation.core.entity.content.Correlation", "com.x.processplatform.core.entity.content.Review",
		"com.x.cms.core.entity.Review", "com.x.processplatform.core.entity.content.Work",
		"com.x.processplatform.core.entity.content.WorkCompleted",
		"com.x.cms.core.entity.Document" }, storageTypes = {}, storeJars = { "x_organization_core_entity",
				"x_organization_core_express", "x_processplatform_core_entity", "x_processplatform_core_express",
				"x_query_core_entity", "x_cms_core_entity", "x_correlation_core_entity", "x_correlation_core_express" })
public class x_correlation_service_processing extends Deployable {

}