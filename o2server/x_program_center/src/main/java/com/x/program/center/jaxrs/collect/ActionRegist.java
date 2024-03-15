package com.x.program.center.jaxrs.collect;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.config.Collect;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.Person;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.tools.StringTools;
import com.x.program.center.ThisApplication;
import com.x.program.center.schedule.CollectPerson;

class ActionRegist extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		if (BooleanUtils.isNotTrue(Config.general().getConfigApiEnable())) {
			throw new ExceptionModifyConfig();
		}
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		String name = wi.getName();
		String password = wi.getPassword();
		String mobile = wi.getMobile();
		String codeAnswer = wi.getCodeAnswer();
		String mail = wi.getMail();
		if (BooleanUtils.isNotTrue(this.connect())) {
			throw new ExceptionUnableConnect();
		}
		if (BooleanUtils.isTrue(this.exist(name))) {
			throw new ExceptionNameExist(name);
		}
		if (!password.matches(Person.DEFAULT_PASSWORDREGEX)) {
			throw new ExceptionInvalidPassword(Person.DEFAULT_PASSWORDREGEXHINT);
		}
		if (!Config.person().isMobile(mobile)) {
			throw new ExceptionInvalidMobile(mobile);
		}
		if (StringUtils.isEmpty(codeAnswer)) {
			throw new CodeAnswerEmptyException();
		}
		if (StringUtils.isEmpty(mail)) {
			throw new ExceptionEmailEmpty();
		}
		if (!StringTools.isMail(mail)) {
			throw new ExceptionInvalidMail(mail);
		}
		Wo wo = new Wo();
		wo.setValue(this.regist(name, password, mobile, codeAnswer, mail));
		if (BooleanUtils.isTrue(wo.getValue())) {
			Config.collect().setEnable(true);
			Config.collect().setName(name);
			Config.collect().setPassword(password);
			Config.collect().save();
			this.configFlush(effectivePerson);
			// 人员和应用市场同步
			ThisApplication.context().scheduleLocal(CollectPerson.class);
		}
		result.setData(wo);
		return result;
	}

	public static class Wi extends Collect {

		static WrapCopier<Wi, Collect> copier = WrapCopierFactory.wi(Wi.class, Collect.class, null, null);

		@FieldDescribe("手机号码")
		private String mobile;
		@FieldDescribe("验证码")
		private String codeAnswer;
		@FieldDescribe("邮件地址")
		private String mail;

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

		public String getMail() {
			return mail;
		}

		public void setMail(String mail) {
			this.mail = mail;
		}
	}

	public static class Wo extends WrapBoolean {

		private static final long serialVersionUID = -2720412587260857928L;
		
	}

}
