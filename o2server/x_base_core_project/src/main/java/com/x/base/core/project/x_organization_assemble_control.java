package com.x.base.core.project;

import com.x.base.core.project.annotation.Module;
import com.x.base.core.project.annotation.ModuleCategory;
import com.x.base.core.project.annotation.ModuleType;

@Module(type = ModuleType.ASSEMBLE, category = ModuleCategory.OFFICIAL, name = "组织管理")
public class x_organization_assemble_control extends AssembleA {
	
	public x_organization_assemble_control() {
		super();
		dependency.containerEntities.add("com.x.organization.core.entity.Group");
		dependency.containerEntities.add("com.x.organization.core.entity.Custom");
		dependency.containerEntities.add("com.x.organization.core.entity.Role");
		dependency.containerEntities.add("com.x.organization.core.entity.Person");
		dependency.containerEntities.add("com.x.organization.core.entity.Identity");
		dependency.containerEntities.add("com.x.organization.core.entity.PersonAttribute");
		dependency.containerEntities.add("com.x.organization.core.entity.Unit");
		dependency.containerEntities.add("com.x.organization.core.entity.UnitAttribute");
		dependency.containerEntities.add("com.x.organization.core.entity.UnitDuty");
		dependency.storeJars.add(x_organization_core_entity.class.getSimpleName());
	}
}
