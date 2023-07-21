package com.x.cms.assemble.control.jaxrs.categoryinfo;

import com.x.base.core.project.cache.Cache;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.cms.assemble.control.service.*;
import com.x.cms.core.entity.AppInfo;
import com.x.cms.core.entity.CategoryInfo;
import com.x.cms.core.entity.Document;
import com.x.cms.core.entity.element.ViewCategory;

public class BaseAction extends StandardJaxrsAction {

	protected Cache.CacheCategory cacheCategory = new Cache.CacheCategory(AppInfo.class, CategoryInfo.class,
			ViewCategory.class, Document.class);

	protected UserManagerService userManagerService = new UserManagerService();
	protected AppInfoServiceAdv appInfoServiceAdv = new AppInfoServiceAdv();
	protected CategoryInfoServiceAdv categoryInfoServiceAdv = new CategoryInfoServiceAdv();
	protected DocumentQueryService documentServiceAdv = new DocumentQueryService();
	protected DocumentInfoService documentInfoService = new DocumentInfoService();
	protected PermissionQueryService permissionQueryService = new PermissionQueryService();
}
