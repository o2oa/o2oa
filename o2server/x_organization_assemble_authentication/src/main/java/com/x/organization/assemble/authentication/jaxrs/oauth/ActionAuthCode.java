package com.x.organization.assemble.authentication.jaxrs.oauth;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.Token.Oauth;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionFieldEmpty;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.organization.assemble.authentication.Business;
import com.x.organization.core.entity.OauthCode;
import org.apache.commons.lang3.StringUtils;

class ActionAuthCode extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionAuthCode.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);
		if(effectivePerson.isNotManager()){
			throw new ExceptionAccessDenied(effectivePerson.getDistinguishedName());
		}
		ActionResult<Wo> result = new ActionResult<>();
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		if (StringUtils.isEmpty(wi.getClientId())) {
			throw new ExceptionClientIdEmpty();
		}
		if (StringUtils.isEmpty(wi.getPerson())) {
			throw new ExceptionFieldEmpty("person");
		}
		Oauth oauth = Config.token().findOauth(wi.getClientId());
		if (null == oauth) {
			throw new ExceptionOauthNotExist(wi.getClientId());
		}
		Wo wo = new Wo();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			OauthCode oauthCode = new OauthCode();
			oauthCode.setClientId(oauth.getClientId());
			oauthCode.setScope(this.getScope(oauth, ""));
			Business business = new Business(emc);
			String person = this.getPerson(business, wi.getPerson());
			if (StringUtils.isEmpty(person)) {
				throw new ExceptionPersonNotExist(effectivePerson.getDistinguishedName());
			}
			oauthCode.setPerson(person);
			emc.beginTransaction(OauthCode.class);
			emc.persist(oauthCode, CheckPersistType.all);
			emc.commit();
			wo.setCode(oauthCode.getCode());
		}
		result.setData(wo);
		return result;
	}

	private String getPerson(Business business, String person) throws Exception {
		return business.person().getWithCredential(person);
	}

	public static class Wi extends GsonPropertyObject {

		private static final long serialVersionUID = -1453106326481426734L;

		@FieldDescribe("oauth客户端ID")
		private String clientId;

		@FieldDescribe("oauth单点用户")
		private String person;

		public String getClientId() {
			return clientId;
		}

		public void setClientId(String clientId) {
			this.clientId = clientId;
		}

		public String getPerson() {
			return person;
		}

		public void setPerson(String person) {
			this.person = person;
		}
	}

	public static class Wo extends GsonPropertyObject {

		private static final long serialVersionUID = 4407474946815615148L;

		@FieldDescribe("oauth单点code")
		private String code;

		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}
	}

}
