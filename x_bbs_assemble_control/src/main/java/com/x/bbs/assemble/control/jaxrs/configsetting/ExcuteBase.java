package com.x.bbs.assemble.control.jaxrs.configsetting;

import com.x.base.core.cache.ApplicationCache;
import com.x.bbs.assemble.control.service.BBSConfigSettingService;
import com.x.bbs.assemble.control.service.UserManagerService;
import com.x.bbs.entity.BBSConfigSetting;

import net.sf.ehcache.Ehcache;

public class ExcuteBase {
	
	protected Ehcache cache = ApplicationCache.instance().getCache( BBSConfigSetting.class);
	
	protected UserManagerService userManagerService = new UserManagerService();
	protected BBSConfigSettingService configSettingService = new BBSConfigSettingService();
	
}
