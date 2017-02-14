package com.x.common.core.application.component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.gson.XGsonBuilder;

public class x_component_assemble_control extends Assemble {

	public static List<String> containerEntities = new ArrayList<>();

	static {
		containerEntities.add("com.x.component.core.entity.Component");
	}

	protected void custom(File dir, String repositoryPath) throws Exception {
		File repository = new File(repositoryPath);
		File lib = new File(dir, "WEB-INF/lib");
		FileUtils.copyDirectory(repository, lib, new WildcardFileFilter("x_component_core_*.jar"));
	}

	public static void main(String[] args) {
		try {
			String str = args[0];
			str = StringUtils.replace(str, "\\", "/");
			Argument arg = XGsonBuilder.instance().fromJson(str, Argument.class);
			x_component_assemble_control o = new x_component_assemble_control();
			o.pack(arg.getDistPath(), arg.getRepositoryPath(), arg.getCenterHost(), arg.getCenterPort(),
					arg.getCenterCipher(), arg.getConfigApplicationServer());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
