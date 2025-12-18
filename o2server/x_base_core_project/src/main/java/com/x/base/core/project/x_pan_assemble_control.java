package com.x.base.core.project;

import com.x.base.core.entity.StorageType;
import com.x.base.core.project.annotation.Module;
import com.x.base.core.project.annotation.ModuleCategory;
import com.x.base.core.project.annotation.ModuleType;

/**
 * web应用工程信息
 * name 应用工程业务简要描述
 * packageName web应用工程类包路径
 * containerEntities 业务需要访问的实体类（需全路径，多个类逗号隔开）
 * storeJars 需要访问平台的实体类工程
 * customJars 需要访问的自定义工程
 * @author sword
 */
@Module(type = ModuleType.ASSEMBLE, category = ModuleCategory.OFFICIAL, name = "新版企业网盘", packageName = "com.x.pan.assemble.control",
		containerEntities = {"com.x.file.core.entity.personal.Folder2","com.x.file.core.entity.personal.Attachment2",
				"com.x.file.core.entity.personal.Share","com.x.file.core.entity.open.OriginFile",
				"com.x.file.core.entity.personal.Recycle","com.x.file.core.entity.open.FileConfig",
				"com.x.pan.core.entity.Folder3","com.x.pan.core.entity.Attachment3",
				"com.x.pan.core.entity.Recycle3","com.x.pan.core.entity.ZonePermission",
				"com.x.pan.core.entity.FileConfig3","com.x.pan.core.entity.Favorite",
				"com.x.pan.core.entity.AttachmentVersion","com.x.pan.core.entity.LockInfo",
				"com.x.organization.core.entity.Identity",
				"com.x.organization.core.entity.Group", "com.x.organization.core.entity.Unit"},
		storeJars = { "x_organization_core_entity", "x_organization_core_express", "x_file_core_entity", "x_pan_core_entity" },
		storageTypes = { StorageType.file })
public class x_pan_assemble_control extends Deployable {
}
