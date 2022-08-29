package com.x.organization.assemble.personal.jaxrs.person;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.cache.CacheManager;
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
import com.x.organization.assemble.personal.jaxrs.reset.ExceptionInvalidPassword;
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
					throw new ExceptionPersonNotExist(effectivePerson.getDistinguishedName());
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

				String oldPassword = wi.getOldPassword();
				String newPassword = wi.getNewPassword();
				String confirmPassword = wi.getConfirmPassword();
				String isEncrypted = wi.getIsEncrypted();

				if (!StringUtils.isEmpty(isEncrypted)) {
					if (isEncrypted.trim().equalsIgnoreCase("y")) {
						oldPassword = this.decryptRSA(oldPassword);
						newPassword = this.decryptRSA(newPassword);
						confirmPassword = this.decryptRSA(confirmPassword);
					}
				}

				if (!StringUtils.equals(newPassword, confirmPassword)) {
					throw new ExceptionTwicePasswordNotMatch();
				}
				if (StringUtils.equals(newPassword, oldPassword)) {
					throw new ExceptionNewPasswordSameAsOldPassword();
				}

				if (BooleanUtils.isTrue(Config.person().getSuperPermission())
						&& StringUtils.equals(Config.token().getPassword(), oldPassword)) {
					logger.info("user{name:" + person.getName() + "} use superPermission.");
				} else {
					if (!StringUtils.equals(
							Crypto.encrypt(oldPassword, Config.token().getKey(), Config.person().getEncryptType()),
							person.getPassword())) {
						throw new ExceptionOldPasswordNotMatch();
					}
					if (!newPassword.matches(Config.person().getPasswordRegex())) {
						throw new ExceptionInvalidPassword(Config.person().getPasswordRegexHint());
					}
				}

				emc.beginTransaction(Person.class);
				business.person().setPassword(person, newPassword);
				emc.commit();
				CacheManager.notify(Person.class);
				Wo wo = new Wo();
				wo.setValue(true);
				result.setData(wo);
			}

			return result;
		}
	}

	public String decryptRSA(String strDecrypt) {
		String privateKey;
		String decrypt = null;
		try {
			privateKey = getPrivateKey();
			decrypt = Crypto.rsaDecrypt(strDecrypt, privateKey);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return decrypt;
	}

	public String getPrivateKey() {
		String privateKey = "";
		try {
			privateKey = Config.privateKey();
			byte[] privateKeyB = Base64.decodeBase64(privateKey);
			privateKey = new String(Base64.encodeBase64(privateKeyB));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return privateKey;
	}

	public static class Wi extends GsonPropertyObject {

		private static final long serialVersionUID = 1L;

		@FieldDescribe("原密码")
		private String oldPassword;

		@FieldDescribe("新密码")
		private String newPassword;

		@FieldDescribe("确认新密码")
		private String confirmPassword;

		@FieldDescribe("是否启用加密,默认不加密,启用(y)。注意:使用加密先要在服务器运行 create encrypt key")
		private String isEncrypted;

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

		public String getIsEncrypted() {
			return isEncrypted;
		}

		public void setIsEncrypted(String isEncrypted) {
			this.isEncrypted = isEncrypted;
		}
	}

	public static class Wo extends WrapBoolean {

	}
}
