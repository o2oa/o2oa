package com.x.cms.assemble.control.jaxrs.view;

import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.cache.Cache;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.cms.assemble.control.service.CategoryInfoServiceAdv;
import com.x.cms.assemble.control.service.DocumentQueryService;
import com.x.cms.assemble.control.service.FormServiceAdv;
import com.x.cms.assemble.control.service.LogService;
import com.x.cms.assemble.control.service.PermissionQueryService;
import com.x.cms.assemble.control.service.UserManagerService;
import com.x.cms.assemble.control.service.ViewServiceAdv;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.element.Script;
import com.x.cms.core.entity.element.View;

import com.x.cms.core.entity.element.ViewCategory;
import com.x.cms.core.entity.element.ViewFieldConfig;
import net.sf.ehcache.Ehcache;

public class BaseAction extends StandardJaxrsAction {
	
	protected Cache.CacheCategory cacheCategory = new Cache.CacheCategory(View.class, ViewFieldConfig.class, ViewCategory.class);

	protected LogService logService = new LogService();
	protected CategoryInfoServiceAdv categoryInfoServiceAdv = new CategoryInfoServiceAdv();
	protected ViewServiceAdv viewServiceAdv = new ViewServiceAdv();
	protected DocumentQueryService documentQueryService = new DocumentQueryService();
	protected UserManagerService userManagerService = new UserManagerService();	
	protected FormServiceAdv formServiceAdv = new FormServiceAdv();
	protected PermissionQueryService permissionQueryService = new PermissionQueryService();
}
