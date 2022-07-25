package com.x.organization.assemble.authentication.jaxrs.authentication;

import java.io.IOException;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.organization.core.express.assemble.authentication.jaxrs.authentication.ActionCaptchaLoginRSAPublicKeyWo;

import io.swagger.v3.oas.annotations.media.Schema;

public class ActionCaptchaLoginRSAPublicKey extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionCaptchaLoginRSAPublicKey.class);

	ActionResult<Wo> execute(HttpServletRequest request, HttpServletResponse response, EffectivePerson effectivePerson)
			throws IOException, URISyntaxException {

		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);

		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		wo.setPublicKey(Config.publicKey());
		result.setData(wo);
		return result;
	}

	@Schema(name = "com.x.organization.assemble.authentication.jaxrs.authentication.ActionCaptchaLoginRSAPublicKey$Wo")
	public static class Wo extends ActionCaptchaLoginRSAPublicKeyWo {

		private static final long serialVersionUID = 4078969835023141099L;

	}

}