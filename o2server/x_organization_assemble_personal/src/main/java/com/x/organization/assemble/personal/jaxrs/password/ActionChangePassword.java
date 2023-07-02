package com.x.organization.assemble.personal.jaxrs.password;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.exception.ExceptionPersonNotExist;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.Crypto;
import com.x.organization.assemble.personal.Business;
import com.x.organization.core.entity.Person;

class ActionChangePassword extends ActionBase {

	private static Logger logger = LoggerFactory.getLogger(ActionChangePassword.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		/* 管理员不可以修改密码 */
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Business business = new Business(emc);
			if (Config.token().isInitialManager(effectivePerson.getDistinguishedName())) {
				throw new ExceptionEditInitialManagerDeny();
			}
			Person person = business.person().pick(effectivePerson.getDistinguishedName());
			person = emc.find(person.getId(), Person.class);
			if (null == person) {
				throw new ExceptionPersonNotExist(effectivePerson.getDistinguishedName());
			}
			if (StringUtils.isEmpty(wi.getOldPassword())) {
				throw new ExceptionOldPasswordEmpty();
			}
			if (StringUtils.isEmpty(wi.getNewPassword())) {
				throw new ExceptionNewPasswordEmpty();
			}
			if (StringUtils.isEmpty(wi.getConfirmPassword())) {
				throw new ConfirmPasswordEmptyException();
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
				if (!StringUtils.equals(
						Crypto.encrypt(wi.getOldPassword(), Config.token().getKey(), Config.person().getEncryptType()),
						person.getPassword())) {
					throw new ExceptionOldPasswordNotMatch();
				}
				if (!wi.getNewPassword().matches(Config.person().getPasswordRegex())) {
					throw new ExceptionInvalidPassword(Config.person().getPasswordRegexHint());
				}

			}
			emc.beginTransaction(Person.class);
			business.person().setPassword(person, wi.getNewPassword());
			emc.commit();
			Wo wo = new Wo();
			wo.setValue(true);
			result.setData(wo);
			return result;
		}
	}

	public static class Wo extends WrapBoolean {
	}

	public static class Wi extends GsonPropertyObject {

		@FieldDescribe("原密码")
		private String oldPassword;

		@FieldDescribe("新密码")
		private String newPassword;

		@FieldDescribe("确认新密码")
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

}
