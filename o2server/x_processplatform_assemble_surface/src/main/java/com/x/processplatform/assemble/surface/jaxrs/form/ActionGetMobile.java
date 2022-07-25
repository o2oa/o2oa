package com.x.processplatform.assemble.surface.jaxrs.form;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoMaxAgeFastETag;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.element.Form;

import io.swagger.v3.oas.annotations.media.Schema;

@Deprecated
class ActionGetMobile extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionGetMobile.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag) throws Exception {

		LOGGER.debug("execute:{}, flag:{}.", effectivePerson::getDistinguishedName, () -> flag);

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Form form = business.form().pick(flag);
			if (null == form) {
				throw new ExceptionEntityNotExist(flag, Form.class);
			}
			Wo wo = new Wo();
			wo.setData(form.getMobileDataOrData());
			wo.setFastETag(form.getId() + form.getUpdateTime().getTime());
			result.setData(wo);
			return result;
		}
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.form.ActionGetMobile$Wo")
	public static class Wo extends WoMaxAgeFastETag {

		private static final long serialVersionUID = -5016818812142868278L;

		private String data;

		public String getData() {
			return data;
		}

		public void setData(String data) {
			this.data = data;
		}

	}

}
