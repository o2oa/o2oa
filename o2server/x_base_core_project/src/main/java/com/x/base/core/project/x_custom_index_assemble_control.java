package com.x.base.core.project;

import com.x.base.core.entity.StorageType;
import com.x.base.core.project.Deployable;
import com.x.base.core.project.annotation.Module;
import com.x.base.core.project.annotation.ModuleCategory;
import com.x.base.core.project.annotation.ModuleType;

@Module(type = ModuleType.ASSEMBLE, category = ModuleCategory.OFFICIAL, name = "台帐应用.", packageName = "com.x.custom.index.assemble.control", containerEntities = {
		"com.x.custom.index.core.entity.Reveal", "com.x.custom.index.core.entity.Custom",
		"com.x.processplatform.core.entity.element.Application", "com.x.processplatform.core.entity.element.Process",
		"com.x.processplatform.core.entity.content.Attachment",
		"com.x.processplatform.core.entity.content.TaskCompleted",
		"com.x.processplatform.core.entity.content.ReadCompleted", "com.x.processplatform.core.entity.content.Review",
		"com.x.processplatform.core.entity.content.Record", "com.x.processplatform.core.entity.content.WorkLog",
		"com.x.processplatform.core.entity.content.Work", "com.x.processplatform.core.entity.content.WorkCompleted",
		"com.x.query.core.entity.Item", "com.x.cms.core.entity.AppInfo", "com.x.cms.core.entity.CategoryInfo",
		"com.x.cms.core.entity.Document", "com.x.cms.core.entity.FileInfo", "com.x.cms.core.entity.Review",
		"com.x.general.core.entity.GeneralFile" }, storeJars = { "x_custom_index_core_entity",
				"x_organization_core_entity", "x_cms_core_entity", "x_processplatform_core_entity",
				"x_query_core_entity", "x_query_core_express", "x_organization_core_express",
				"x_general_core_entity" }, customJars = {}, storageTypes = { StorageType.custom, StorageType.cms,
						StorageType.processPlatform })
public class x_custom_index_assemble_control extends Deployable {
}
