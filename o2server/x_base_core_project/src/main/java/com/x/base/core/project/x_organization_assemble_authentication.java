package com.x.base.core.project;

import com.x.base.core.project.annotation.Module;
import com.x.base.core.project.annotation.ModuleCategory;
import com.x.base.core.project.annotation.ModuleType;

@Module(type = ModuleType.ASSEMBLE, category = ModuleCategory.OFFICIAL, name = "组织管理认证")
public class x_organization_assemble_authentication extends AssembleA {

	public x_organization_assemble_authentication() {
		super();
		dependency.containerEntities.add("com.x.organization.core.entity.Person");
		dependency.containerEntities.add("com.x.organization.core.entity.Identity");
		dependency.containerEntities.add("com.x.organization.core.entity.Role");
		dependency.containerEntities.add("com.x.organization.core.entity.Bind");
		dependency.containerEntities.add("com.x.organization.core.entity.OauthCode");
		dependency.storeJars.add(x_organization_core_entity.class.getSimpleName());
		dependency.storeJars.add(x_organization_core_express.class.getSimpleName());
	}
}
