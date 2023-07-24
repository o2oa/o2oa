package com.x.bbs.assemble.control.jaxrs.configsetting;

import com.x.base.core.project.cache.Cache;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.bbs.assemble.control.service.BBSConfigSettingService;
import com.x.bbs.assemble.control.service.UserManagerService;
import com.x.bbs.entity.BBSConfigSetting;


public class BaseAction extends StandardJaxrsAction{
	protected Cache.CacheCategory cacheCategory = new Cache.CacheCategory(BBSConfigSetting.class);
	
	protected UserManagerService userManagerService = new UserManagerService();
	protected BBSConfigSettingService configSettingService = new BBSConfigSettingService();
	
}
