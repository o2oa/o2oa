package com.x.cms.assemble.control.jaxrs.appcategorypermission;

import com.x.base.core.cache.ApplicationCache;
import com.x.cms.assemble.control.service.AppCategoryPermissionServiceAdv;
import com.x.cms.assemble.control.service.AppInfoServiceAdv;
import com.x.cms.assemble.control.service.CategoryInfoServiceAdv;
import com.x.cms.assemble.control.service.UserManagerService;
import com.x.cms.core.entity.AppCategoryPermission;

import net.sf.ehcache.Ehcache;

public class ExcuteBase {
	
	protected Ehcache cache = ApplicationCache.instance().getCache( AppCategoryPermission.class);
	
	protected AppInfoServiceAdv appInfoServiceAdv = new AppInfoServiceAdv();
	protected UserManagerService userManagerService = new UserManagerService();
	protected CategoryInfoServiceAdv categoryInfoServiceAdv = new CategoryInfoServiceAdv();
	protected AppCategoryPermissionServiceAdv appCategoryPermissionServiceAdv = new AppCategoryPermissionServiceAdv();	
}
