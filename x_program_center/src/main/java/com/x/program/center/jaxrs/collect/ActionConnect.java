package com.x.program.center.jaxrs.collect;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.WrapOutBoolean;

class ActionConnect extends ActionBase {

	ActionResult<WrapOutBoolean> execute() throws Exception {
		ActionResult<WrapOutBoolean> result = new ActionResult<>();
		WrapOutBoolean wrap = new WrapOutBoolean();
		if (!this.connect()) {
			throw new UnableConnectException();
		}
		wrap.setValue(true);
		result.setData(wrap);
		return result;
	}

}
