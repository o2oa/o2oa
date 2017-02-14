package com.x.processplatform.assemble.designer.jaxrs.queryview;

import java.util.List;

import com.x.base.core.http.ActionResult;
import com.x.processplatform.assemble.designer.wrapout.WrapOutQueryView;

class ActionListPrev extends ActionBase {
	ActionResult<List<WrapOutQueryView>> execute(String id, Integer count) throws Exception {
		ActionResult<List<WrapOutQueryView>> result = new ActionResult<>();
		result = this.standardListPrev(outCopier, id, count, "sequence", null, null, null, null, null, null, null, true,
				DESC);
		return result;
	}
}
