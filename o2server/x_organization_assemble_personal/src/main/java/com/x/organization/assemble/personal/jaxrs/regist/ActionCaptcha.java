package com.x.organization.assemble.personal.jaxrs.regist;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoCaptcha;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.organization.assemble.personal.Business;

class ActionCaptcha extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionCaptcha.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson,Integer width, Integer height) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			if (!StringUtils.equals(com.x.base.core.project.config.Person.REGISTER_TYPE_CAPTCHA,
					Config.person().getRegister())) {
				throw new ExceptionDisableCaptcha();
			}
			WoCaptcha wrap = business.instrument().captcha().create(width, height);
			Wo wo = new Wo();
			wo.setId(wrap.getId());
			wo.setImage(wrap.getImage());
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WoCaptcha {

	}

}
