package com.x.processplatform.assemble.designer.jaxrs.queryview;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.QueryView;

class ActionDelete extends BaseAction {
	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			emc.beginTransaction(QueryView.class);
			QueryView queryView = emc.find(id, QueryView.class);
			if (null == queryView) {
				throw new ExceptionQueryViewNotExist(id);
			}
			Application application = emc.find(queryView.getApplication(), Application.class);
			if (null == application) {
				throw new ExceptionApplicationNotExist(queryView.getApplication());
			}
			if (!business.editable(effectivePerson, application)) {
				throw new ExceptionApplicationAccessDenied(effectivePerson.getDistinguishedName(),
						application.getName(), application.getId());
			}
			emc.remove(queryView, CheckRemoveType.all);
			emc.commit();
			ApplicationCache.notify(QueryView.class);
			Wo wo = new Wo();
			wo.setId(queryView.getId());
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WoId {
	}
}
