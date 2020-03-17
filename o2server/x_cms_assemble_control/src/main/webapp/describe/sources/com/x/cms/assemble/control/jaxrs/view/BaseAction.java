package com.x.cms.assemble.control.jaxrs.view;

import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.cms.assemble.control.service.CategoryInfoServiceAdv;
import com.x.cms.assemble.control.service.DocumentQueryService;
import com.x.cms.assemble.control.service.FormServiceAdv;
import com.x.cms.assemble.control.service.LogService;
import com.x.cms.assemble.control.service.PermissionQueryService;
import com.x.cms.assemble.control.service.UserManagerService;
import com.x.cms.assemble.control.service.ViewServiceAdv;
import com.x.cms.core.entity.element.View;

import net.sf.ehcache.Ehcache;

public class BaseAction extends StandardJaxrsAction {
	
	protected Ehcache cache = ApplicationCache.instance().getCache( View.class );

	protected LogService logService = new LogService();
	protected CategoryInfoServiceAdv categoryInfoServiceAdv = new CategoryInfoServiceAdv();
	protected ViewServiceAdv viewServiceAdv = new ViewServiceAdv();
	protected DocumentQueryService documentQueryService = new DocumentQueryService();
	protected UserManagerService userManagerService = new UserManagerService();	
	protected FormServiceAdv formServiceAdv = new FormServiceAdv();
	protected PermissionQueryService permissionQueryService = new PermissionQueryService();
}
