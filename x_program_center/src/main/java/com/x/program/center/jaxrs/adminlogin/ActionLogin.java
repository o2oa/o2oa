package com.x.program.center.jaxrs.adminlogin;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.HttpToken;
import com.x.base.core.http.TokenType;
import com.x.base.core.project.server.Config;

public class ActionLogin {
	public WrapOutAdminLogin execute(HttpServletRequest request, HttpServletResponse response, String credential,
			String password) throws Exception {
		if (!Config.token().isInitialManager(credential)) {
			/* 管理员登陆 */
			throw new Exception("credential not match.");
		}
		if (!StringUtils.equals(Config.token().getPassword(), password)) {
			throw new Exception("password not match, credential:" + credential + ".");
		}
		HttpToken httpToken = new HttpToken();
		EffectivePerson effectivePerson = new EffectivePerson(Config.token().initialManagerInstance().getName(),
				TokenType.manager, Config.token().getCipher());
		httpToken.setToken(request, response, effectivePerson);
		WrapOutAdminLogin wrap = new WrapOutAdminLogin();
		Config.token().initialManagerInstance().copyTo(wrap);
		wrap.setToken(effectivePerson.getToken());
		wrap.setTokenType(TokenType.manager);
		return wrap;
	}

}
