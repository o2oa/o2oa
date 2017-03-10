package com.x.cms.assemble.control.jaxrs.categoryinfo;

import com.x.base.core.cache.ApplicationCache;
import com.x.cms.assemble.control.service.AppCategoryAdminServiceAdv;
import com.x.cms.assemble.control.service.AppCategoryPermissionServiceAdv;
import com.x.cms.assemble.control.service.CategoryInfoServiceAdv;
import com.x.cms.assemble.control.service.DocumentInfoServiceAdv;
import com.x.cms.assemble.control.service.UserManagerService;
import com.x.cms.core.entity.CategoryInfo;

import net.sf.ehcache.Ehcache;

public class ExcuteBase {
	
	protected static Ehcache cache = ApplicationCache.instance().getCache( CategoryInfo.class);
	
	protected UserManagerService userManagerService = new UserManagerService();
	protected AppCategoryAdminServiceAdv appCategoryAdminServiceAdv = new AppCategoryAdminServiceAdv();
	protected AppCategoryPermissionServiceAdv appCategoryPermissionServiceAdv = new AppCategoryPermissionServiceAdv();
	protected CategoryInfoServiceAdv categoryInfoServiceAdv = new CategoryInfoServiceAdv();
	protected DocumentInfoServiceAdv documentServiceAdv = new DocumentInfoServiceAdv();
	
}
