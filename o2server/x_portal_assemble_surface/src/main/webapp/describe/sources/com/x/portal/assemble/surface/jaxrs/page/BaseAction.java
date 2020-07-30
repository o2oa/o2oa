package com.x.portal.assemble.surface.jaxrs.page;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.portal.core.entity.Page;

import net.sf.ehcache.Ehcache;

abstract class BaseAction extends StandardJaxrsAction {

	Ehcache pageCache = ApplicationCache.instance().getCache(Page.class);

	protected boolean isNotLoginPage(String id) throws Exception {
		return !(Config.portal().getIndexPage().getEnable()
				&& StringUtils.equals(id, Config.portal().getIndexPage().getPage()));
	}

}
