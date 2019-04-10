package com.x.query.assemble.designer.jaxrs.statement;

import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.query.assemble.designer.Business;
import com.x.query.core.entity.Query;
import com.x.query.core.entity.schema.Statement;
import com.x.query.core.entity.schema.Table;

abstract class BaseAction extends StandardJaxrsAction {

	protected void check(EffectivePerson effectivePerson, Business business, Statement statement) throws Exception {
		Table table = business.entityManagerContainer().flag(statement.getTable(), Table.class);
		if (null == table) {
			throw new ExceptionEntityNotExist(statement.getTable(), Table.class);
		}
		Query query = business.entityManagerContainer().flag(table.getQuery(), Query.class);
		if (null == query) {
			throw new ExceptionEntityNotExist(table.getQuery(), Query.class);
		}
		if (!business.editable(effectivePerson, query)) {
			throw new ExceptionAccessDenied(effectivePerson, query);
		}
	}

}
