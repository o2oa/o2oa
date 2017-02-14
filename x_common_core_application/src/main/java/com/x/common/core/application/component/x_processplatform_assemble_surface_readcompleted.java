package com.x.common.core.application.component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.gson.XGsonBuilder;

public class x_processplatform_assemble_surface_readcompleted extends Assemble {

	public static List<String> containerEntities = new ArrayList<>();

	static {
		containerEntities.add("com.x.processplatform.core.entity.content.Work");
		containerEntities.add("com.x.processplatform.core.entity.content.ReadCompleted");
		containerEntities.add("com.x.processplatform.core.entity.content.WorkCompleted");
		containerEntities.add("com.x.processplatform.core.entity.content.WorkLog");
		containerEntities.add("com.x.processplatform.core.entity.content.Task");
		containerEntities.add("com.x.processplatform.core.entity.content.TaskCompleted");
		containerEntities.add("com.x.processplatform.core.entity.element.Application");
	}

	protected void custom(File dir, String repositoryPath) throws Exception {
		File repository = new File(repositoryPath);
		File lib = new File(dir, "WEB-INF/lib");
		FileUtils.copyDirectory(repository, lib, new WildcardFileFilter("x_organization_core_entity*.jar"));
		FileUtils.copyDirectory(repository, lib, new WildcardFileFilter("x_organization_core_express*.jar"));
		FileUtils.copyDirectory(repository, lib, new WildcardFileFilter("x_processplatform_core_surface*.jar"));
		FileUtils.copyDirectory(repository, lib, new WildcardFileFilter("x_processplatform_core_entity*.jar"));
	}

	public static void main(String[] args) {
		try {
			String str = args[0];
			str = StringUtils.replace(str, "\\", "/");
			Argument arg = XGsonBuilder.instance().fromJson(str, Argument.class);
			x_processplatform_assemble_surface_readcompleted o = new x_processplatform_assemble_surface_readcompleted();
			o.pack(arg.getDistPath(), arg.getRepositoryPath(), arg.getCenterHost(), arg.getCenterPort(),
					arg.getCenterCipher(), arg.getConfigApplicationServer());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
