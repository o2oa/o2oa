package com.x.base.core.project;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.entity.StorageType;
import com.x.base.core.gson.XGsonBuilder;

public class x_portal_assemble_designer extends Assemble {

	public static List<String> containerEntities = new ArrayList<>();
	public static List<StorageType> usedStorageTypes = new ArrayList<>();
	public static List<Class<? extends Compilable>> dependents = new ArrayList<>();

	static {
		containerEntities.add("com.x.portal.core.entity.Portal");
		containerEntities.add("com.x.portal.core.entity.Menu");
		containerEntities.add("com.x.portal.core.entity.Page");
		containerEntities.add("com.x.portal.core.entity.Source");
		containerEntities.add("com.x.portal.core.entity.Script");
		containerEntities.add("com.x.portal.core.entity.TemplatePage");
		dependents.add(x_base_core_project.class);
		dependents.add(x_organization_core_entity.class);
		dependents.add(x_organization_core_express.class);
		dependents.add(x_instrument_core_express.class);
		dependents.add(x_portal_core_entity.class);
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
			x_portal_assemble_designer o = new x_portal_assemble_designer();
			o.pack(arg.getDistPath(), arg.getRepositoryPath());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
