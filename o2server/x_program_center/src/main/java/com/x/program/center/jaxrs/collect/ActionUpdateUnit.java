package com.x.program.center.jaxrs.collect;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.config.Collect;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

class ActionUpdateUnit extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		ActionResult<Wo> result = new ActionResult<>();
		String name = wi.getName();
		String mobile = wi.getMobile();
		String codeAnswer = wi.getCodeAnswer();
		String newName = wi.getNewName();
		if (!this.connect()) {
			throw new ExceptionUnableConnect();
		}
		if (!this.exist(name)) {
			throw new ExceptionNameNotExist(name);
		}
		if (!Config.person().isMobile(mobile)) {
			throw new ExceptionInvalidMobile(mobile);
		}
		if (StringUtils.isEmpty(codeAnswer)) {
			throw new CodeAnswerEmptyException();
		}
		if (StringUtils.isEmpty(newName)) {
			throw new ExceptionNameEmpty();
		}
		Wo wo = new Wo();
		wo.setValue(this.update(name, newName, mobile, codeAnswer, wi.getKey(), wi.getSecret()));
		if (BooleanUtils.isTrue(wo.getValue()) && name.equals(Config.collect().getName())) {
			Config.collect().setName(newName);
			Config.collect().setKey(wi.getKey());
			Config.collect().setSecret(wi.getSecret());
			Config.collect().save();
			Config.flush();
		}
		result.setData(wo);
		return result;
	}

	public static class Wi extends Collect {

		static WrapCopier<Wi, Collect> copier = WrapCopierFactory.wi(Wi.class, Collect.class, null, null);

		@FieldDescribe("手机号码.")
		private String mobile;
		@FieldDescribe("验证码.")
		private String codeAnswer;
		@FieldDescribe("新组织名称.")
		private String newName;

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

		public String getNewName() {
			return newName;
		}

		public void setNewName(String newName) {
			this.newName = newName;
		}
	}

	public static class Wo extends WrapBoolean {
	}
}
