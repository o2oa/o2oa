package com.x.organization.assemble.authentication.jaxrs.authentication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.HttpToken;
import com.x.base.core.project.http.TokenType;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

import io.swagger.v3.oas.annotations.media.Schema;

class ActionLogout extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionLogout.class);

	ActionResult<Wo> execute(HttpServletRequest request, HttpServletResponse response, EffectivePerson effectivePerson)
			throws Exception {

		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);

		ActionResult<Wo> result = new ActionResult<>();
		HttpToken httpToken = new HttpToken();
		httpToken.deleteToken(request, response);
		Wo wo = new Wo();
		wo.setTokenType(TokenType.anonymous);
		wo.setName(EffectivePerson.ANONYMOUS);
		result.setData(wo);
		return result;
	}

	@Schema(name = "com.x.organization.assemble.authentication.jaxrs.authentication.ActionLogout$Wo")
	public static class Wo extends AbstractWoAuthentication {

		private static final long serialVersionUID = 4883354487268278719L;

	}

}