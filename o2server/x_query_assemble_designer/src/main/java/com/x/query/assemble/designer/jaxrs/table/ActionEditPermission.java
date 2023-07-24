package com.x.query.assemble.designer.jaxrs.table;

import java.util.Date;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.query.assemble.designer.Business;
import com.x.query.core.entity.Query;
import com.x.query.core.entity.schema.Statement;
import com.x.query.core.entity.schema.Table;

class ActionEditPermission extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionEditPermission.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag, JsonElement jsonElement) throws Exception {
		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Table table = emc.flag(flag, Table.class);
			if (null == table) {
				throw new ExceptionEntityNotExist(flag, Table.class);
			}
			Wo wo = new Wo();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);

			Query query = business.entityManagerContainer().flag(table.getQuery(), Query.class);
			if (null == query) {
				throw new ExceptionEntityNotExist(table.getQuery(), Query.class);
			}
			if (!effectivePerson.isSecurityManager() && !business.editable(effectivePerson, query)) {
				throw new ExceptionAccessDenied(effectivePerson, query);
			}

			emc.beginTransaction(Table.class);
			Wi.copier.copy(wi, table);
			table.setLastUpdatePerson(effectivePerson.getDistinguishedName());
			table.setLastUpdateTime(new Date());
			emc.check(table, CheckPersistType.all);
			emc.commit();
			CacheManager.notify(Table.class);
			CacheManager.notify(Statement.class);

			wo.setId(table.getId());
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WoId {

		private static final long serialVersionUID = 2706685216257429716L;

	}

	public static class Wi extends Table {

		private static final long serialVersionUID = 694790255177362996L;

		static WrapCopier<Wi, Table> copier = WrapCopierFactory.wi(Wi.class, Table.class,
				ListTools.toList(Table.READPERSONLIST_FIELDNAME, Table.READUNITLIST_FIELDNAME,
						Table.READGROUPLIST_FIELDNAME, Table.EDITPERSONLIST_FIELDNAME, Table.EDITUNITLIST_FIELDNAME,
						Table.EDITGROUPLIST_FIELDNAME),
				null);
	}
}
