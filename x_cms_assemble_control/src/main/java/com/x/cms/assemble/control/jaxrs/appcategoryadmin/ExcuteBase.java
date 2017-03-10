package com.x.cms.assemble.control.jaxrs.appcategoryadmin;

import com.x.base.core.cache.ApplicationCache;
import com.x.cms.assemble.control.service.AppCategoryAdminServiceAdv;
import com.x.cms.assemble.control.service.UserManagerService;
import com.x.cms.core.entity.AppCategoryAdmin;

import net.sf.ehcache.Ehcache;

public class ExcuteBase {
	
	protected static Ehcache cache = ApplicationCache.instance().getCache( AppCategoryAdmin.class);
	
	protected UserManagerService userManagerService = new UserManagerService();
	protected AppCategoryAdminServiceAdv appCategoryAdminServiceAdv = new AppCategoryAdminServiceAdv();
	
}
