package com.x.program.center.jaxrs.distribute;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.http.ActionResult;

class ActionAssemble extends ActionBase {

	ActionResult<Map<String, WrapOutAssemble>> execute(HttpServletRequest request, String source) throws Exception {
		ActionResult<Map<String, WrapOutAssemble>> result = new ActionResult<>();
		Map<String, WrapOutAssemble> wrap = this.getRandomAssembles(request, source);
		result.setData(wrap);
		return result;
	}

}