package com.x.processplatform.assemble.designer.jaxrs.script;

import java.util.List;

import com.x.base.core.http.ActionResult;
import com.x.processplatform.assemble.designer.wrapout.WrapOutScript;

class ActionListPrev extends ActionBase {
	ActionResult<List<WrapOutScript>> execute(String id, Integer count) throws Exception {
		ActionResult<List<WrapOutScript>> result = new ActionResult<>();
		result = this.standardListPrev(outCopier, id, count, "sequence", null, null, null, null, null, null, null, true,
				DESC);
		return result;
	}
}
