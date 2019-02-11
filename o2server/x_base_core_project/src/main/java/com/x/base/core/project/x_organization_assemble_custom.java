package com.x.base.core.project;

import com.x.base.core.project.annotation.Module;
import com.x.base.core.project.annotation.ModuleCategory;
import com.x.base.core.project.annotation.ModuleType;

@Module(type = ModuleType.ASSEMBLE, category = ModuleCategory.OFFICIAL, name = "布局")
public class x_organization_assemble_custom extends AssembleA {

	public x_organization_assemble_custom() {
		super();
		dependency.containerEntities.add("com.x.organization.core.entity.Custom");
		dependency.containerEntities.add("com.x.organization.core.entity.Person");
		dependency.containerEntities.add("com.x.organization.core.entity.Definition");
		dependency.storeJars.add(x_organization_core_entity.class.getSimpleName());
	}

//	public static final String name = "布局";
//	public static List<String> containerEntities = new ArrayList<>();
//	public static List<StorageType> usedStorageTypes = new ArrayList<>();
//	public static List<Class<? extends Compilable>> dependents = new ArrayList<>();
//
//	static {
//		containerEntities.add("com.x.organization.core.entity.Custom");
//		containerEntities.add("com.x.organization.core.entity.Person");
//		containerEntities.add("com.x.organization.core.entity.Definition");
//		dependents.add(x_base_core_project.class);
//		dependents.add(x_organization_core_entity.class);
//	}

}