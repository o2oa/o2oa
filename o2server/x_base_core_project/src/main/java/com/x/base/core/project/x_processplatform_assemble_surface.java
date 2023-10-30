package com.x.base.core.project;

import com.x.base.core.entity.StorageType;
import com.x.base.core.project.annotation.Module;
import com.x.base.core.project.annotation.ModuleCategory;
import com.x.base.core.project.annotation.ModuleType;

@Module(type = ModuleType.ASSEMBLE, category = ModuleCategory.OFFICIAL, name = "流程", packageName = "com.x.processplatform.assemble.surface", containerEntities = {
		"com.x.processplatform.core.entity.content.Snap", "com.x.processplatform.core.entity.content.Draft",
		"com.x.processplatform.core.entity.content.Attachment", "com.x.processplatform.core.entity.content.Read",
		"com.x.processplatform.core.entity.content.ReadCompleted", "com.x.processplatform.core.entity.content.Review",
		"com.x.processplatform.core.entity.content.Record", "com.x.processplatform.core.entity.content.SerialNumber",
		"com.x.processplatform.core.entity.content.Task", "com.x.processplatform.core.entity.content.TaskCompleted",
		"com.x.processplatform.core.entity.content.Work", "com.x.processplatform.core.entity.content.WorkCompleted",
		"com.x.processplatform.core.entity.content.WorkLog", "com.x.processplatform.core.entity.content.Record",
		"com.x.processplatform.core.entity.content.KeyLock", "com.x.processplatform.core.entity.content.DocSign",
		"com.x.processplatform.core.entity.content.DocSignScrawl", "com.x.processplatform.core.entity.content.DataRecord",
		"com.x.processplatform.core.entity.content.TaskProcessMode",
		"com.x.processplatform.core.entity.content.DocumentVersion", "com.x.processplatform.core.entity.element.Agent",
		"com.x.processplatform.core.entity.content.Handover",
		"com.x.processplatform.core.entity.element.Application", "com.x.processplatform.core.entity.element.Publish",
		"com.x.processplatform.core.entity.element.ApplicationDict",
		"com.x.processplatform.core.entity.element.ApplicationDictItem",
		"com.x.processplatform.core.entity.element.Begin", "com.x.processplatform.core.entity.element.Cancel",
		"com.x.processplatform.core.entity.element.Choice", "com.x.processplatform.core.entity.element.Delay",
		"com.x.processplatform.core.entity.element.Embed", "com.x.processplatform.core.entity.element.End",
		"com.x.processplatform.core.entity.element.File", "com.x.processplatform.core.entity.element.Form",
		"com.x.processplatform.core.entity.element.FormField", "com.x.processplatform.core.entity.element.Invoke",
		"com.x.processplatform.core.entity.element.Manual", "com.x.processplatform.core.entity.element.Merge",
		"com.x.processplatform.core.entity.element.Parallel", "com.x.processplatform.core.entity.element.Process",
		"com.x.processplatform.core.entity.element.Route", "com.x.processplatform.core.entity.element.Script",
		"com.x.processplatform.core.entity.element.Service", "com.x.processplatform.core.entity.element.Split",
		"com.x.processplatform.core.entity.element.Mapping", "com.x.query.core.entity.Item",
		"com.x.cms.core.entity.Document", "com.x.cms.core.entity.Review", "com.x.cms.core.entity.element.Script",
		"com.x.portal.core.entity.Script", "com.x.program.center.core.entity.Script",
		"com.x.general.core.entity.GeneralFile" }, storageTypes = { StorageType.processPlatform,
				StorageType.general }, storeJars = { "x_processplatform_core_express", "x_correlation_core_express",
						"x_organization_core_entity", "x_organization_core_express", "x_processplatform_core_entity",
						"x_correlation_core_entity", "x_query_core_entity", "x_cms_core_entity", "x_portal_core_entity",
						"x_general_core_entity", "x_program_center_core_entity" })
public class x_processplatform_assemble_surface extends Deployable {
}
