package com.x.processplatform.assemble.surface.jaxrs.form;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.element.Form;

class ActionGetMobile extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Form form = business.form().pick(flag);
			if (null == form) {
				throw new ExceptionEntityNotExist(flag, Form.class);
			}
			Wo wo = new Wo();
			wo.setData(form.getMobileDataOrData());
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends GsonPropertyObject {

		private String data;

		public String getData() {
			return data;
		}

		public void setData(String data) {
			this.data = data;
		}

	}

}
