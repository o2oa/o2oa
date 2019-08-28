package com.x.base.core.project;

import com.x.base.core.entity.StorageType;
import com.x.base.core.project.annotation.Module;
import com.x.base.core.project.annotation.ModuleCategory;
import com.x.base.core.project.annotation.ModuleType;

@Module(type = ModuleType.SERVICE, category = ModuleCategory.OFFICIAL, name = "流程服务", packageName = "com.x.processplatform.service.processing", containerEntities = {
		"com.x.processplatform.core.entity.content.Attachment",
		"com.x.processplatform.core.entity.content.TaskCompleted",
		"com.x.processplatform.core.entity.content.ReadCompleted", "com.x.processplatform.core.entity.content.Review",
		"com.x.processplatform.core.entity.content.Hint", "com.x.processplatform.core.entity.content.WorkCompleted",
		"com.x.processplatform.core.entity.content.WorkLog", "com.x.processplatform.core.entity.content.Task",
		"com.x.processplatform.core.entity.content.Work", "com.x.processplatform.core.entity.content.Read",
		"com.x.processplatform.core.entity.content.SerialNumber", "com.x.processplatform.core.entity.element.End",
		"com.x.processplatform.core.entity.element.Application",
		"com.x.processplatform.core.entity.element.ApplicationDict",
		"com.x.processplatform.core.entity.element.ApplicationDictItem",
		"com.x.processplatform.core.entity.element.Script", "com.x.processplatform.core.entity.element.Cancel",
		"com.x.processplatform.core.entity.element.Merge", "com.x.processplatform.core.entity.element.Route",
		"com.x.processplatform.core.entity.element.Choice", "com.x.processplatform.core.entity.element.Invoke",
		"com.x.processplatform.core.entity.element.Manual", "com.x.processplatform.core.entity.element.Parallel",
		"com.x.processplatform.core.entity.element.Begin", "com.x.processplatform.core.entity.element.Split",
		"com.x.processplatform.core.entity.element.Message", "com.x.processplatform.core.entity.element.Process",
		"com.x.processplatform.core.entity.element.Service", "com.x.processplatform.core.entity.element.Agent",
		"com.x.processplatform.core.entity.element.Delay", "com.x.processplatform.core.entity.element.File",
		"com.x.processplatform.core.entity.element.Form", "com.x.processplatform.core.entity.element.FormField",
		"com.x.processplatform.core.entity.element.Embed", "com.x.processplatform.core.entity.element.Projection",
		"com.x.processplatform.core.entity.element.Mapping", "com.x.processplatform.core.entity.log.ProcessingError",
		"com.x.query.core.entity.Item",
		"com.x.query.dynamic.entity.*" }, storageTypes = { StorageType.processPlatform }, storeJars = {
				"x_organization_core_entity", "x_organization_core_express", "x_processplatform_core_entity",
				"x_query_core_entity" }, dynamicJars = { "x_query_dynamic_entity" })
public class x_processplatform_service_processing extends Deployable {

}
