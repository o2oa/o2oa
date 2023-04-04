package com.x.program.center.jaxrs.invoke;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.Token.Sso;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapString;
import com.x.base.core.project.tools.Crypto;

class ActionToken extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		Wo wo = new Wo();
		if (effectivePerson.isAnonymous()) {
			throw new ExceptionAccessDenied(effectivePerson);
		}

		Sso sso = Config.token().findSso(wi.getClient());
		if (null == sso) {
			throw new ExceptionClientNotExist(wi.getClient());
		}

		wo.setValue(Crypto.encrypt(effectivePerson.getDistinguishedName() + SPLIT + System.currentTimeMillis(),
				sso.getKey(), Config.person().getEncryptType()));
		result.setData(wo);
		return result;

	}

	public static class Wi extends GsonPropertyObject {

		private static final long serialVersionUID = -251331390296713913L;

		@FieldDescribe("鉴权认证接入名称")
		private String client;

		protected String getClient() {
			return client;
		}

		protected void setClient(String client) {
			this.client = client;
		}

	}

	public static class Wo extends WrapString {

		private static final long serialVersionUID = 71264076097806524L;

	}

}
