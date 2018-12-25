package com.x.processplatform.assemble.designer.jaxrs.querystat;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.QueryStat;

class ActionGet extends BaseAction {
	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
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
			Wo wo = Wo.copier.copy(queryStat);
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends QueryStat {

		private static final long serialVersionUID = -5755898083219447939L;

		static WrapCopier<QueryStat, Wo> copier = WrapCopierFactory.wo(QueryStat.class, Wo.class, null,
				JpaObject.FieldsInvisible);
	}
}
