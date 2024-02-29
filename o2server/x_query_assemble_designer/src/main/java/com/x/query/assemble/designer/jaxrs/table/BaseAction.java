package com.x.query.assemble.designer.jaxrs.table;

import java.lang.reflect.Field;
import java.util.Optional;

import com.x.base.core.project.bean.tuple.Triple;
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

	protected void checkDuplicate(Business business, Table table) throws Exception {
		Optional<Triple<Table, Field, Object>> opt = business.entityManagerContainer().conflict(Table.class, table);
		if (opt.isPresent()) {
			Query query = business.entityManagerContainer().find(opt.get().first().getQuery(), Query.class);
			throw new ExceptionDuplicate(query.getName(), opt.get().second().getName(), opt.get().third());
		}
	}

}
