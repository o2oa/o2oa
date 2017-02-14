package com.x.cms.assemble.control.jaxrs.queryviewdesign;

import java.util.List;

import com.x.base.core.http.ActionResult;

class ActionListNext extends ActionBase {
	ActionResult<List<WrapOutQueryView>> execute(String id, Integer count) throws Exception {
		ActionResult<List<WrapOutQueryView>> result = new ActionResult<>();
		result = this.standardListNext(outCopier, id, count, "sequence", null, null, null, null, null, null, null,
				true, DESC);
		return result;
	}
}
