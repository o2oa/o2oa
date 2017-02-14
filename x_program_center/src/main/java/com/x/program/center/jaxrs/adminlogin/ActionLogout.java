package com.x.program.center.jaxrs.adminlogin;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpToken;
import com.x.base.core.http.TokenType;

public class ActionLogout {

	public WrapOutAdminLogin execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		HttpToken httpToken = new HttpToken();
		httpToken.deleteToken(request, response);
		WrapOutAdminLogin wrap = new WrapOutAdminLogin();
		wrap.setTokenType(TokenType.anonymous);
		wrap.setName(EffectivePerson.ANONYMOUS);
		return wrap;
	}

}