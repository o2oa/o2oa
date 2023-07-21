package com.x.organization.assemble.authentication.jaxrs.oauth;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.jaxrs.WoText;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.organization.core.entity.OauthCode;

class ActionToken extends StandardJaxrsAction {

	private static Logger logger = LoggerFactory.getLogger(ActionToken.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String code, String grant_type, String contentType)
			throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			if (StringUtils.isEmpty(code)) {
				throw new ExceptionCodeEmpty();
			}
			if (StringUtils.isEmpty(grant_type)) {
				throw new ExceptionGrantTypeEmpty();
			}
			if (!StringUtils.equalsIgnoreCase(grant_type, "authorization_code")) {
				throw new ExceptionGrantTypeNotAuthorizationCode(grant_type);
			}
			OauthCode oauthCode = emc.firstEqualAndEqual(OauthCode.class, OauthCode.code_FIELDNAME, code,
					OauthCode.codeUsed_FIELDNAME, false);
			if (null == oauthCode) {
				throw new ExceptionOauthCodeNotExist(code);
			} else {
				emc.beginTransaction(OauthCode.class);
				oauthCode.setCodeUsed(true);
				emc.commit();
			}
			WoToken woToken = new WoToken();
			woToken.setAccess_token(oauthCode.getAccessToken());
			woToken.setExpires_in(3600);
			Wo wo = new Wo();
			wo.setText(gson.toJson(woToken));
			wo.setContentType(contentType);
			result.setData(wo);
			return result;
		}
	}

	public static class WoToken extends GsonPropertyObject {
		private String access_token;
		private Integer expires_in;
//		private String token_type = "bearer";
//		private String refresh_token = "123";
//		private String scope = "read";

//		public String getToken_type() {
//			return token_type;
//		}
//
//		public void setToken_type(String token_type) {
//			this.token_type = token_type;
//		}

		public String getAccess_token() {
			return access_token;
		}

		public void setAccess_token(String access_token) {
			this.access_token = access_token;
		}

		public Integer getExpires_in() {
			return expires_in;
		}

		public void setExpires_in(Integer expires_in) {
			this.expires_in = expires_in;
		}
	}

	public static class Wo extends WoText {

	}
}