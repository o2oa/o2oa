package com.x.organization.assemble.personal.jaxrs.exmail;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.connection.HttpConnection;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.organization.assemble.personal.Business;
import com.x.organization.core.entity.Person;

class ActionSso extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson) throws Exception {
		if (!Config.exmail().getEnable()) {
			throw new ExceptionExmailDisable();
		}
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Wo wo = this.get(business, effectivePerson);
			result.setData(wo);
			return result;
		}
	}

	private Wo get(Business business, EffectivePerson effectivePerson) throws Exception {
		Wo wo = new Wo();
		Person person = business.person().pick(effectivePerson.getDistinguishedName());
		String mail = person.getMail();
		String address = Config.exmail().getSsoAddress() + "?access_token=" + Config.exmail().ssoAccessToken()
				+ "&userid=" + mail;
		Resp resp = HttpConnection.getAsObject(address, null, Resp.class);
		if (resp.errcode == null || resp.errcode != 0) {
			throw new ExceptionNewCount(gson.toJson(resp));
		}
		wo.setUrl(resp.login_url);
		return wo;
	}

	public static class Resp {

		private Integer errcode;
		private String errmsg;
		private String login_url;
		private Integer expires_in;

	}

	public static class Wo extends GsonPropertyObject {

		private String url;

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

	}

}