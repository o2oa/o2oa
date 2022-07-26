package com.x.organization.assemble.authentication.jaxrs.authentication;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoCaptcha;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.organization.assemble.authentication.Business;
import com.x.organization.core.express.assemble.authentication.jaxrs.authentication.ActionCaptchaWo;

class ActionCaptcha extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionCaptcha.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, Integer width, Integer height) throws Exception {

		LOGGER.debug("execute:{}, width:{}, height:{}.", effectivePerson::getDistinguishedName, () -> width,
				() -> height);

		ActionResult<Wo> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			WoCaptcha wrap = business.instrument().captcha().create(width, height);
			Wo wo = new Wo();
			wo.setId(wrap.getId());
			wo.setImage(wrap.getImage());
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends ActionCaptchaWo {

		private static final long serialVersionUID = 6270286054165678137L;

	}
}
