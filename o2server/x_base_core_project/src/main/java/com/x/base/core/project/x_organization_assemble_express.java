package com.x.base.core.project;

import com.x.base.core.project.annotation.Module;
import com.x.base.core.project.annotation.ModuleCategory;
import com.x.base.core.project.annotation.ModuleType;

@Module(type = ModuleType.ASSEMBLE, category = ModuleCategory.OFFICIAL, name = "组织管理接口服务", packageName = "com.x.organization.assemble.express", containerEntities = {
		"com.x.organization.core.entity.Group", "com.x.organization.core.entity.Role",
		"com.x.organization.core.entity.Person", "com.x.organization.core.entity.PersonAttribute",
		"com.x.organization.core.entity.Identity", "com.x.organization.core.entity.Unit",
		"com.x.organization.core.entity.UnitAttribute", "com.x.organization.core.entity.UnitDuty",
		"com.x.organization.core.entity.accredit.Empower",
		"com.x.organization.core.entity.accredit.EmpowerLog" }, storeJars = { "x_organization_core_entity" })
public class x_organization_assemble_express extends Deployable {
}
