package com.x.organization.assemble.authentication.jaxrs.authentication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Audit;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.organization.assemble.authentication.Business;
import com.x.organization.core.entity.Person;

class ActionSwitchUser extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionSwitchUser.class);

	ActionResult<Wo> execute(HttpServletRequest request, HttpServletResponse response, EffectivePerson effectivePerson,
			JsonElement jsonElement) throws Exception {
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		ActionResult<Wo> result = new ActionResult<>();
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Audit audit = logger.audit(effectivePerson);
			if (effectivePerson.isNotManager()) {
				throw new ExceptionAccessDenied(effectivePerson);
			}
			Business business = new Business(emc);
			String personId = business.person().getWithCredential(wi.getCredential());
			if (StringUtils.isEmpty(personId)) {
				throw new ExceptionEntityNotExist(wi.getCredential(), Person.class);
			}
			Person o = emc.find(personId, Person.class);
			Wo wo = this.user(request, response, business, o, Wo.class);
			result.setData(wo);
			audit.log(o.getDistinguishedName(), "切换用户");
			return result;
		}
	}

	public static class Wi extends GsonPropertyObject {

		@FieldDescribe("用户凭证")
		private String credential;

		public String getCredential() {
			return credential;
		}

		public void setCredential(String credential) {
			this.credential = credential;
		}

	}

	public static class Wo extends AbstractWoAuthentication {

		private static final long serialVersionUID = -5992706204803405898L;

	}

}