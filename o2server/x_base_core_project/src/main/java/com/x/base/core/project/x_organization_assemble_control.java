package com.x.base.core.project;

import com.x.base.core.entity.StorageType;
import com.x.base.core.project.annotation.Module;
import com.x.base.core.project.annotation.ModuleCategory;
import com.x.base.core.project.annotation.ModuleType;

@Module(type = ModuleType.ASSEMBLE, category = ModuleCategory.OFFICIAL, name = "组织管理", packageName = "com.x.organization.assemble.control", containerEntities = {
		"com.x.organization.core.entity.Group", "com.x.organization.core.entity.Custom",
		"com.x.organization.core.entity.Role", "com.x.organization.core.entity.Person",
		"com.x.organization.core.entity.Identity", "com.x.organization.core.entity.PersonAttribute",
		"com.x.organization.core.entity.PersonExtend", "com.x.organization.core.entity.Unit",
		"com.x.organization.core.entity.UnitAttribute", "com.x.organization.core.entity.PersonCard",
		"com.x.organization.core.entity.PermissionSetting", "com.x.organization.core.entity.UnitDuty",
		"com.x.organization.core.entity.Custom", "com.x.general.core.entity.GeneralFile" }, storageTypes = {
				StorageType.general }, storeJars = { "x_organization_core_entity", "x_general_core_entity" })
public class x_organization_assemble_control extends Deployable {
}
