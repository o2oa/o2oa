package com.x.program.center.jaxrs.collect;

import com.x.base.core.http.ActionResult;
import com.x.base.core.http.WrapOutBoolean;

class ActionExist extends ActionBase {

	ActionResult<WrapOutBoolean> execute(String name) throws Exception {
		ActionResult<WrapOutBoolean> result = new ActionResult<>();
		WrapOutBoolean wrap = WrapOutBoolean.falseInstance();
		if (!this.connect()) {
			throw new UnableConnectException();
		}
		wrap.setValue(this.exist(name));
		result.setData(wrap);
		return result;
	}

}
