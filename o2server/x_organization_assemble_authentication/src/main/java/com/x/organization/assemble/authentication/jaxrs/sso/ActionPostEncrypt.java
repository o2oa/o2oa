package com.x.organization.assemble.authentication.jaxrs.sso;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.Token.Sso;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.Crypto;
import com.x.organization.core.express.assemble.authentication.jaxrs.sso.ActionPostEncryptWi;
import com.x.organization.core.express.assemble.authentication.jaxrs.sso.ActionPostEncryptWo;

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
		// byte[] bs = Crypto.encrypt(str.getBytes(DefaultCharset.charset),
		// wi.getKey().getBytes());
		// String token = new String(Base64.encodeBase64(bs), DefaultCharset.charset);
		String token = Crypto.encrypt(str, wi.getKey());
		Wo wo = new Wo();
		wo.setToken(token);
		result.setData(wo);
		return result;
	}

	public static class Wi extends ActionPostEncryptWi {

	}

	public static class Wo extends ActionPostEncryptWo {

	}

}