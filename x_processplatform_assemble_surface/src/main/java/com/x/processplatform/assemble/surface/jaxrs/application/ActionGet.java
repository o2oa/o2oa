package com.x.processplatform.assemble.surface.jaxrs.application;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.assemble.surface.wrapout.element.WrapOutApplication;
import com.x.processplatform.core.entity.element.Application;

class ActionGet extends ActionBase {

	ActionResult<WrapOutApplication> execute(EffectivePerson effectivePerson, String flag) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<WrapOutApplication> result = new ActionResult<>();
			Business business = new Business(emc);
			Application application = business.application().pick(flag, ExceptionWhen.not_found);
			WrapOutApplication wrap = applicationOutCopier.copy(application);
			wrap.setAllowControl(business.application().allowControl(effectivePerson, application));
			result.setData(wrap);
			return result;
		}
	}

}
