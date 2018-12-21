package com.x.processplatform.assemble.designer.jaxrs.querystat;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.QueryStat;

class ActionDelete extends BaseAction {
	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			emc.beginTransaction(QueryStat.class);
			QueryStat queryStat = emc.find(id, QueryStat.class);
			if (null == queryStat) {
				throw new ExceptionQueryStatNotExist(id);
			}
			Application application = emc.find(queryStat.getApplication(), Application.class);
			if (null == application) {
				throw new ExceptionApplicationNotExist(queryStat.getApplication());
			}
			if (!business.editable(effectivePerson, application)) {
				throw new ExceptionApplicationAccessDenied(effectivePerson.getDistinguishedName(),
						application.getName(), application.getId());
			}
			emc.remove(queryStat, CheckRemoveType.all);
			emc.commit();
			ApplicationCache.notify(QueryStat.class);
			Wo wo = new Wo();
			wo.setId(queryStat.getId());
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WoId {

	}
}
