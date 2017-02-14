package com.x.common.core.application.component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.gson.XGsonBuilder;

public class x_processplatform_service_processing extends Service {

	public static List<String> containerEntities = new ArrayList<>();

	static {
		containerEntities.add("com.x.processplatform.core.entity.content.DataItem");
		containerEntities.add("com.x.processplatform.core.entity.content.Attachment");
		containerEntities.add("com.x.processplatform.core.entity.content.TaskCompleted");
		containerEntities.add("com.x.processplatform.core.entity.content.ReadCompleted");
		containerEntities.add("com.x.processplatform.core.entity.content.Review");
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
		containerEntities.add("com.x.processplatform.core.entity.element.Condition");
		containerEntities.add("com.x.processplatform.core.entity.element.Message");
		containerEntities.add("com.x.processplatform.core.entity.element.Process");
		containerEntities.add("com.x.processplatform.core.entity.element.Service");
		containerEntities.add("com.x.processplatform.core.entity.element.Agent");
		containerEntities.add("com.x.processplatform.core.entity.element.Delay");
		containerEntities.add("com.x.processplatform.core.entity.element.Form");
		containerEntities.add("com.x.processplatform.core.entity.element.Embed");
		containerEntities.add("com.x.processplatform.core.entity.log.ProcessingError");
	}

	protected void custom(File dir, String repositoryPath) throws Exception {
		File repository = new File(repositoryPath);
		File lib = new File(dir, "WEB-INF/lib");
		FileUtils.copyDirectory(repository, lib, new WildcardFileFilter("x_organization_core_entity*.jar"));
		FileUtils.copyDirectory(repository, lib, new WildcardFileFilter("x_organization_core_express*.jar"));
		FileUtils.copyDirectory(repository, lib, new WildcardFileFilter("x_processplatform_assemble_surface*.jar"));
		FileUtils.copyDirectory(repository, lib, new WildcardFileFilter("x_processplatform_core_entity*.jar"));
		FileUtils.copyDirectory(repository, lib, new WildcardFileFilter("x_processplatform_core_serial*.jar"));
		FileUtils.copyDirectory(repository, lib, new WildcardFileFilter("x_processplatform_core_lookup*.jar"));
		FileUtils.copyDirectory(repository, lib, new WildcardFileFilter("x_collaboration_core_entity*.jar"));
		FileUtils.copyDirectory(repository, lib, new WildcardFileFilter("x_collaboration_core_message*.jar"));
	}

	public static void main(String[] args) {
		try {
			String str = args[0];
			str = StringUtils.replace(str, "\\", "/");
			Argument arg = XGsonBuilder.instance().fromJson(str, Argument.class);
			x_processplatform_service_processing o = new x_processplatform_service_processing();
			o.pack(arg.getDistPath(), arg.getRepositoryPath(), arg.getCenterHost(), arg.getCenterPort(),
					 arg.getCenterCipher(),  arg.getConfigApplicationServer());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
