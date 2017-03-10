package com.x.organization.assemble.authentication.jaxrs.authentication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpToken;
import com.x.base.core.http.TokenType;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.organization.assemble.authentication.wrap.out.WrapOutAuthentication;

class ActionLogout extends ActionBase {

	private static Logger logger = LoggerFactory.getLogger(ActionLogout.class);

	ActionResult<WrapOutAuthentication> execute(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		ActionResult<WrapOutAuthentication> result = new ActionResult<>();
		HttpToken httpToken = new HttpToken();
		httpToken.deleteToken(request, response);
		WrapOutAuthentication wrap = new WrapOutAuthentication();
		wrap.setTokenType(TokenType.anonymous);
		wrap.setName(EffectivePerson.ANONYMOUS);
		result.setData(wrap);
		return result;
	}

}