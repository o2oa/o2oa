package com.x.query.assemble.designer.jaxrs.statement;

import java.lang.reflect.Field;
import java.util.Optional;

import com.x.base.core.project.bean.tuple.Triple;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.query.assemble.designer.Business;
import com.x.query.core.entity.Query;
import com.x.query.core.entity.schema.Statement;

abstract class BaseAction extends StandardJaxrsAction {

	protected void checkDuplicate(Business business, Statement statement) throws Exception {
		Optional<Triple<Statement, Field, Object>> opt = business.entityManagerContainer().conflict(Statement.class,
				statement);
		if (opt.isPresent()) {
			Query query = business.entityManagerContainer().find(opt.get().first().getQuery(), Query.class);
			throw new ExceptionDuplicate(query.getName(), opt.get().second().getName(), opt.get().third());
		}
	}
}
