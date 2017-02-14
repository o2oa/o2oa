package com.x.collect.service.transmit.jaxrs.transmit;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.utils.StringTools;
import com.x.collect.service.transmit.task.CollectTask;

class ActionExecute extends ActionBase {
	ActionResult<WrapOutId> execute() throws Exception {
		ActionResult<WrapOutId> result = new ActionResult<>();
		WrapOutId wrap = new WrapOutId(StringTools.uniqueToken());
		Thread thread = new Thread(new CollectTask());
		thread.start();
		result.setData(wrap);
		return result;
	}
}
