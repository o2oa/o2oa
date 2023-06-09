package com.x.base.core.project;

import com.x.base.core.entity.StorageType;
import com.x.base.core.project.annotation.Module;
import com.x.base.core.project.annotation.ModuleCategory;
import com.x.base.core.project.annotation.ModuleType;

@Module(type = ModuleType.ASSEMBLE, category = ModuleCategory.OFFICIAL, name = "企业网盘", packageName = "com.x.file.assemble.control", containerEntities = {
		"com.x.file.core.entity.personal.Folder", "com.x.file.core.entity.personal.Folder2",
		"com.x.file.core.entity.personal.Attachment", "com.x.file.core.entity.personal.Attachment2",
		"com.x.file.core.entity.personal.Share", "com.x.file.core.entity.open.File",
		"com.x.file.core.entity.open.OriginFile", "com.x.file.core.entity.personal.Recycle",
		"com.x.file.core.entity.open.FileConfig",
		"com.x.cms.core.entity.Document" }, storageTypes = { StorageType.file }, storeJars = { "x_file_core_entity",
				"x_organization_core_express", "x_organization_core_entity", "x_cms_core_entity" })
public class x_file_assemble_control extends Deployable {
}
