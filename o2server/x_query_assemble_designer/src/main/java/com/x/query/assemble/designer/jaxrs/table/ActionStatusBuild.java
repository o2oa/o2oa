package com.x.query.assemble.designer.jaxrs.table;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.exception.ExceptionEntityFieldEmpty;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.query.assemble.designer.Business;
import com.x.query.core.entity.schema.Statement;
import com.x.query.core.entity.schema.Table;

class ActionStatusBuild extends BaseAction {
	ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Table table = emc.flag(flag, Table.class);
			if (null == table) {
				throw new ExceptionEntityNotExist(flag, Table.class);
			}
			Business business = new Business(emc);
			this.check(effectivePerson, business, table);
			if (StringUtils.isEmpty(table.getDraftData())) {
				throw new ExceptionEntityFieldEmpty(Table.class, Table.draftData_FIELDNAME);
			}
			emc.beginTransaction(Table.class);
			table.setData(table.getDraftData());
			table.setStatus(Table.STATUS_build);
			emc.commit();
			ApplicationCache.notify(Table.class);
			ApplicationCache.notify(Statement.class);
			Wo wo = new Wo();
			wo.setId(table.getId());
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WoId {

	}
}