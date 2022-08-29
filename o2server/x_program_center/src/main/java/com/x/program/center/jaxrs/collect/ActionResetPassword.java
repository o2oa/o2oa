package com.x.program.center.jaxrs.collect;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.config.Collect;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.Person;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;

class ActionResetPassword extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		ActionResult<Wo> result = new ActionResult<>();
		if (BooleanUtils.isNotTrue(Config.general().getConfigApiEnable())) {
			throw new ExceptionModifyConfig();
		}
		String name = wi.getName();
		String password = wi.getPassword();
		String mobile = wi.getMobile();
		String codeAnswer = wi.getCodeAnswer();
		if (BooleanUtils.isNotTrue(this.connect())) {
			throw new ExceptionUnableConnect();
		}
		if (BooleanUtils.isNotTrue(this.exist(name))) {
			throw new ExceptionNameNotExist(name);
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
		Wo wo = new Wo();
		wo.setValue(this.password(name, password, mobile, codeAnswer));
		if (BooleanUtils.isTrue(wo.getValue())) {
			Config.collect().setPassword(password);
			Config.collect().save();
			this.configFlush(effectivePerson);
		}
		result.setData(wo);
		return result;
	}

	public static class Wi extends Collect {

		private static final long serialVersionUID = 5230110648080511863L;

		static WrapCopier<Wi, Collect> copier = WrapCopierFactory.wi(Wi.class, Collect.class, null, null);

		private String mobile;

		private String codeAnswer;

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
	}

	public static class Wo extends WrapBoolean {

		private static final long serialVersionUID = -7763796793681024809L;
		
	}
}
