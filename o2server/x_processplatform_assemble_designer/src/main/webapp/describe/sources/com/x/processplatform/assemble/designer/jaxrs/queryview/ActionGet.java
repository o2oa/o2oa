package com.x.processplatform.assemble.designer.jaxrs.queryview;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.QueryView;

class ActionGet extends BaseAction {
	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
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
			Wo wo = Wo.copier.copy(queryView);
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends QueryView {

		private static final long serialVersionUID = 2886873983211744188L;

		static WrapCopier<QueryView, Wo> copier = WrapCopierFactory.wo(QueryView.class, Wo.class, null,
				JpaObject.FieldsInvisible);

	}

}
