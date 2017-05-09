package com.x.cms.assemble.control.jaxrs.view;

import com.x.base.core.cache.ApplicationCache;
import com.x.cms.assemble.control.service.AppCategoryAdminServiceAdv;
import com.x.cms.assemble.control.service.AppCategoryPermissionService;
import com.x.cms.assemble.control.service.CategoryInfoServiceAdv;
import com.x.cms.assemble.control.service.DocumentInfoServiceAdv;
import com.x.cms.assemble.control.service.DocumentPermissionServiceAdv;
import com.x.cms.assemble.control.service.LogService;
import com.x.cms.assemble.control.service.UserManagerService;
import com.x.cms.assemble.control.service.ViewServiceAdv;
import com.x.cms.core.entity.element.View;

import net.sf.ehcache.Ehcache;

public class ExcuteBase {
	
	protected Ehcache cache = ApplicationCache.instance().getCache( View.class );
	
	protected AppCategoryAdminServiceAdv appCategoryAdminServiceAdv = new AppCategoryAdminServiceAdv();
	protected AppCategoryPermissionService appCategoryPermissionService = new AppCategoryPermissionService();
	protected DocumentPermissionServiceAdv documentPermissionServiceAdv = new DocumentPermissionServiceAdv();
	protected LogService logService = new LogService();
	protected CategoryInfoServiceAdv categoryInfoServiceAdv = new CategoryInfoServiceAdv();
	protected ViewServiceAdv viewServiceAdv = new ViewServiceAdv();
	protected DocumentInfoServiceAdv documentInfoServiceAdv = new DocumentInfoServiceAdv();
	protected UserManagerService userManagerService = new UserManagerService();	
}
