package com.x.cms.assemble.control.jaxrs.appinfo;

import com.x.base.core.cache.ApplicationCache;
import com.x.cms.assemble.control.service.AppCategoryAdminServiceAdv;
import com.x.cms.assemble.control.service.AppInfoServiceAdv;
import com.x.cms.assemble.control.service.CategoryInfoServiceAdv;
import com.x.cms.assemble.control.service.UserManagerService;
import com.x.cms.core.entity.AppInfo;

import net.sf.ehcache.Ehcache;

public class ExcuteBase {
	
	protected static Ehcache cache = ApplicationCache.instance().getCache( AppInfo.class);
	
	protected AppCategoryAdminServiceAdv appCategoryAdminServiceAdv = new AppCategoryAdminServiceAdv();
	protected AppInfoServiceAdv appInfoServiceAdv = new AppInfoServiceAdv();
	protected CategoryInfoServiceAdv categoryInfoServiceAdv = new CategoryInfoServiceAdv();
	protected UserManagerService userManagerService = new UserManagerService();
	
}
