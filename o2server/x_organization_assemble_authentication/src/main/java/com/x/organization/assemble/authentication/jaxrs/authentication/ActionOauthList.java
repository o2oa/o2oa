package com.x.organization.assemble.authentication.jaxrs.authentication;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.project.config.Dingding;
import com.x.base.core.project.config.Qiyeweixin;
import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.Token.OauthClient;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;

class ActionOauthList extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionOauthList.class);

	ActionResult<List<Wo>> execute(EffectivePerson effectivePerson) throws Exception {
		ActionResult<List<Wo>> result = new ActionResult<>();
		List<Wo> wos = new ArrayList<>();
		if (ListTools.isNotEmpty(Config.token().getOauthClients())) {
			for (OauthClient o : Config.token().getOauthClients()) {
				if (BooleanUtils.isTrue(o.getEnable())) {
					Wo wo = new Wo();
					wo.setName(o.getName());
					wo.setIcon(o.getIcon());
					wo.setAuthAddress(o.getAuthAddress());
					wo.setAuthMethod(o.getAuthMethod());
					wo.setDisplayName(o.getDisplayName());
					wo.setBindingEnable(o.getBindingEnable());
					String authParameter = this.fillAuthParameter(o.getAuthParameter(), o);
					logger.debug("auth parameter:{}.", authParameter);
					wo.setAuthParameter(authParameter);
					wos.add(wo);
				}
			}
		}
		//企业微信扫码登录
		if (Config.qiyeweixin().getScanLoginEnable()) {
			Wo wo = new Wo();
			wo.setName("企业微信");
			wo.setDisplayName("@O2企业微信");
			wo.setIcon(Qiyeweixin.qywxLogo);
			wos.add(wo);
		}
		//钉钉扫码登录
		if (Config.dingding().getScanLoginEnable()) {
			Wo wo = new Wo();
			wo.setName("钉钉");
			wo.setIcon(Dingding.dingdingLogo);
			wo.setDisplayName("@O2钉钉");
			wos.add(wo);
		}
		result.setData(wos);
		return result;
	}

	public static class Wo extends GsonPropertyObject {

		private String name;
		private String displayName;
		private String authAddress;
		private String authMethod;
		private String authParameter;
		private String icon;
		private Boolean bindingEnable;

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

		public Boolean getBindingEnable() {
			return bindingEnable;
		}

		public void setBindingEnable(Boolean bindingEnable) {
			this.bindingEnable = bindingEnable;
		}

		public String getDisplayName() {
			return displayName;
		}

		public void setDisplayName(String displayName) {
			this.displayName = displayName;
		}

	}

}