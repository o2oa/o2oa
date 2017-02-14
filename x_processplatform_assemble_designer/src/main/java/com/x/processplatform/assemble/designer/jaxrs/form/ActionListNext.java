package com.x.processplatform.assemble.designer.jaxrs.form;

import java.util.List;

import com.x.base.core.http.ActionResult;
import com.x.processplatform.assemble.designer.wrapout.WrapOutFormSimple;

class ActionListNext extends ActionBase {
	ActionResult<List<WrapOutFormSimple>> execute(String id, Integer count) throws Exception {
		ActionResult<List<WrapOutFormSimple>> result = new ActionResult<>();
		result = this.standardListNext(simpleOutCopier, id, count, "sequence", null, null, null, null, null, null, null,
				true, DESC);
		return result;
	}
}
