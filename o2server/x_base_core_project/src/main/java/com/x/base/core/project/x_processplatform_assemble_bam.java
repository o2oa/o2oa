package com.x.base.core.project;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.entity.StorageType;
import com.x.base.core.project.gson.XGsonBuilder;

public class x_processplatform_assemble_bam extends AssembleA {

	public static final String name = "流程监控";
	public static List<String> containerEntities = new ArrayList<>();
	public static List<StorageType> usedStorageTypes = new ArrayList<>();
	public static List<Class<? extends Compilable>> dependents = new ArrayList<>();

	static {
		containerEntities.add("com.x.processplatform.core.entity.content.Attachment");
		containerEntities.add("com.x.processplatform.core.entity.content.Read");
		containerEntities.add("com.x.processplatform.core.entity.content.ReadCompleted");
		containerEntities.add("com.x.processplatform.core.entity.content.Review");
		containerEntities.add("com.x.processplatform.core.entity.content.SerialNumber");
		containerEntities.add("com.x.processplatform.core.entity.content.Task");
		containerEntities.add("com.x.processplatform.core.entity.content.TaskCompleted");
		containerEntities.add("com.x.processplatform.core.entity.content.Work");
		containerEntities.add("com.x.processplatform.core.entity.content.WorkCompleted");
		containerEntities.add("com.x.processplatform.core.entity.content.WorkLog");
		containerEntities.add("com.x.processplatform.core.entity.element.Agent");
		containerEntities.add("com.x.processplatform.core.entity.element.Application");
		containerEntities.add("com.x.processplatform.core.entity.element.ApplicationDict");
		containerEntities.add("com.x.processplatform.core.entity.element.ApplicationDictItem");
		// containerEntities.add("com.x.processplatform.core.entity.element.ApplicationDictLobItem");
		containerEntities.add("com.x.processplatform.core.entity.element.Begin");
		containerEntities.add("com.x.processplatform.core.entity.element.Cancel");
		containerEntities.add("com.x.processplatform.core.entity.element.Choice");
		containerEntities.add("com.x.processplatform.core.entity.element.Delay");
		containerEntities.add("com.x.processplatform.core.entity.element.Embed");
		containerEntities.add("com.x.processplatform.core.entity.element.End");
		containerEntities.add("com.x.processplatform.core.entity.element.Form");
		containerEntities.add("com.x.processplatform.core.entity.element.Invoke");
		containerEntities.add("com.x.processplatform.core.entity.element.Manual");
		containerEntities.add("com.x.processplatform.core.entity.element.Merge");
		containerEntities.add("com.x.processplatform.core.entity.element.Message");
		containerEntities.add("com.x.processplatform.core.entity.element.Parallel");
		containerEntities.add("com.x.processplatform.core.entity.element.Process");
		containerEntities.add("com.x.processplatform.core.entity.element.Route");
		containerEntities.add("com.x.processplatform.core.entity.element.Script");
		containerEntities.add("com.x.processplatform.core.entity.element.Service");
		containerEntities.add("com.x.processplatform.core.entity.element.Split");
		containerEntities.add("com.x.processplatform.core.entity.element.QueryView");
		containerEntities.add("com.x.query.core.entity.Item");
		dependents.add(x_base_core_project.class);
		dependents.add(x_organization_core_entity.class);
		dependents.add(x_organization_core_express.class);
		dependents.add(x_processplatform_core_entity.class);
		dependents.add(x_query_core_entity.class);
	}

	protected void custom(File lib, String xLib) throws Exception {
	}

	public static void main(String[] args) {
		try {
			String str = args[0];
			str = StringUtils.replace(str, "\\", "/");
			Argument arg = XGsonBuilder.instance().fromJson(str, Argument.class);
			x_processplatform_assemble_bam o = new x_processplatform_assemble_bam();
			o.pack(arg.getDistPath(), arg.getRepositoryPath());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
