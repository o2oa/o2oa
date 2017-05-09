package com.x.organization.assemble.personal.jaxrs.regist;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.project.jaxrs.CaptchaWo;
import com.x.base.core.project.server.Config;
import com.x.organization.assemble.personal.Business;

class ActionCaptcha extends ActionBase {
	
	private static Logger logger = LoggerFactory.getLogger(ActionCaptcha.class);

	ActionResult<CaptchaWo> execute(Integer width, Integer height) throws Exception {

		ActionResult<CaptchaWo> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			if (!StringUtils.equals(com.x.base.core.project.server.Person.REGISTER_TYPE_CAPTCHA,
					Config.person().getRegister())) {
				throw new DisableCaptchaException();
			}
			CaptchaWo wrap = business.instrument().captcha().create(width, height);
			result.setData(wrap);
			return result;
		}
	}

}
