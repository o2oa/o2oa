package com.x.organization.assemble.authentication.jaxrs.authentication;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.Token.OauthClient;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;

class ActionOauthGet extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionOauthGet.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String name) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		OauthClient oauthClient = null;
		if (ListTools.isNotEmpty(Config.token().getOauthClients())) {
			for (OauthClient o : Config.token().getOauthClients()) {
				if (BooleanUtils.isTrue(o.getEnable()) && StringUtils.equals(o.getName(), name)) {
					oauthClient = o;
				}
			}
		}
		if (null == oauthClient) {
			throw new ExceptionOauthNotExist(name);
		}
		Wo wo = new Wo();
		wo.setName(oauthClient.getName());
		wo.setRedirectUri(oauthClient.getAuthAddress());
		wo.setAuthAddress(oauthClient.getAuthAddress());
		wo.setAuthMethod(oauthClient.getAuthMethod());
		wo.setIcon(oauthClient.getIcon());
		String authParameter = this.fillAuthParameter(oauthClient.getAuthParameter(), oauthClient);
		logger.debug("auth parameter:{}.", authParameter);
		wo.setAuthParameter(authParameter);
		result.setData(wo);
		return result;
	}

	public static class Wo extends GsonPropertyObject {

		private String name;
		private String redirectUri;
		private String authAddress;
		private String authMethod;
		private String authParameter;
		private String icon;

		public String getIcon() {
			return icon;
		}

		public void setIcon(String icon) {
			this.icon = icon;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getRedirectUri() {
			return redirectUri;
		}

		public void setRedirectUri(String redirectUri) {
			this.redirectUri = redirectUri;
		}

		public String getAuthMethod() {
			return authMethod;
		}

		public void setAuthMethod(String authMethod) {
			this.authMethod = authMethod;
		}

		public String getAuthParameter() {
			return authParameter;
		}

		public void setAuthParameter(String authParameter) {
			this.authParameter = authParameter;
		}

		public String getAuthAddress() {
			return authAddress;
		}

		public void setAuthAddress(String authAddress) {
			this.authAddress = authAddress;
		}
	}

}