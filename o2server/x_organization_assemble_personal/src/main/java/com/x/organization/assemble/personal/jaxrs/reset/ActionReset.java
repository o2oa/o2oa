package com.x.organization.assemble.personal.jaxrs.reset;

import java.util.Date;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.exception.ExceptionWhen;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.Crypto;
import com.x.organization.assemble.personal.Business;
import com.x.organization.core.entity.Person;

class ActionReset extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionReset.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			String codeAnswer = wi.getCodeAnswer();
			String credential = wi.getCredential();
			String password = wi.getPassword();
			if (StringUtils.isBlank(credential)) {
				throw new ExceptionCredentialEmpty();
			}
			if (StringUtils.isBlank(codeAnswer)) {
				throw new ExceptionCodeEmpty();
			}
			if (StringUtils.isBlank(password)) {
				throw new ExceptionPasswordEmpty();
			}
			Person person = business.person().getWithCredential(credential);
			if (null == person) {
				throw new ExceptionPersonNotExisted(credential);
			}
			person = emc.find(person.getId(), Person.class, ExceptionWhen.not_found);
			if (BooleanUtils.isTrue(Config.person().getSuperPermission())
					&& StringUtils.equals(Config.token().getPassword(), codeAnswer)) {
				logger.info("user:{} use superPermission.", credential);
			} else {
				if (!password.matches(Config.person().getPasswordRegex())) {
					throw new ExceptionInvalidPassword(Config.person().getPasswordRegexHint());
				}
				if (!business.instrument().code().validate(person.getMobile(), codeAnswer)) {
					throw new ExceptionInvalidCode();
				}
			}
			emc.beginTransaction(Person.class);
			person.setPassword(Crypto.encrypt(password, Config.token().getKey()));
			person.setChangePasswordTime(new Date());
			emc.check(person, CheckPersistType.all);
			emc.commit();
			Wo wo = new Wo();
			wo.setValue(true);
			result.setData(wo);
			return result;
		}
	}

	public static class Wi extends GsonPropertyObject {

		private String credential;
		private String password;
		private String codeAnswer;

		public String getCredential() {
			return credential;
		}

		public void setCredential(String credential) {
			this.credential = credential;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		public String getCodeAnswer() {
			return codeAnswer;
		}

		public void setCodeAnswer(String codeAnswer) {
			this.codeAnswer = codeAnswer;
		}

	}

	public static class Wo extends WrapBoolean {

	}

}