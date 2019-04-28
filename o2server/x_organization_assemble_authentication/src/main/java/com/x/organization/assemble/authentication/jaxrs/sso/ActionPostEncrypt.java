package com.x.organization.assemble.authentication.jaxrs.sso;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.Token.Sso;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.Crypto;
import com.x.base.core.project.tools.DefaultCharset;

class ActionPostEncrypt extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionPostEncrypt.class);

	ActionResult<Wo> execute(HttpServletRequest request, HttpServletResponse response, EffectivePerson effectivePerson,
			JsonElement jsonElement) throws Exception {
		logger.debug(effectivePerson, "receive:{}", jsonElement);
		ActionResult<Wo> result = new ActionResult<>();
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		if (StringUtils.isEmpty(wi.getClient())) {
			throw new ExceptionClientEmpty();
		}
		if (StringUtils.isEmpty(wi.getCredential())) {
			throw new ExceptionEmptyCredential();
		}
		if (StringUtils.isEmpty(wi.getKey())) {
			throw new ExceptionEmptyKey();
		}
		Sso sso = Config.token().findSso(wi.getClient());
		if (null == sso) {
			throw new ExceptionClientNotExist(wi.getClient());
		}
		String str = wi.getCredential() + TOKEN_SPLIT + new Date().getTime();
		byte[] bs = Crypto.encrypt(str.getBytes(DefaultCharset.charset), wi.getKey().getBytes());
		String token = new String(Base64.encodeBase64(bs), DefaultCharset.charset);
		Wo wo = new Wo();
		wo.setToken(token);
		result.setData(wo);
		return result;
	}

	public static class Wi extends GsonPropertyObject {

		private String client;
		private String credential;
		private String key;

		public String getClient() {
			return client;
		}

		public void setClient(String client) {
			this.client = client;
		}

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

		public String getCredential() {
			return credential;
		}

		public void setCredential(String credential) {
			this.credential = credential;
		}

 

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