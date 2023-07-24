package com.x.query.assemble.designer.jaxrs.table;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.exception.ExceptionEntityFieldEmpty;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.query.assemble.designer.Business;
import com.x.query.core.entity.schema.Statement;
import com.x.query.core.entity.schema.Table;

class ActionStatusBuild extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionStatusBuild.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag) throws Exception {
		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Table table = emc.flag(flag, Table.class);
			if (null == table) {
				throw new ExceptionEntityNotExist(flag, Table.class);
			}
			Business business = new Business(emc);
			this.check(effectivePerson, business, table);
			if (StringUtils.isEmpty(table.getDraftData())) {
				throw new ExceptionEntityFieldEmpty(Table.class, Table.DRAFTDATA_FIELDNAME);
			}
			emc.beginTransaction(Table.class);
			table.setData(table.getDraftData());
			table.setStatus(Table.STATUS_BUILD);
			emc.commit();
			CacheManager.notify(Table.class);
			CacheManager.notify(Statement.class);
			Wo wo = new Wo();
			wo.setId(table.getId());
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WoId {

		private static final long serialVersionUID = 301191937947918554L;

	}
}