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
import com.x.processplatform.core.entity.element.Application;
import com.x.processplatform.core.entity.element.Form;

import io.swagger.v3.oas.annotations.media.Schema;

@Deprecated
class ActionGetWithApplication extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionGetWithApplication.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String applicationFlag, String flag) throws Exception {

		LOGGER.debug("execute:{}, applicationFlag:{}, flag:{}.", effectivePerson::getDistinguishedName,
				() -> applicationFlag, () -> flag);

		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Application application = business.application().pick(applicationFlag);
			if (null == application) {
				throw new ExceptionEntityNotExist(applicationFlag, Application.class);
			}
			Form form = business.form().pick(application, flag);
			Wo wo = new Wo();
			wo.setData(form.getDataOrMobileData());
			wo.setFastETag(form.getId() + form.getUpdateTime().getTime());
			result.setData(wo);
			return result;
		}
	}

	@Schema(name = "com.x.processplatform.assemble.surface.jaxrs.form.ActionGetWithApplication$Wo")
	public static class Wo extends WoMaxAgeFastETag {

		private static final long serialVersionUID = -413658152174057647L;

		private String data;

		public String getData() {
			return data;
		}

		public void setData(String data) {
			this.data = data;
		}

	}
}
