package com.x.base.core.project;

import com.x.base.core.project.annotation.Module;
import com.x.base.core.project.annotation.ModuleCategory;
import com.x.base.core.project.annotation.ModuleType;

@Module(type = ModuleType.ASSEMBLE, category = ModuleCategory.OFFICIAL, name = "组织管理接口服务")
public class x_organization_assemble_express extends AssembleA {

	public x_organization_assemble_express() {
		super();
		dependency.containerEntities.add("com.x.organization.core.entity.Group");
		dependency.containerEntities.add("com.x.organization.core.entity.Role");
		dependency.containerEntities.add("com.x.organization.core.entity.Person");
		dependency.containerEntities.add("com.x.organization.core.entity.PersonAttribute");
		dependency.containerEntities.add("com.x.organization.core.entity.Identity");
		dependency.containerEntities.add("com.x.organization.core.entity.Unit");
		dependency.containerEntities.add("com.x.organization.core.entity.UnitAttribute");
		dependency.containerEntities.add("com.x.organization.core.entity.UnitDuty");
		dependency.storeJars.add(x_organization_core_entity.class.getSimpleName());
	}

//	public static final String name = "组织管理查询";
//	public static List<String> containerEntities = new ArrayList<>();
//	public static List<StorageType> usedStorageTypes = new ArrayList<>();
//	public static List<Class<? extends Compilable>> dependents = new ArrayList<>();
//
//	static {
//		containerEntities.add("com.x.organization.core.entity.Group");
//		containerEntities.add("com.x.organization.core.entity.Role");
//		containerEntities.add("com.x.organization.core.entity.Person");
//		containerEntities.add("com.x.organization.core.entity.PersonAttribute");
//		containerEntities.add("com.x.organization.core.entity.Identity");
//		containerEntities.add("com.x.organization.core.entity.Unit");
//		containerEntities.add("com.x.organization.core.entity.UnitAttribute");
//		containerEntities.add("com.x.organization.core.entity.UnitDuty");
//		dependents.add(x_base_core_project.class);
//		dependents.add(x_organization_core_entity.class);
//	}

}
