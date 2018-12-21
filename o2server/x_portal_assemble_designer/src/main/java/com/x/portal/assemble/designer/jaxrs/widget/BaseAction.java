package com.x.portal.assemble.designer.jaxrs.widget;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.portal.assemble.designer.Business;
import com.x.portal.core.entity.Widget;

import net.sf.ehcache.Ehcache;

abstract class BaseAction extends StandardJaxrsAction {

	static Ehcache cache = ApplicationCache.instance().getCache(Widget.class);

	void checkName(Business business, Widget widget) throws Exception {
		if (StringUtils.isEmpty(widget.getName())) {
			throw new ExceptionNameEmpty();
		}
		String id = business.widget().getWithNameWithPortal(widget.getName(), widget.getPortal());
		if (StringUtils.isNotEmpty(id) && (!StringUtils.equals(widget.getId(), id))) {
			throw new ExceptionNameDuplicate(widget.getName());
		}
		id = business.page().getWithAliasWithPortal(widget.getName(), widget.getPortal());
		if (StringUtils.isNotEmpty(id) && (!StringUtils.equals(widget.getId(), id))) {
			throw new ExceptionNameDuplicateWithAlias(widget.getName());
		}
	}

	void checkAlias(Business business, Widget widget) throws Exception {
		if (StringUtils.isEmpty(widget.getAlias())) {
			return;
		}
		String id = business.page().getWithAliasWithPortal(widget.getAlias(), widget.getPortal());
		if (StringUtils.isNotEmpty(id) && (!StringUtils.equals(widget.getId(), id))) {
			throw new ExceptionAliasDuplicate(widget.getAlias());
		}
		id = business.page().getWithNameWithPortal(widget.getAlias(), widget.getPortal());
		if (StringUtils.isNotEmpty(id) && (!StringUtils.equals(widget.getId(), id))) {
			throw new ExceptionAliasDuplicateWithName(widget.getAlias());
		}
	}

}