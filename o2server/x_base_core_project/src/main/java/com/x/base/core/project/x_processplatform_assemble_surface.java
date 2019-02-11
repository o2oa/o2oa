package com.x.base.core.project;

import com.x.base.core.entity.StorageType;
import com.x.base.core.project.annotation.Module;
import com.x.base.core.project.annotation.ModuleCategory;
import com.x.base.core.project.annotation.ModuleType;

@Module(type = ModuleType.ASSEMBLE, category = ModuleCategory.OFFICIAL, name = "流程")
public class x_processplatform_assemble_surface extends AssembleA {
	
	public x_processplatform_assemble_surface() {
		super();
		dependency.containerEntities.add("com.x.processplatform.core.entity.content.Attachment");
		dependency.containerEntities.add("com.x.processplatform.core.entity.content.Read");
		dependency.containerEntities.add("com.x.processplatform.core.entity.content.ReadCompleted");
		dependency.containerEntities.add("com.x.processplatform.core.entity.content.Review");
		dependency.containerEntities.add("com.x.processplatform.core.entity.content.Hint");
		dependency.containerEntities.add("com.x.processplatform.core.entity.content.SerialNumber");
		dependency.containerEntities.add("com.x.processplatform.core.entity.content.Task");
		dependency.containerEntities.add("com.x.processplatform.core.entity.content.TaskCompleted");
		dependency.containerEntities.add("com.x.processplatform.core.entity.content.Work");
		dependency.containerEntities.add("com.x.processplatform.core.entity.content.WorkCompleted");
		dependency.containerEntities.add("com.x.processplatform.core.entity.content.WorkLog");
		dependency.containerEntities.add("com.x.processplatform.core.entity.element.Agent");
		dependency.containerEntities.add("com.x.processplatform.core.entity.element.Application");
		dependency.containerEntities.add("com.x.processplatform.core.entity.element.ApplicationDict");
		dependency.containerEntities.add("com.x.processplatform.core.entity.element.ApplicationDictItem");
		dependency.containerEntities.add("com.x.processplatform.core.entity.element.Begin");
		dependency.containerEntities.add("com.x.processplatform.core.entity.element.Cancel");
		dependency.containerEntities.add("com.x.processplatform.core.entity.element.Choice");
		dependency.containerEntities.add("com.x.processplatform.core.entity.element.Delay");
		dependency.containerEntities.add("com.x.processplatform.core.entity.element.Embed");
		dependency.containerEntities.add("com.x.processplatform.core.entity.element.End");
		dependency.containerEntities.add("com.x.processplatform.core.entity.element.File");
		dependency.containerEntities.add("com.x.processplatform.core.entity.element.Form");
		dependency.containerEntities.add("com.x.processplatform.core.entity.element.FormField");
		dependency.containerEntities.add("com.x.processplatform.core.entity.element.Invoke");
		dependency.containerEntities.add("com.x.processplatform.core.entity.element.Manual");
		dependency.containerEntities.add("com.x.processplatform.core.entity.element.Merge");
		dependency.containerEntities.add("com.x.processplatform.core.entity.element.Message");
		dependency.containerEntities.add("com.x.processplatform.core.entity.element.Parallel");
		dependency.containerEntities.add("com.x.processplatform.core.entity.element.Process");
		dependency.containerEntities.add("com.x.processplatform.core.entity.element.Route");
		dependency.containerEntities.add("com.x.processplatform.core.entity.element.Script");
		dependency.containerEntities.add("com.x.processplatform.core.entity.element.Service");
		dependency.containerEntities.add("com.x.processplatform.core.entity.element.Split");
		dependency.containerEntities.add("com.x.processplatform.core.entity.element.QueryView");
		dependency.containerEntities.add("com.x.processplatform.core.entity.element.QueryStat");
		dependency.containerEntities.add("com.x.query.core.entity.Item");
		dependency.storageTypes.add(StorageType.processPlatform.toString());
		dependency.storeJars.add(x_organization_core_entity.class.getSimpleName());
		dependency.storeJars.add(x_organization_core_express.class.getSimpleName());
		dependency.storeJars.add(x_processplatform_core_entity.class.getSimpleName());
		dependency.storeJars.add(x_query_core_entity.class.getSimpleName());
	}

//	public static final String name = "流程";
//	public static List<String> containerEntities = new ArrayList<>();
//	public static List<StorageType> usedStorageTypes = new ArrayList<>();
//	public static List<Class<? extends Compilable>> dependents = new ArrayList<>();
//
//	static {
//		containerEntities.add("com.x.processplatform.core.entity.content.Attachment");
//		containerEntities.add("com.x.processplatform.core.entity.content.Read");
//		containerEntities.add("com.x.processplatform.core.entity.content.ReadCompleted");
//		containerEntities.add("com.x.processplatform.core.entity.content.Review");
//		containerEntities.add("com.x.processplatform.core.entity.content.Hint");
//		containerEntities.add("com.x.processplatform.core.entity.content.SerialNumber");
//		containerEntities.add("com.x.processplatform.core.entity.content.Task");
//		containerEntities.add("com.x.processplatform.core.entity.content.TaskCompleted");
//		containerEntities.add("com.x.processplatform.core.entity.content.Work");
//		containerEntities.add("com.x.processplatform.core.entity.content.WorkCompleted");
//		containerEntities.add("com.x.processplatform.core.entity.content.WorkLog");
//		containerEntities.add("com.x.processplatform.core.entity.element.Agent");
//		containerEntities.add("com.x.processplatform.core.entity.element.Application");
//		containerEntities.add("com.x.processplatform.core.entity.element.ApplicationDict");
//		containerEntities.add("com.x.processplatform.core.entity.element.ApplicationDictItem");
//		// containerEntities.add("com.x.processplatform.core.entity.element.ApplicationDictLobItem");
//		containerEntities.add("com.x.processplatform.core.entity.element.Begin");
//		containerEntities.add("com.x.processplatform.core.entity.element.Cancel");
//		containerEntities.add("com.x.processplatform.core.entity.element.Choice");
//		containerEntities.add("com.x.processplatform.core.entity.element.Delay");
//		containerEntities.add("com.x.processplatform.core.entity.element.Embed");
//		containerEntities.add("com.x.processplatform.core.entity.element.End");
//		containerEntities.add("com.x.processplatform.core.entity.element.File");
//		containerEntities.add("com.x.processplatform.core.entity.element.Form");
//		containerEntities.add("com.x.processplatform.core.entity.element.FormField");
//		containerEntities.add("com.x.processplatform.core.entity.element.Invoke");
//		containerEntities.add("com.x.processplatform.core.entity.element.Manual");
//		containerEntities.add("com.x.processplatform.core.entity.element.Merge");
//		containerEntities.add("com.x.processplatform.core.entity.element.Message");
//		containerEntities.add("com.x.processplatform.core.entity.element.Parallel");
//		containerEntities.add("com.x.processplatform.core.entity.element.Process");
//		containerEntities.add("com.x.processplatform.core.entity.element.Route");
//		containerEntities.add("com.x.processplatform.core.entity.element.Script");
//		containerEntities.add("com.x.processplatform.core.entity.element.Service");
//		containerEntities.add("com.x.processplatform.core.entity.element.Split");
//		containerEntities.add("com.x.processplatform.core.entity.element.QueryView");
//		containerEntities.add("com.x.processplatform.core.entity.element.QueryStat");
//		containerEntities.add("com.x.query.core.entity.Item");
//		usedStorageTypes.add(StorageType.processPlatform);
//		dependents.add(x_base_core_project.class);
//		dependents.add(x_organization_core_entity.class);
//		dependents.add(x_organization_core_express.class);
//		dependents.add(x_processplatform_core_entity.class);
//		dependents.add(x_query_core_entity.class);
//	}

}
