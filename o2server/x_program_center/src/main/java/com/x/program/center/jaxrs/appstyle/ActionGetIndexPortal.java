package com.x.program.center.jaxrs.appstyle;

import com.x.base.core.project.config.AppStyle;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapString;

class ActionGetIndexPortal extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		AppStyle appStyle = Config.appStyle();
		Wo wo = new Wo();
		wo.setValue(appStyle.getIndexPortal());
		result.setData(wo);
		return result;
	}

	public static class Wo extends WrapString {

	}

}
