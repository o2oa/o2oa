package com.x.organization.assemble.personal.jaxrs.regist;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.WrapOutString;
import com.x.base.core.project.server.Config;

class ActionMode extends ActionBase {
	
	private static Logger logger = LoggerFactory.getLogger(ActionMode.class);

	ActionResult<WrapOutString> execute() throws Exception {
		ActionResult<WrapOutString> result = new ActionResult<>();
		WrapOutString wrap = new WrapOutString();
		wrap.setValue(Config.person().getRegister());
		result.setData(wrap);
		return result;
	}

}
