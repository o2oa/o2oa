package com.x.query.assemble.designer.jaxrs.view;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.query.assemble.designer.Business;
import com.x.query.core.entity.Query;
import com.x.query.core.entity.View;

class ActionDelete extends BaseAction {
	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			emc.beginTransaction(View.class);
			View view = emc.find(id, View.class);
			if (null == view) {
				throw new ExceptionViewNotExist(id);
			}
			Query query = emc.find(view.getQuery(), Query.class);
			if (null == query) {
				throw new ExceptionQueryNotExist(view.getQuery());
			}
			if (!business.editable(effectivePerson, query)) {
				throw new ExceptionQueryAccessDenied(effectivePerson.getDistinguishedName(), query.getName());
			}
			emc.remove(view, CheckRemoveType.all);
			emc.commit();
			CacheManager.notify(View.class);
			Wo wo = new Wo();
			wo.setId(view.getId());
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WoId {

	}
}