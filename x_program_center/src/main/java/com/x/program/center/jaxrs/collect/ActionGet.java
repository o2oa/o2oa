package com.x.program.center.jaxrs.collect;

import com.x.base.core.http.ActionResult;
import com.x.base.core.project.server.Config;
import com.x.program.center.jaxrs.collect.wrapout.WrapOutCollect;

class ActionGet extends ActionBase {

	ActionResult<WrapOutCollect> execute() throws Exception {
		ActionResult<WrapOutCollect> result = new ActionResult<>();
		WrapOutCollect wrap = outCopier.copy(Config.collect());
		result.setData(wrap);
		return result;
	}

}
