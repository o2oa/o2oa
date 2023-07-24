package com.x.base.core.project;

import com.x.base.core.project.annotation.Module;
import com.x.base.core.project.annotation.ModuleCategory;
import com.x.base.core.project.annotation.ModuleType;

@Module(type = ModuleType.EXPRESS, category = ModuleCategory.OFFICIAL, name = "CMS权限查询管理接口", packageName = "com.x.cms.core.express", containerEntities = {
		"com.x.cms.core.entity.AppInfo", "com.x.cms.core.entity.CategoryInfo", 
		"com.x.cms.core.entity.Document", "com.x.cms.core.entity.Review"}, storeJars = { "x_cms_core_entity"})
public class x_cms_core_express extends Compilable {

}
