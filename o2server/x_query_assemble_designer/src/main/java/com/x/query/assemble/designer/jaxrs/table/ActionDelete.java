package com.x.query.assemble.designer.jaxrs.table;

import java.util.List;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.query.assemble.designer.Business;
import com.x.query.core.entity.schema.Statement;
import com.x.query.core.entity.schema.Table;

class ActionDelete extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionDelete.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag) throws Exception {
		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Table table = emc.flag(flag, Table.class);
			if (null == table) {
				throw new ExceptionEntityNotExist(flag, Table.class);
			}
			this.check(effectivePerson, business, table);
			List<Statement> statements = emc.listEqual(Statement.class, Statement.TABLE_FIELDNAME, table.getId());
			emc.beginTransaction(Statement.class);
			emc.beginTransaction(Table.class);
			for (Statement statement : statements) {
				emc.remove(statement, CheckRemoveType.all);
			}
			emc.commit();
			/* 有级联关系,先删除statement */
			emc.beginTransaction(Table.class);
			emc.remove(table);
			emc.commit();
			CacheManager.notify(Statement.class);
			CacheManager.notify(Table.class);
			Wo wo = new Wo();
			wo.setId(table.getId());
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WoId {

		private static final long serialVersionUID = 5408521814879545848L;

	}
}