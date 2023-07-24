package com.x.program.center.jaxrs.collect;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.jaxrs.WrapBoolean;

class ActionValidateCodeAnswer extends BaseAction {

	ActionResult<Wo> execute(JsonElement jsonElement) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		Wo wo = new Wo();
		wo.setValue(true);
		String mobile = wi.getMobile();
		String codeAnswer = wi.getCodeAnswer();
		if (!Config.person().isMobile(mobile)) {
			throw new ExceptionInvalidMobile(mobile);
		}
		if (StringUtils.isEmpty(codeAnswer)) {
			throw new CodeAnswerEmptyException();
		}
		if (BooleanUtils.isNotTrue(this.connect())) {
			throw new ExceptionUnableConnect();
		}
		if (BooleanUtils.isNotTrue(this.validateCodeAnswer(mobile, codeAnswer))) {
			wo.setValue(false);
		}
		result.setData(wo);
		return result;
	}

	public static class Wi extends GsonPropertyObject {

		private static final long serialVersionUID = -2372766347222201401L;

		@FieldDescribe("手机号码")
		private String mobile;

		@FieldDescribe("短信认证码")
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
	}

}
