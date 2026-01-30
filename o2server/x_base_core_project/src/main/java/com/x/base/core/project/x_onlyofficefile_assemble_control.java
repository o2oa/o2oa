package com.x.base.core.project;

import com.x.base.core.entity.StorageType;
import com.x.base.core.project.annotation.Module;
import com.x.base.core.project.annotation.ModuleCategory;
import com.x.base.core.project.annotation.ModuleType;

@Module(type = ModuleType.ASSEMBLE, category = ModuleCategory.OFFICIAL, name = "onlyOffice在线文档管理", packageName = "com.x.onlyofficefile.assemble.control", containerEntities = {
		"com.x.onlyofficefile.core.entity.OnlyOfficeFile", "com.x.onlyofficefile.core.entity.OnlyOfficeFileVersion",
		"com.x.processplatform.core.entity.content.Attachment","com.x.cms.core.entity.FileInfo"},
     storageTypes = {StorageType.custom,StorageType.cms, StorageType.processPlatform} ,
		storeJars = {"x_organization_core_entity", "x_organization_core_express",
		"x_processplatform_core_entity", "x_cms_core_entity", "x_onlyofficefile_core_entity"})
public class x_onlyofficefile_assemble_control extends Deployable {

}
