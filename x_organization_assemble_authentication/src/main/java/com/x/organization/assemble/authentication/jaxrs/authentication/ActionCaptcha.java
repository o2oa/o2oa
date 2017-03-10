package com.x.organization.assemble.authentication.jaxrs.authentication;


import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.instrument.assemble.express.wrap.WrapCaptcha;
import com.x.organization.assemble.authentication.Business;

class ActionCaptcha extends ActionBase {

	private static Logger logger = LoggerFactory.getLogger(ActionCaptcha.class);

	ActionResult<WrapCaptcha> execute(Integer width, Integer height) throws Exception {
		ActionResult<WrapCaptcha> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			WrapCaptcha wrap = business.instrument().captcha().create(width, height);
			result.setData(wrap);
			return result;
		}
	}

}
