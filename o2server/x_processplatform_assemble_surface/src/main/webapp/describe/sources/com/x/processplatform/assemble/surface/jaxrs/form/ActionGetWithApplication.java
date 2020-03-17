package com.x.processplatform.assemble.surface.jaxrs.form;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Form;

class ActionGetWithApplication extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String applicationFlag, String flag) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Application application = business.application().pick(applicationFlag);
			if (null == application) {
				throw new ExceptionEntityNotExist(applicationFlag, Application.class);
			}
			Form form = business.form().pick(application, flag);
			Wo wo = Wo.copier.copy(form);
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends Form {

		private static final long serialVersionUID = 1541438199059150837L;

		static WrapCopier<Form, Wo> copier = WrapCopierFactory.wo(Form.class, Wo.class, null,
				JpaObject.FieldsInvisible);

	}

}
