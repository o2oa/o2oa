package com.x.processplatform.assemble.designer.jaxrs.application;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.core.entity.element.Application;

class ActionGet extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Wo wo = null;
			Business business = new Business(emc);
			Application application = emc.find(id, Application.class);
			if (null == application) {
				throw new ExceptionApplicationNotExist(id);
			}
			if (!effectivePerson.isSecurityManager() && !business.editable(effectivePerson, application)) {
				throw new ExceptionApplicationAccessDenied(effectivePerson.getDistinguishedName(),
						application.getName(), application.getId());
			}
			wo = Wo.copier.copy(application);
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends Application {

		private static final long serialVersionUID = -7648824521711153693L;

		static WrapCopier<Application, Wo> copier = WrapCopierFactory.wo(Application.class, Wo.class, null,
				JpaObject.FieldsInvisible);

	}

}
