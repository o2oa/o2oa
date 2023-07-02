package com.x.organization.assemble.authentication.jaxrs.authentication;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.organization.core.express.assemble.authentication.jaxrs.authentication.ActionCaptchaRSAPublicKeyWo;

import io.swagger.v3.oas.annotations.media.Schema;

public class ActionCaptchaRSAPublicKey extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionCaptchaRSAPublicKey.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson) throws Exception {

		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);

		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		wo.setPublicKey(Config.publicKey());
		wo.setRsaEnable(Config.token().getRsaEnable());
		result.setData(wo);
		return result;
	}

	@Schema(name = "com.x.organization.assemble.authentication.jaxrs.authentication.ActionCaptchaRSAPublicKey$Wo")
	public static class Wo extends ActionCaptchaRSAPublicKeyWo {

		private static final long serialVersionUID = 4078969835023141099L;

	}

}