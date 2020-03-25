package com.x.program.center.jaxrs.collect;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

class ActionDelete extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson, String name, String mobile, String codeAnswer) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
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
		Wo wo = new Wo();
		wo.setValue(this.delete(name, mobile, codeAnswer));
		if (BooleanUtils.isTrue(wo.getValue()) && name.equals(Config.collect().getName())) {
			Config.collect().setEnable(false);
			Config.collect().setName("");
			Config.collect().setPassword("");
			Config.collect().setKey(null);
			Config.collect().setSecret(null);
			Config.collect().save();
			Config.flush();
		}
		result.setData(wo);
		return result;
	}

	public static class Wo extends WrapBoolean {
	}
}
