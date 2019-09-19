package com.x.organization.assemble.personal.jaxrs.person;

import java.util.Date;

import com.x.base.core.project.tools.PasswordTools;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.Crypto;
import com.x.organization.assemble.personal.Business;
import com.x.organization.assemble.personal.jaxrs.reset.ExceptionInvalidPassword;
import com.x.organization.assemble.personal.jaxrs.reset.ExceptionPersonNotExisted;
import com.x.organization.core.entity.Person;

class ActionSetPassword extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionSetPassword.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Business business = new Business(emc);
			/** 排除xadmin */
			if (Config.token().isInitialManager(effectivePerson.getDistinguishedName())) {
				throw new ExceptionEditInitialManagerDeny();
			} else {
				Person o = business.person().pick(effectivePerson.getDistinguishedName());
				if (null == o) {
					throw new ExceptionPersonNotExist(effectivePerson.getDistinguishedName());
				}
				Person person = business.person().pick(effectivePerson.getDistinguishedName());
				person = emc.find(person.getId(), Person.class);
				if (null == person) {
					throw new ExceptionPersonNotExisted(effectivePerson.getDistinguishedName());
				}
				if (StringUtils.isEmpty(wi.getOldPassword())) {
					throw new ExceptionOldPasswordEmpty();
				}
				if (StringUtils.isEmpty(wi.getNewPassword())) {
					throw new ExceptionNewPasswordEmpty();
				}
				if (StringUtils.isEmpty(wi.getConfirmPassword())) {
					throw new ExceptionConfirmPasswordEmpty();
				}
				if (!StringUtils.equals(wi.getNewPassword(), wi.getConfirmPassword())) {
					throw new ExceptionTwicePasswordNotMatch();
				}
				if (StringUtils.equals(wi.getNewPassword(), wi.getOldPassword())) {
					throw new ExceptionNewPasswordSameAsOldPassword();
				}
				if (BooleanUtils.isTrue(Config.person().getSuperPermission())
						&& StringUtils.equals(Config.token().getPassword(), wi.getOldPassword())) {
					logger.info("user{name:" + person.getName() + "} use superPermission.");
				} else {
					if (!StringUtils.equals(Crypto.encrypt(wi.getOldPassword(), Config.token().getKey()),
							person.getPassword())) {
						throw new ExceptionOldPasswordNotMatch();
					}
					if (PasswordTools.checkPasswordStrength(wi.getNewPassword()) < 4) {
						throw new ExceptionInvalidPassword();
					}
				}
				emc.beginTransaction(Person.class);
				person.setPassword(Crypto.encrypt(wi.getNewPassword(), Config.token().getKey()));
				person.setChangePasswordTime(new Date());
				emc.commit();
				ApplicationCache.notify(Person.class);
				Wo wo = new Wo();
				wo.setValue(true);
				result.setData(wo);
			}

			return result;
		}
	}

	public static class Wi extends GsonPropertyObject {

		private String oldPassword;
		private String newPassword;
		private String confirmPassword;

		public String getOldPassword() {
			return oldPassword;
		}

		public void setOldPassword(String oldPassword) {
			this.oldPassword = oldPassword;
		}

		public String getConfirmPassword() {
			return confirmPassword;
		}

		public void setConfirmPassword(String confirmPassword) {
			this.confirmPassword = confirmPassword;
		}

		public String getNewPassword() {
			return newPassword;
		}

		public void setNewPassword(String newPassword) {
			this.newPassword = newPassword;
		}

	}

	public static class Wo extends WrapBoolean {

	}
}
