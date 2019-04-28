package com.x.organization.assemble.authentication.jaxrs.sso;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.Token.Sso;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.Crypto;
import com.x.base.core.project.tools.DefaultCharset;

class ActionGetEncrypt extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionGetEncrypt.class);

	ActionResult<Wo> execute(HttpServletRequest request, HttpServletResponse response, EffectivePerson effectivePerson,
			String client, String key, String credential) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		if (StringUtils.isEmpty(client)) {
			throw new ExceptionClientEmpty();
		}
		if (StringUtils.isEmpty(credential)) {
			throw new ExceptionEmptyCredential();
		}
		if (StringUtils.isEmpty(key)) {
			throw new ExceptionEmptyKey();
		}
		Sso sso = Config.token().findSso(client);
		if (null == sso) {
			throw new ExceptionClientNotExist(client);
		}
		String str = credential + TOKEN_SPLIT + new Date().getTime();
		byte[] bs = Crypto.encrypt(str.getBytes(DefaultCharset.charset), key.getBytes());
		String token = new String(Base64.encodeBase64(bs), DefaultCharset.charset);
		Wo wo = new Wo();
		wo.setToken(token);
		result.setData(wo);
		return result;
	}

	public static class Wo extends GsonPropertyObject {

		private String token;

		public String getToken() {
			return token;
		}

		public void setToken(String token) {
			this.token = token;
		}

	}

}