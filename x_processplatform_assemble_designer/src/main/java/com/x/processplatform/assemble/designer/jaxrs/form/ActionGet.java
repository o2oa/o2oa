package com.x.processplatform.assemble.designer.jaxrs.form;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.processplatform.assemble.designer.Business;
import com.x.processplatform.assemble.designer.wrapout.WrapOutForm;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Form;

class ActionGet extends ActionBase {
	ActionResult<WrapOutForm> execute(EffectivePerson effectivePerson, String id) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<WrapOutForm> result = new ActionResult<>();
			Business business = new Business(emc);
			Form form = emc.find(id, Form.class, ExceptionWhen.not_found);
			Application application = emc.find(form.getApplication(), Application.class, ExceptionWhen.not_found);
			business.applicationEditAvailable(effectivePerson, application, ExceptionWhen.not_allow);
			WrapOutForm wrap = outCopier.copy(form);
			result.setData(wrap);
			return result;
		}
	}
}
