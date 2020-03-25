package com.x.program.center.jaxrs.distribute;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.http.ActionResult;

class ActionAssemble extends BaseAction {

	ActionResult<Wo> execute(HttpServletRequest request, String source) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Map<String, WoAssemble> o = this.getRandomAssembles(request, source);
		Wo wo = new Wo(o);
		result.setData(wo);
		return result;
	}

	public static class Wo extends LinkedHashMap<String, WoAssemble> {

		private static final long serialVersionUID = 3880748824112856592L;

		public Wo(Map<String, WoAssemble> o) {
			super(o);
		}
	}

}