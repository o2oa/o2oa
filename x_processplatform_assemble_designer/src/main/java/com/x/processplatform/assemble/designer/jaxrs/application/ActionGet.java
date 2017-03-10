package com.x.processplatform.assemble.designer.jaxrs.application;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.assemble.designer.wrapout.WrapOutApplication;
import com.x.processplatform.core.entity.element.Application;

class ActionGet extends ActionBase {

	ActionResult<WrapOutApplication> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<WrapOutApplication> result = new ActionResult<>();
			WrapOutApplication wrap = new WrapOutApplication();
			Business business = new Business(emc);
			Application application = emc.find(id, Application.class);
			if (null == application) {
				throw new ApplicationNotExistedException(id);
			}
			if (!business.applicationEditAvailable(effectivePerson, application)) {
				throw new ApplicationAccessDeniedException(effectivePerson.getName(), application.getName(),
						application.getId());
			}
			wrap = outCopier.copy(application);
			result.setData(wrap);
			return result;
		}
	}

}