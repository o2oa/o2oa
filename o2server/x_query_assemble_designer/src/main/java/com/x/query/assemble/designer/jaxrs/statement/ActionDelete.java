package com.x.query.assemble.designer.jaxrs.statement;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.query.assemble.designer.Business;
import com.x.query.core.entity.Query;
import com.x.query.core.entity.schema.Statement;

class ActionDelete extends BaseAction {
	ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Statement statement = emc.flag(flag, Statement.class);
			if (null == statement) {
				throw new ExceptionEntityNotExist(flag, Statement.class);
			}
			com.x.query.core.entity.Query query = emc.flag(statement.getQuery(), com.x.query.core.entity.Query.class);
			if (null == query) {
				throw new ExceptionEntityNotExist(flag, Query.class);
			}
			if (!business.editable(effectivePerson, query)) {
				throw new ExceptionAccessDenied(effectivePerson, query);
			}
			emc.beginTransaction(Statement.class);
			emc.remove(statement);
			emc.commit();
			ApplicationCache.notify(Statement.class);
			Wo wo = new Wo();
			wo.setId(statement.getId());
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WoId {

	}
}