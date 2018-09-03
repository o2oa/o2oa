package com.x.cms.assemble.control.jaxrs.categoryinfo;

import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.cms.assemble.control.service.AppInfoServiceAdv;
import com.x.cms.assemble.control.service.CategoryInfoServiceAdv;
import com.x.cms.assemble.control.service.DocumentInfoServiceAdv;
import com.x.cms.assemble.control.service.PermissionQueryService;
import com.x.cms.assemble.control.service.QueryViewService;
import com.x.cms.assemble.control.service.UserManagerService;
import com.x.cms.core.entity.CategoryInfo;

import net.sf.ehcache.Ehcache;

public class BaseAction extends StandardJaxrsAction {

	protected Ehcache cache = ApplicationCache.instance().getCache(CategoryInfo.class);
	protected QueryViewService queryViewService = new QueryViewService();
	protected UserManagerService userManagerService = new UserManagerService();
	protected AppInfoServiceAdv appInfoServiceAdv = new AppInfoServiceAdv();
	protected CategoryInfoServiceAdv categoryInfoServiceAdv = new CategoryInfoServiceAdv();
	protected DocumentInfoServiceAdv documentServiceAdv = new DocumentInfoServiceAdv();
	protected PermissionQueryService permissionQueryService = new PermissionQueryService();
}
