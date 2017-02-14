package com.x.program.center.jaxrs.distribute;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.http.ActionResult;

class ActionAssembleWithWebServer extends ActionBase {

	ActionResult<Map<String, Object>> execute(HttpServletRequest request, String source) throws Exception {
		ActionResult<Map<String, Object>> result = new ActionResult<>();
		Map<String, Object> wrap = new HashMap<>();
		wrap.put("webServer", this.getRandomWebServer(request, source));
		wrap.put("assembles", this.getRandomAssembles(request, source));
		result.setData(wrap);
		return result;
	}

}
