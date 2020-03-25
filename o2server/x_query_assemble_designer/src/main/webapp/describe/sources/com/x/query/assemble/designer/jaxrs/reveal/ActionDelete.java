package com.x.query.assemble.designer.jaxrs.reveal;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.query.assemble.designer.Business;
import com.x.query.core.entity.Query;
import com.x.query.core.entity.Reveal;

class ActionDelete extends BaseAction {
	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			emc.beginTransaction(Reveal.class);
			Reveal reveal = emc.find(id, Reveal.class);
			if (null == reveal) {
				throw new ExceptionRevealNotExist(id);
			}
			Query query = emc.find(reveal.getQuery(), Query.class);
			if (null == query) {
				throw new ExceptionQueryNotExist(reveal.getQuery());
			}
			if (!business.editable(effectivePerson, query)) {
				throw new ExceptionQueryAccessDenied(effectivePerson.getDistinguishedName(), query.getName());
			}
			emc.remove(reveal, CheckRemoveType.all);
			emc.commit();
			ApplicationCache.notify(Reveal.class);
			Wo wo = new Wo();
			wo.setId(reveal.getId());
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WoId {

	}
}