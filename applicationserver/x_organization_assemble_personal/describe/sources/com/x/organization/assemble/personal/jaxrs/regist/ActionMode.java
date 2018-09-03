package com.x.organization.assemble.personal.jaxrs.regist;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.WrapOutString;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

class ActionMode extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionMode.class);

	ActionResult<WrapOutString> execute() throws Exception {
		ActionResult<WrapOutString> result = new ActionResult<>();
		WrapOutString wrap = new WrapOutString();
		wrap.setValue(Config.person().getRegister());
		result.setData(wrap);
		return result;
	}

}
