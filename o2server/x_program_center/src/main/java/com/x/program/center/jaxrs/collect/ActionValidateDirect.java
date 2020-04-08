package com.x.program.center.jaxrs.collect;

import com.x.base.core.project.config.Config;
import com.x.program.center.ThisApplication;
import com.x.program.center.schedule.CollectPerson;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.project.config.Collect;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.jaxrs.WrapBoolean;

class ActionValidateDirect extends BaseAction {

	ActionResult<Wo> execute(JsonElement jsonElement) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		Wo wo = new Wo();
		wo.setValue(true);
		if (!this.connect()) {
			throw new ExceptionUnableConnect();
		}
		String name = wi.getName();
		String password = wi.getPassword();
		if (StringUtils.isEmpty(name)) {
			throw new ExceptionNameEmpty();
		}
		if (StringUtils.isEmpty(password)) {
			throw new ExceptionPasswordEmpty();
		}
		if (!this.validate(name, password)) {
			wo.setValue(false);
		}
		if (BooleanUtils.isTrue(wo.getValue())) {
			Config.collect().setEnable(true);
			Config.collect().setName(name);
			Config.collect().setPassword(password);
			Config.collect().save();
			Config.flush();
			/* 提交人员人员同步 */
			ThisApplication.context().scheduleLocal(CollectPerson.class);
		}
		result.setData(wo);
		return result;
	}

	public static class Wi extends Collect {

	}

	public static class Wo extends WrapBoolean {
	}

}
