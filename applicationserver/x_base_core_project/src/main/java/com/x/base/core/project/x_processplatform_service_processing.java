package com.x.base.core.project;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.StorageType;

public class x_processplatform_service_processing extends ServiceA {

	public static final String name = "流程驱动";
	public static List<String> containerEntities = new ArrayList<>();
	public static List<StorageType> usedStorageTypes = new ArrayList<>();
	public static List<Class<? extends Compilable>> dependents = new ArrayList<>();

	static {
		containerEntities.add("com.x.processplatform.core.entity.content.Attachment");
		containerEntities.add("com.x.processplatform.core.entity.content.TaskCompleted");
		containerEntities.add("com.x.processplatform.core.entity.content.ReadCompleted");
		containerEntities.add("com.x.processplatform.core.entity.content.Review");
		containerEntities.add("com.x.processplatform.core.entity.content.Hint");
		containerEntities.add("com.x.processplatform.core.entity.content.WorkCompleted");
		containerEntities.add("com.x.processplatform.core.entity.content.WorkLog");
		containerEntities.add("com.x.processplatform.core.entity.content.Task");
		containerEntities.add("com.x.processplatform.core.entity.content.Work");
		containerEntities.add("com.x.processplatform.core.entity.content.Read");
		containerEntities.add("com.x.processplatform.core.entity.content.SerialNumber");
		containerEntities.add("com.x.processplatform.core.entity.element.End");
		containerEntities.add("com.x.processplatform.core.entity.element.Application");
		containerEntities.add("com.x.processplatform.core.entity.element.ApplicationDict");
		containerEntities.add("com.x.processplatform.core.entity.element.ApplicationDictItem");
		// containerEntities.add("com.x.processplatform.core.entity.element.ApplicationDictLobItem");
		containerEntities.add("com.x.processplatform.core.entity.element.Script");
		containerEntities.add("com.x.processplatform.core.entity.element.Cancel");
		containerEntities.add("com.x.processplatform.core.entity.element.Merge");
		containerEntities.add("com.x.processplatform.core.entity.element.Route");
		containerEntities.add("com.x.processplatform.core.entity.element.Choice");
		containerEntities.add("com.x.processplatform.core.entity.element.Invoke");
		containerEntities.add("com.x.processplatform.core.entity.element.Manual");
		containerEntities.add("com.x.processplatform.core.entity.element.Parallel");
		containerEntities.add("com.x.processplatform.core.entity.element.Begin");
		containerEntities.add("com.x.processplatform.core.entity.element.Split");
		containerEntities.add("com.x.processplatform.core.entity.element.Message");
		containerEntities.add("com.x.processplatform.core.entity.element.Process");
		containerEntities.add("com.x.processplatform.core.entity.element.Service");
		containerEntities.add("com.x.processplatform.core.entity.element.Agent");
		containerEntities.add("com.x.processplatform.core.entity.element.Delay");
		containerEntities.add("com.x.processplatform.core.entity.element.File");
		containerEntities.add("com.x.processplatform.core.entity.element.Form");
		containerEntities.add("com.x.processplatform.core.entity.element.FormField");
		containerEntities.add("com.x.processplatform.core.entity.element.Embed");
		containerEntities.add("com.x.processplatform.core.entity.log.ProcessingError");
		containerEntities.add("com.x.query.core.entity.Item");
		usedStorageTypes.add(StorageType.processPlatform);
		dependents.add(x_base_core_project.class);
		dependents.add(x_organization_core_entity.class);
		dependents.add(x_organization_core_express.class);
		dependents.add(x_processplatform_core_entity.class);
		dependents.add(x_processplatform_core_serial.class);
		dependents.add(x_collaboration_core_entity.class);
		dependents.add(x_collaboration_core_message.class);
		dependents.add(x_query_core_entity.class);
	}

	protected void custom(File lib, String xLib) throws Exception {
	}

}
