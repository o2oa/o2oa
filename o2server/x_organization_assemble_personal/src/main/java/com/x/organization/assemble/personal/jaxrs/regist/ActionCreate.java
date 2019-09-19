package com.x.organization.assemble.personal.jaxrs.regist;

import com.x.base.core.project.tools.PasswordTools;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.type.GenderType;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.StringTools;
import com.x.organization.assemble.personal.Business;
import com.x.organization.core.entity.Person;

class ActionCreate extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionCreate.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Business business = new Business(emc);
			String name = wi.getName();
			String password = wi.getPassword();
			String mobile = wi.getMobile();
			GenderType genderType = wi.getGenderType();
			String codeAnswer = wi.getCodeAnswer();
			String captcha = wi.getCaptcha();
			String captchaAnswer = wi.getCaptchaAnswer();
			if (StringUtils.equals(com.x.base.core.project.config.Person.REGISTER_TYPE_DISABLE,
					Config.person().getRegister())) {
				throw new ExceptionDisableRegist();
			}
			if (StringUtils.isEmpty(name) || (!StringTools.isSimply(name))) {
				throw new ExceptionInvalidName(name);
			}
			if (this.nameExisted(emc, name)) {
				throw new ExceptionNameExist(name);
			}
			if (!Config.person().isMobile(mobile)) {
				throw new ExceptionInvalidMobile(mobile);
			}
			if (this.mobileExisted(emc, mobile)) {
				throw new ExceptionMobileExist(mobile);
			}
			if (PasswordTools.checkPasswordStrength(password) < 4) {
				throw new ExceptionInvalidPassword();
			}
			if (null == genderType) {
				throw new ExceptionInvalidGenderType();
			}
			if (StringUtils.equals(com.x.base.core.project.config.Person.REGISTER_TYPE_CODE,
					Config.person().getRegister())) {
				if (BooleanUtils.isNotTrue(business.instrument().code().validate(mobile, codeAnswer))) {
					throw new ExceptionInvalidCode();
				}
			}
			if (StringUtils.equals(com.x.base.core.project.config.Person.REGISTER_TYPE_CAPTCHA,
					Config.person().getRegister())) {
				if (!business.instrument().captcha().validate(captcha, captchaAnswer)) {
					throw new ExceptionInvalidCaptcha();
				}
			}
			this.register(business, name, password, genderType, mobile);
			Wo wo = new Wo();
			wo.setValue(true);
			result.setData(wo);
			return result;
		}
	}

	private void register(Business business, String name, String password, GenderType genderType, String mobile)
			throws Exception {
		Person o = new Person();
		o.setName(name);
		business.person().setPassword(o, password);
		o.setGenderType(genderType);
		o.setMobile(mobile);
		business.entityManagerContainer().beginTransaction(Person.class);
		business.entityManagerContainer().persist(o, CheckPersistType.all);
		business.entityManagerContainer().commit();
	}

	public static class Wi extends GsonPropertyObject {

		private String name;

		private GenderType genderType;

		private String password;

		private String mobile;

		private String codeAnswer;

		private String captchaAnswer;

		private String captcha;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public GenderType getGenderType() {
			return genderType;
		}

		public void setGenderType(GenderType genderType) {
			this.genderType = genderType;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		public String getMobile() {
			return mobile;
		}

		public void setMobile(String mobile) {
			this.mobile = mobile;
		}

		public String getCodeAnswer() {
			return codeAnswer;
		}

		public void setCodeAnswer(String codeAnswer) {
			this.codeAnswer = codeAnswer;
		}

		public String getCaptchaAnswer() {
			return captchaAnswer;
		}

		public void setCaptchaAnswer(String captchaAnswer) {
			this.captchaAnswer = captchaAnswer;
		}

		public String getCaptcha() {
			return captcha;
		}

		public void setCaptcha(String captcha) {
			this.captcha = captcha;
		}

	}

	public static class Wo extends WrapBoolean {
	}

}