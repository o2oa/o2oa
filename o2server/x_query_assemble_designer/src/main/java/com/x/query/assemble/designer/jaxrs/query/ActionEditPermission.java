package com.x.query.assemble.designer.jaxrs.query;

import java.util.Date;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.query.assemble.designer.Business;
import com.x.query.core.entity.Query;

class ActionEditPermission extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionEditPermission.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag, JsonElement jsonElement) throws Exception {
		logger.debug(effectivePerson.getDistinguishedName());
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Business business = new Business(emc);
			Query query = emc.flag(flag, Query.class);
			if (null == query) {
				throw new ExceptionQueryNotExist(flag);
			}
			if (!effectivePerson.isSecurityManager() && !business.editable(effectivePerson, query)) {
				throw new ExceptionQueryAccessDenied(effectivePerson.getDistinguishedName(), query.getName(),
						query.getId());
			}

			emc.beginTransaction(Query.class);
			Wi.copier.copy(wi, query);
			query.setLastUpdatePerson(effectivePerson.getDistinguishedName());
			query.setLastUpdateTime(new Date());
			emc.check(query, CheckPersistType.all);
			emc.commit();
			CacheManager.notify(Query.class);
			Wo wo = new Wo();
			wo.setId(query.getId());
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WoId {
	}

	public static class Wi extends Query {

		private static final long serialVersionUID = -5028892909521045209L;

		static WrapCopier<Wi, Query> copier = WrapCopierFactory.wi(Wi.class, Query.class,
				ListTools.toList(Query.controllerList_FIELDNAME, Query.availableIdentityList_FIELDNAME,
						Query.availableUnitList_FIELDNAME, Query.availableGroupList_FIELDNAME),
				null);

	}
}
