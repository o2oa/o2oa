package com.x.program.center.jaxrs.collect;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.program.center.ThisApplication;
import com.x.program.center.schedule.CollectPerson;
import org.apache.commons.lang3.BooleanUtils;

class ActionValidate extends BaseAction {

	ActionResult<Wo> execute() throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		wo.setValue(true);
		if (!this.connect()) {
			wo.setValue(false);
		}
		if(BooleanUtils.isFalse(Config.collect().getEnable())){
			wo.setValue(false);
		}
		if (!this.validate(Config.collect().getName(), Config.collect().getPassword())) {
			wo.setValue(false);
		}
		if (BooleanUtils.isTrue(wo.getValue())) {
			/* 提交人员同步 */
			ThisApplication.context().scheduleLocal(CollectPerson.class);
		}
		result.setData(wo);
		return result;
	}

	public static class Wo extends WrapBoolean {

	}

}
