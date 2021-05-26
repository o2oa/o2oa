package com.x.query.assemble.designer.jaxrs.importmodel;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.query.assemble.designer.Business;
import com.x.query.core.entity.ImportModel;
import com.x.query.core.entity.Query;

class ActionDelete extends BaseAction {
	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			emc.beginTransaction(ImportModel.class);
			ImportModel model = emc.find(id, ImportModel.class);
			if (null == model) {
				throw new ExceptionEntityNotExist(id, ImportModel.class);
			}
			Query query = emc.find(model.getQuery(), Query.class);
			if (null == query) {
				throw new ExceptionEntityNotExist(model.getQuery(), Query.class);
			}
			if (!business.editable(effectivePerson, query)) {
				throw new ExceptionAccessDenied(effectivePerson, query.getName());
			}
			emc.remove(model, CheckRemoveType.all);
			emc.commit();
			CacheManager.notify(ImportModel.class);
			Wo wo = new Wo();
			wo.setId(model.getId());
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WoId {

	}
}
