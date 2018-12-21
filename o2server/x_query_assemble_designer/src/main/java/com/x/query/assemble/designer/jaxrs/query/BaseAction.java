package com.x.query.assemble.designer.jaxrs.query;

import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.query.assemble.designer.Business;
import com.x.query.core.entity.Query;

abstract class BaseAction extends StandardJaxrsAction {

	protected boolean idleName(Business business, Query query) throws Exception {
		return !business.entityManagerContainer().duplicateWithFlags(query.getId(), Query.class, query.getName());
	}

	protected boolean idleAlias(Business business, Query query) throws Exception {
		return !business.entityManagerContainer().duplicateWithFlags(query.getId(), Query.class, query.getAlias());
	}

	protected boolean idleId(Business business, Query query) throws Exception {
		return !business.entityManagerContainer().duplicateWithFlags(Query.class, query.getId());
	}

}