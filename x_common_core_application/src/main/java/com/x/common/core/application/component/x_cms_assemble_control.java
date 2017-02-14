package com.x.common.core.application.component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.gson.XGsonBuilder;

public class x_cms_assemble_control extends Assemble {

	public static List<String> containerEntities = new ArrayList<>();

	static {
		containerEntities.add("com.x.cms.core.entity.element.ViewFieldConfig");
		containerEntities.add("com.x.cms.core.entity.element.ViewCatagory");
		containerEntities.add("com.x.cms.core.entity.element.View");
		containerEntities.add("com.x.cms.core.entity.element.Form");
		containerEntities.add("com.x.cms.core.entity.element.AppDict");
		containerEntities.add("com.x.cms.core.entity.element.AppDictItem");
		containerEntities.add("com.x.cms.core.entity.element.Script");
		containerEntities.add("com.x.cms.core.entity.AppCatagoryAdmin");
		containerEntities.add("com.x.cms.core.entity.AppCatagoryPermission");
		containerEntities.add("com.x.cms.core.entity.AppInfo");
		containerEntities.add("com.x.cms.core.entity.CatagoryInfo");
		containerEntities.add("com.x.cms.core.entity.DataItem");
		containerEntities.add("com.x.cms.core.entity.Document");
		containerEntities.add("com.x.cms.core.entity.FileInfo");
		containerEntities.add("com.x.cms.core.entity.Log");
	}

	protected void custom(File dir, String repositoryPath) throws Exception {
		File repository = new File(repositoryPath);
		File lib = new File(dir, "WEB-INF/lib");
		FileUtils.copyDirectory(repository, lib, new WildcardFileFilter("x_organization_core_entity*.jar"));
		FileUtils.copyDirectory(repository, lib, new WildcardFileFilter("x_organization_core_express*.jar"));
		FileUtils.copyDirectory(repository, lib, new WildcardFileFilter("x_cms_core_entity*.jar"));
	}

	public static void main(String[] args) {
		try {
			String str = args[0];
			str = StringUtils.replace(str, "\\", "/");
			Argument arg = XGsonBuilder.instance().fromJson(str, Argument.class);
			x_cms_assemble_control o = new x_cms_assemble_control();
			o.pack(arg.getDistPath(), arg.getRepositoryPath(), arg.getCenterHost(), arg.getCenterPort(),
					arg.getCenterCipher(), arg.getConfigApplicationServer());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
