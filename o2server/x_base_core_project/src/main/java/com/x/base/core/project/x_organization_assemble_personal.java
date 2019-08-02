package com.x.base.core.project;

import com.x.base.core.project.annotation.Module;
import com.x.base.core.project.annotation.ModuleCategory;
import com.x.base.core.project.annotation.ModuleType;

@Module(type = ModuleType.ASSEMBLE, category = ModuleCategory.OFFICIAL, name = "组织管理个人", packageName = "com.x.organization.assemble.personal", containerEntities = {
		"com.x.organization.core.entity.Group", "com.x.organization.core.entity.Role",
		"com.x.organization.core.entity.Person", "com.x.organization.core.entity.PersonAttribute",
		"com.x.organization.core.entity.Identity", "com.x.organization.core.entity.Unit",
		"com.x.organization.core.entity.UnitAttribute", "com.x.organization.core.entity.UnitDuty",
		"com.x.organization.core.entity.Custom", "com.x.organization.core.entity.Definition",
		"com.x.organization.core.entity.accredit.Empower",
		"com.x.organization.core.entity.accredit.EmpowerLog" }, storeJars = { "x_organization_core_entity",
				"x_organization_core_express" })
public class x_organization_assemble_personal extends Deployable {

//	public x_organization_assemble_personal() {
//		super();
//		dependency.containerEntities.add("com.x.organization.core.entity.Group");
//		dependency.containerEntities.add("com.x.organization.core.entity.Role");
//		dependency.containerEntities.add("com.x.organization.core.entity.Person");
//		dependency.containerEntities.add("com.x.organization.core.entity.PersonAttribute");
//		dependency.containerEntities.add("com.x.organization.core.entity.Identity");
//		dependency.containerEntities.add("com.x.organization.core.entity.Unit");
//		dependency.containerEntities.add("com.x.organization.core.entity.UnitAttribute");
//		dependency.containerEntities.add("com.x.organization.core.entity.UnitDuty");
//		dependency.containerEntities.add("com.x.organization.core.entity.Custom");
//		dependency.containerEntities.add("com.x.organization.core.entity.Definition");
//		dependency.containerEntities.add("com.x.organization.core.entity.accredit.Trust");
//		dependency.containerEntities.add("com.x.organization.core.entity.accredit.TrustLog");
//		dependency.storeJars.add(x_organization_core_entity.class.getSimpleName());
//		dependency.storeJars.add(x_organization_core_express.class.getSimpleName());
//	}
}
