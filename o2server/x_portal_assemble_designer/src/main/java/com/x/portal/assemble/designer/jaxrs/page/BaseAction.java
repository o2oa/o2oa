package com.x.portal.assemble.designer.jaxrs.page;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.cache.Cache.CacheCategory;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.portal.assemble.designer.Business;
import com.x.portal.core.entity.Page;
import com.x.portal.core.entity.Portal;

abstract class BaseAction extends StandardJaxrsAction {

	static CacheCategory cache = new CacheCategory(Page.class);

	void checkName(Business business, Page page) throws Exception {
		if (StringUtils.isEmpty(page.getName())) {
			throw new NameEmptyException();
		}
		String id = business.page().getWithNameWithPortal(page.getName(), page.getPortal());
		if (StringUtils.isNotEmpty(id) && (!StringUtils.equals(page.getId(), id))) {
			throw new NameDuplicateException(page.getName());
		}
		id = business.page().getWithAliasWithPortal(page.getName(), page.getPortal());
		if (StringUtils.isNotEmpty(id) && (!StringUtils.equals(page.getId(), id))) {
			throw new NameDuplicateWithAliasException(page.getName());
		}
	}

	void checkAlias(Business business, Page page) throws Exception {
		if (StringUtils.isEmpty(page.getAlias())) {
			return;
		}
		String id = business.page().getWithAliasWithPortal(page.getAlias(), page.getPortal());
		if (StringUtils.isNotEmpty(id) && (!StringUtils.equals(page.getId(), id))) {
			throw new AliasDuplicateException(page.getAlias());
		}
		id = business.page().getWithNameWithPortal(page.getAlias(), page.getPortal());
		if (StringUtils.isNotEmpty(id) && (!StringUtils.equals(page.getId(), id))) {
			throw new AliasDuplicateWithNameException(page.getAlias());
		}
	}

	boolean isBecomeFirstPage(Business business, Portal portal, Page page) throws Exception {
		if (business.page().isFirstPage(page)) {
			Page o = business.entityManagerContainer().find(portal.getFirstPage(), Page.class);
			if (null == o) {
				return true;
			}
		}
		return false;
	}
}