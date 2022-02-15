package com.x.query.assemble.designer.jaxrs.statement;

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

import java.util.Date;

class ActionEditPermission extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionEditPermission.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag, JsonElement jsonElement) throws Exception {
		logger.debug(effectivePerson.getDistinguishedName());
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Statement statement = emc.flag(flag, Statement.class);
			if (null == statement) {
				throw new ExceptionEntityNotExist(flag, Statement.class);
			}

			Query query = emc.flag(statement.getQuery(), Query.class);
			if (null == query) {
				throw new ExceptionEntityNotExist(wi.getQuery(), Query.class);
			}
			if (!effectivePerson.isSecurityManager() && !business.editable(effectivePerson, query)) {
				throw new ExceptionAccessDenied(effectivePerson, query);
			}

			emc.beginTransaction(Statement.class);
			Wi.copier.copy(wi, statement);
			statement.setLastUpdatePerson(effectivePerson.getDistinguishedName());
			statement.setLastUpdateTime(new Date());
			emc.check(statement, CheckPersistType.all);
			emc.commit();
			CacheManager.notify(Statement.class);
			Wo wo = new Wo();
			wo.setId(statement.getId());
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WoId {

	}

	public static class Wi extends Statement {

		private static final long serialVersionUID = -5237741099036357033L;

		static WrapCopier<Wi, Statement> copier = WrapCopierFactory.wi(Wi.class, Statement.class,
				ListTools.toList(Statement.executePersonList_FIELDNAME, Statement.executeUnitList_FIELDNAME,
						Statement.executeGroupList_FIELDNAME, Statement.anonymousAccessible_FIELDNAME),
				null);
	}
}
