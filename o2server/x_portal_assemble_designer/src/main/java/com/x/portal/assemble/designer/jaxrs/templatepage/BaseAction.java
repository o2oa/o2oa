package com.x.portal.assemble.designer.jaxrs.templatepage;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.portal.assemble.designer.Business;
import com.x.portal.core.entity.TemplatePage;

abstract class BaseAction extends StandardJaxrsAction {

	void checkName(Business business, TemplatePage o) throws Exception {
		if (StringUtils.isEmpty(o.getName())) {
			throw new NameEmptyException();
		}
		String id = business.templatePage().getWithName(o.getName());
		if (StringUtils.isNotEmpty(id) && (!StringUtils.equals(o.getId(), id))) {
			throw new NameDuplicateException(o.getName());
		}
		id = business.templatePage().getWithAlias(o.getName());
		if (StringUtils.isNotEmpty(id) && (!StringUtils.equals(o.getId(), id))) {
			throw new NameDuplicateWithAliasException(o.getName());
		}
	}

	void checkAlias(Business business, TemplatePage o) throws Exception {
		if (StringUtils.isEmpty(o.getAlias())) {
			return;
		}
		String id = business.templatePage().getWithAlias(o.getAlias());
		if (StringUtils.isNotEmpty(id) && (!StringUtils.equals(o.getId(), id))) {
			throw new AliasDuplicateException(o.getAlias());
		}
		id = business.templatePage().getWithName(o.getAlias());
		if (StringUtils.isNotEmpty(id) && (!StringUtils.equals(o.getId(), id))) {
			throw new AliasDuplicateWithNameException(o.getAlias());
		}
	}
}