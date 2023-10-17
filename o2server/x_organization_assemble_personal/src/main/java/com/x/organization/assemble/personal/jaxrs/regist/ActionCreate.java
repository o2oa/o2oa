package com.x.organization.assemble.personal.jaxrs.regist;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.type.GenderType;
import com.x.base.core.project.x_organization_assemble_control;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.StringTools;
import com.x.organization.assemble.personal.Business;
import com.x.organization.assemble.personal.ThisApplication;
import com.x.organization.core.entity.Person;

class ActionCreate extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionCreate.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		Person person;
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			String name = wi.getName();
			String passwd = wi.getPassword();
			String mobile = wi.getMobile();
			GenderType genderType = wi.getGenderType();
			String codeAnswer = wi.getCodeAnswer();
			String captcha = wi.getCaptcha();
			String captchaAnswer = wi.getCaptchaAnswer();
			String mail = wi.getMail();
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
			if(StringUtils.isNotBlank(mail)){
				if (!StringTools.isMail(mail)) {
					throw new ExceptionInvalidMail(mail);
				}
				if(this.mailExisted(emc, mail)){
					throw new ExceptionMailExist(mail);
				}
			}
			if (!passwd.matches(Config.person().getPasswordRegex())) {
				throw new ExceptionInvalidPassword(Config.person().getPasswordRegexHint());
			}
			if (null == genderType) {
				genderType = GenderType.d;
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
			if(StringUtils.isNotBlank(wi.getUnit()) && business.unit().pick(wi.getUnit()) == null){
				throw new ExceptionInvalidUnit(wi.getUnit());
			}
			person = this.register(business, name, passwd, genderType, mobile, mail);
		}
		if(StringUtils.isNotBlank(wi.getUnit())){
			this.createIdentity(person, wi.getUnit());
		}
		Wo wo = new Wo();
		wo.setValue(true);
		result.setData(wo);
		return result;
	}

	private Person register(Business business, String name, String password, GenderType genderType, String mobile, String mail)
			throws Exception {
		Person o = new Person();
		o.setName(name);
		business.person().setPassword(o, password);
		o.setGenderType(genderType);
		o.setMobile(mobile);
		o.setMail(mail);
		business.entityManagerContainer().beginTransaction(Person.class);
		business.entityManagerContainer().persist(o, CheckPersistType.all);
		business.entityManagerContainer().commit();
		return o;
	}

	private void createIdentity(Person person, String unit) throws Exception {
		WrapIdentity identity = new WrapIdentity();
		identity.setName(person.getName());
		identity.setPerson(person.getId());
		identity.setUnit(unit);
		ThisApplication.context().applications().postQuery(x_organization_assemble_control.class,
				"identity", identity);
	}

	private static class WrapIdentity extends GsonPropertyObject {
		private String person;
		private String name;
		private String unit;

		public String getPerson() {
			return person;
		}

		public void setPerson(String person) {
			this.person = person;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getUnit() {
			return unit;
		}

		public void setUnit(String unit) {
			this.unit = unit;
		}
	}

	public static class Wi extends GsonPropertyObject {

		@FieldDescribe("用户名称.")
		private String name;

		@FieldDescribe("性别.男:m,女:f,未知:d")
		private GenderType genderType;

		@FieldDescribe("密码.")
		private String password;

		@FieldDescribe("必填,手机号.")
		private String mobile;

		@FieldDescribe("手机认证码")
		private String codeAnswer;

		@FieldDescribe("图片认证码")
		private String captchaAnswer;

		@FieldDescribe("图片认证编号")
		private String captcha;

		@FieldDescribe("邮件地址(非必填).")
		private String mail;

		@FieldDescribe("归属组织(非必填).")
		private String unit;

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

		public String getMail() {
			return mail;
		}

		public void setMail(String mail) {
			this.mail = mail;
		}

		public String getUnit() {
			return unit;
		}

		public void setUnit(String unit) {
			this.unit = unit;
		}
	}

	public static class Wo extends WrapBoolean {
	}

}
