package com.x.organization.assemble.authentication.jaxrs.authentication;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.WrapOutMap;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.base.core.project.server.Config;

class ActionMode extends ActionBase {
	
	private static Logger logger = LoggerFactory.getLogger(ActionMode.class);

	ActionResult<WrapOutMap> execute() throws Exception {
		ActionResult<WrapOutMap> result = new ActionResult<>();
		WrapOutMap wrap = new WrapOutMap();
		if (BooleanUtils.isTrue(Config.person().getCodeLogin())) {
			wrap.put("codeLogin", true);
		} else {
			wrap.put("codeLogin", false);
		}
		if (BooleanUtils.isTrue(Config.person().getBindLogin())) {
			wrap.put("bindLogin", true);
		} else {
			wrap.put("bindLogin", false);
		}
		result.setData(wrap);
		return result;
	}

}