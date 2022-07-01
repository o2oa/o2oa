package com.x.organization.assemble.personal.jaxrs.exmail;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.connection.HttpConnection;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.organization.assemble.personal.Business;
import com.x.organization.core.entity.Person;

/**
 * 
 * @author ray
 *
 */
class ActionSso extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionSso.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson) throws Exception {

		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);

		checkEnable();

		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();

		if ((!effectivePerson.isAnonymous()) && (!effectivePerson.isCipher())) {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				Business business = new Business(emc);
				wo = this.get(business, effectivePerson);
			}
		}

		result.setData(wo);
		return result;
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

		private static final long serialVersionUID = -5433981326231458301L;
		
		private String url;

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

	}

}