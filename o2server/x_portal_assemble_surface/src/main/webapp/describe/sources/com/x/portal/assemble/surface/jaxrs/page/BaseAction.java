package com.x.portal.assemble.surface.jaxrs.page;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.portal.core.entity.Page;
import com.x.base.core.project.cache.Cache.CacheCategory;

abstract class BaseAction extends StandardJaxrsAction {

	CacheCategory pageCache = new CacheCategory(Page.class);

	protected boolean isNotLoginPage(String id) throws Exception {
		return !(Config.portal().getIndexPage().getEnable()
				&& StringUtils.equals(id, Config.portal().getIndexPage().getPage()));
	}

}
