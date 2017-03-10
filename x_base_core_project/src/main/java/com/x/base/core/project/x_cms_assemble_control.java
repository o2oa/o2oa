package com.x.base.core.project;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.entity.StorageType;
import com.x.base.core.gson.XGsonBuilder;

public class x_cms_assemble_control extends Assemble {

	public static List<String> containerEntities = new ArrayList<>();
	public static List<StorageType> usedStorageTypes = new ArrayList<>();
	public static List<Class<? extends Compilable>> dependents = new ArrayList<>();

	static {
		containerEntities.add("com.x.cms.core.entity.content.DataItem");
		containerEntities.add("com.x.cms.core.entity.content.DataLobItem");
		
		containerEntities.add("com.x.cms.core.entity.element.AppDict");
		containerEntities.add("com.x.cms.core.entity.element.AppDictItem");
		containerEntities.add("com.x.cms.core.entity.element.AppDictLobItem");
		containerEntities.add("com.x.cms.core.entity.element.Form");
		containerEntities.add("com.x.cms.core.entity.element.QueryView");
		containerEntities.add("com.x.cms.core.entity.element.Script");
		containerEntities.add("com.x.cms.core.entity.element.TemplateForm");
		containerEntities.add("com.x.cms.core.entity.element.View");
		containerEntities.add("com.x.cms.core.entity.element.ViewCategory");
		containerEntities.add("com.x.cms.core.entity.element.ViewFieldConfig");
		
		containerEntities.add("com.x.cms.core.entity.AppCategoryAdmin");
		containerEntities.add("com.x.cms.core.entity.AppCategoryPermission");
		containerEntities.add("com.x.cms.core.entity.AppInfo");
		containerEntities.add("com.x.cms.core.entity.CategoryInfo");
		containerEntities.add("com.x.cms.core.entity.Document");
		containerEntities.add("com.x.cms.core.entity.DocumentPermission"); 
		containerEntities.add("com.x.cms.core.entity.DocumentPictureInfo");
		containerEntities.add("com.x.cms.core.entity.DocumentViewRecord");
		containerEntities.add("com.x.cms.core.entity.FileInfo");
		containerEntities.add("com.x.cms.core.entity.Log");		
		
		dependents.add(x_base_core_project.class);
		dependents.add(x_organization_core_entity.class);
		dependents.add(x_organization_core_express.class);
		dependents.add(x_cms_core_entity.class);
	}

	protected void custom(File lib, String xLib) throws Exception {
		// File xLibDir = new File(xLib);
		// File libDir = new File(lib, "WEB-INF/lib");
		// for (Class<? extends Compilable> clz : dependents) {
		// FileUtils.copyDirectory(xLibDir, libDir, new
		// NameFileFilter(clz.getSimpleName() + "-" + VERSION + ".jar"));
		// }
	}

	public static void main(String[] args) {
		try {
			String str = args[0];
			str = StringUtils.replace(str, "\\", "/");
			Argument arg = XGsonBuilder.instance().fromJson(str, Argument.class);
			x_cms_assemble_control o = new x_cms_assemble_control();
			o.pack(arg.getDistPath(), arg.getRepositoryPath());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
