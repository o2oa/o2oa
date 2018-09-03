package com.x.query.assemble.designer.jaxrs.stat;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.query.assemble.designer.Business;
import com.x.query.core.entity.Query;
import com.x.query.core.entity.Stat;

class ActionGet extends BaseAction {
	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Stat stat = emc.find(id, Stat.class);
			if (null == stat) {
				throw new ExceptionStatNotExist(id);
			}
			Query query = emc.find(stat.getQuery(), Query.class);
			if (null == query) {
				throw new ExceptionQueryNotExist(stat.getQuery());
			}
			if (!business.editable(effectivePerson, query)) {
				throw new ExceptionQueryAccessDenied(effectivePerson.getDistinguishedName(), query.getName());
			}
			Wo wo = Wo.copier.copy(stat);
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends Stat {

		private static final long serialVersionUID = -5755898083219447939L;

		static WrapCopier<Stat, Wo> copier = WrapCopierFactory.wo(Stat.class, Wo.class, null,
				JpaObject.FieldsInvisible);
	}
}
