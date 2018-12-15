package com.x.base.core.project;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.StorageType;

public class x_organization_assemble_authentication extends AssembleA {

	public static final String name = "组织管理认证";
	public static List<String> containerEntities = new ArrayList<>();
	public static List<StorageType> usedStorageTypes = new ArrayList<>();
	public static List<Class<? extends Compilable>> dependents = new ArrayList<>();

	static {
		containerEntities.add("com.x.organization.core.entity.Person");
		containerEntities.add("com.x.organization.core.entity.Identity");
		containerEntities.add("com.x.organization.core.entity.Role");
		containerEntities.add("com.x.organization.core.entity.Bind");
		containerEntities.add("com.x.organization.core.entity.OauthCode");
		dependents.add(x_base_core_project.class);
		dependents.add(x_organization_core_entity.class);
		dependents.add(x_organization_core_express.class);
	}

	protected void custom(File lib, String xLib) throws Exception {
	}

}
