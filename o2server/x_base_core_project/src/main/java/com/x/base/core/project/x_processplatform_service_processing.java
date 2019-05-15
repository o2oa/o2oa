package com.x.base.core.project;

import com.x.base.core.entity.StorageType;
import com.x.base.core.project.annotation.Module;
import com.x.base.core.project.annotation.ModuleCategory;
import com.x.base.core.project.annotation.ModuleType;

@Module(type = ModuleType.SERVICE, category = ModuleCategory.OFFICIAL, name = "流程服务")
public class x_processplatform_service_processing extends ServiceA {
	
	public x_processplatform_service_processing() {
		super();
		dependency.containerEntities.add("com.x.processplatform.core.entity.content.Attachment");
		dependency.containerEntities.add("com.x.processplatform.core.entity.content.TaskCompleted");
		dependency.containerEntities.add("com.x.processplatform.core.entity.content.ReadCompleted");
		dependency.containerEntities.add("com.x.processplatform.core.entity.content.Review");
		dependency.containerEntities.add("com.x.processplatform.core.entity.content.Hint");
		dependency.containerEntities.add("com.x.processplatform.core.entity.content.WorkCompleted");
		dependency.containerEntities.add("com.x.processplatform.core.entity.content.WorkLog");
		dependency.containerEntities.add("com.x.processplatform.core.entity.content.Task");
		dependency.containerEntities.add("com.x.processplatform.core.entity.content.Work");
		dependency.containerEntities.add("com.x.processplatform.core.entity.content.Read");
		dependency.containerEntities.add("com.x.processplatform.core.entity.content.SerialNumber");
		dependency.containerEntities.add("com.x.processplatform.core.entity.element.End");
		dependency.containerEntities.add("com.x.processplatform.core.entity.element.Application");
		dependency.containerEntities.add("com.x.processplatform.core.entity.element.ApplicationDict");
		dependency.containerEntities.add("com.x.processplatform.core.entity.element.ApplicationDictItem");
		dependency.containerEntities.add("com.x.processplatform.core.entity.element.Script");
		dependency.containerEntities.add("com.x.processplatform.core.entity.element.Cancel");
		dependency.containerEntities.add("com.x.processplatform.core.entity.element.Merge");
		dependency.containerEntities.add("com.x.processplatform.core.entity.element.Route");
		dependency.containerEntities.add("com.x.processplatform.core.entity.element.Choice");
		dependency.containerEntities.add("com.x.processplatform.core.entity.element.Invoke");
		dependency.containerEntities.add("com.x.processplatform.core.entity.element.Manual");
		dependency.containerEntities.add("com.x.processplatform.core.entity.element.Parallel");
		dependency.containerEntities.add("com.x.processplatform.core.entity.element.Begin");
		dependency.containerEntities.add("com.x.processplatform.core.entity.element.Split");
		dependency.containerEntities.add("com.x.processplatform.core.entity.element.Message");
		dependency.containerEntities.add("com.x.processplatform.core.entity.element.Process");
		dependency.containerEntities.add("com.x.processplatform.core.entity.element.Service");
		dependency.containerEntities.add("com.x.processplatform.core.entity.element.Agent");
		dependency.containerEntities.add("com.x.processplatform.core.entity.element.Delay");
		dependency.containerEntities.add("com.x.processplatform.core.entity.element.File");
		dependency.containerEntities.add("com.x.processplatform.core.entity.element.Form");
		dependency.containerEntities.add("com.x.processplatform.core.entity.element.FormField");
		dependency.containerEntities.add("com.x.processplatform.core.entity.element.Embed");
		dependency.containerEntities.add("com.x.processplatform.core.entity.log.ProcessingError");
		dependency.containerEntities.add("com.x.query.core.entity.Item");
		dependency.storageTypes.add(StorageType.processPlatform.toString());
		dependency.storeJars.add(x_organization_core_entity.class.getSimpleName());
		dependency.storeJars.add(x_organization_core_express.class.getSimpleName());
		dependency.storeJars.add(x_processplatform_core_entity.class.getSimpleName());
//		dependency.storeJars.add(x_collaboration_core_entity.class.getSimpleName());
//		dependency.storeJars.add(x_collaboration_core_message.class.getSimpleName());
		dependency.storeJars.add(x_query_core_entity.class.getSimpleName());
	}
}
