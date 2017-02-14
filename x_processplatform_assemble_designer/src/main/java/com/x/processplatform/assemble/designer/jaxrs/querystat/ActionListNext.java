package com.x.processplatform.assemble.designer.jaxrs.querystat;

import java.util.List;

import com.x.base.core.http.ActionResult;
import com.x.processplatform.assemble.designer.wrapout.WrapOutQueryStat;

class ActionListNext extends ActionBase {
	ActionResult<List<WrapOutQueryStat>> execute(String id, Integer count) throws Exception {
		ActionResult<List<WrapOutQueryStat>> result = new ActionResult<>();
		result = this.standardListNext(outCopier, id, count, "sequence", null, null, null, null, null, null, null, true,
				DESC);
		return result;
	}
}
