package com.x.query.assemble.designer.jaxrs.table;

import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.query.assemble.designer.Business;
import com.x.query.core.entity.Query;
import com.x.query.core.entity.schema.Table;

abstract class BaseAction extends StandardJaxrsAction {

	protected void check(EffectivePerson effectivePerson, Business business, Table table) throws Exception {
		Query query = business.entityManagerContainer().flag(table.getQuery(), Query.class);
		if (null == query) {
			throw new ExceptionEntityNotExist(table.getQuery(), Query.class);
		}
		if (!business.editable(effectivePerson, query)) {
			throw new ExceptionAccessDenied(effectivePerson, query);
		}
	}

}