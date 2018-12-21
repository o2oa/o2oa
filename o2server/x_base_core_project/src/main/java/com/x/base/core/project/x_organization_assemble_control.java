package com.x.base.core.project;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.entity.StorageType;
import com.x.base.core.project.gson.XGsonBuilder;

public class x_organization_assemble_control extends AssembleA {

	public static final String name = "组织管理";
	public static List<String> containerEntities = new ArrayList<>();
	public static List<StorageType> usedStorageTypes = new ArrayList<>();
	public static List<Class<? extends Compilable>> dependents = new ArrayList<>();

	static {
		containerEntities.add("com.x.organization.core.entity.Group");
		containerEntities.add("com.x.organization.core.entity.Custom");
		containerEntities.add("com.x.organization.core.entity.Role");
		containerEntities.add("com.x.organization.core.entity.Person");
		containerEntities.add("com.x.organization.core.entity.Identity");
		containerEntities.add("com.x.organization.core.entity.PersonAttribute");
		containerEntities.add("com.x.organization.core.entity.Unit");
		containerEntities.add("com.x.organization.core.entity.UnitAttribute");
		containerEntities.add("com.x.organization.core.entity.UnitDuty");
		dependents.add(x_base_core_project.class);
		dependents.add(x_organization_core_entity.class);
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
			x_organization_assemble_control o = new x_organization_assemble_control();
			o.pack(arg.getDistPath(), arg.getRepositoryPath());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
