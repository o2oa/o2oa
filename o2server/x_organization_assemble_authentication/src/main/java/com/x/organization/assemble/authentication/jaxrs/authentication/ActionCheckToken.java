package com.x.organization.assemble.authentication.jaxrs.authentication;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionFieldEmpty;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.http.HttpToken;
import com.x.base.core.project.jaxrs.WrapString;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;

class ActionCheckToken extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionCheckToken.class);

	ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson,
			JsonElement jsonElement) throws Exception {

		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);

		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		ActionResult<Wo> result = new ActionResult<>();

		if (effectivePerson.isNotManager()) {
			throw new ExceptionAccessDenied(effectivePerson);
		}
		if(StringUtils.isBlank(wi.getToken())){
			throw new ExceptionFieldEmpty("token");
		}
		HttpToken httpToken = new HttpToken();
		EffectivePerson ep = httpToken.who(wi.getToken(), Config.token().getCipher(), HttpToken.remoteAddress(request));
		Wo wo = new Wo();
		wo.setValue(ep.getDistinguishedName());
		result.setData(wo);
		return result;
	}

	public static class Wi extends GsonPropertyObject {
		@FieldDescribe("令牌")
		private String token;

		public String getToken() {
			return token;
		}

		public void setToken(String token) {
			this.token = token;
		}
	}

	public static class Wo extends WrapString {

		private static final long serialVersionUID = -5992706204803405898L;

	}

}
