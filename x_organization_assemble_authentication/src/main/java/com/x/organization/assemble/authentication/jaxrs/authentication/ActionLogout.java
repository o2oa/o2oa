package com.x.organization.assemble.authentication.jaxrs.authentication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpToken;
import com.x.base.core.http.TokenType;
import com.x.organization.assemble.authentication.wrap.out.WrapOutAuthentication;

class ActionLogout {

	protected WrapOutAuthentication execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		HttpToken httpToken = new HttpToken();
		httpToken.deleteToken(request, response);
		WrapOutAuthentication wrap = new WrapOutAuthentication();
		wrap.setTokenType(TokenType.anonymous);
		wrap.setName(EffectivePerson.ANONYMOUS);
		return wrap;
	}

}