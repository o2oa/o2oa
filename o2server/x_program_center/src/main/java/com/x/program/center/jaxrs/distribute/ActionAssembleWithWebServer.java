package com.x.program.center.jaxrs.distribute;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;

class ActionAssembleWithWebServer extends BaseAction {

	ActionResult<Wo> execute(HttpServletRequest request, String source) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		wo.setWebServer(this.getRandomWebServer(request, source));
		wo.setAssembles(this.getRandomAssembles(request, source));
		result.setData(wo);
		return result;
	}

	public static class Wo extends GsonPropertyObject {

		@FieldDescribe("webServer")
		public WoWebServer webServer;

		@FieldDescribe("assembles")
		public Map<String, WoAssemble> assembles;

		public WoWebServer getWebServer() {
			return webServer;
		}

		public void setWebServer(WoWebServer webServer) {
			this.webServer = webServer;
		}

		public Map<String, WoAssemble> getAssembles() {
			return assembles;
		}

		public void setAssembles(Map<String, WoAssemble> assembles) {
			this.assembles = assembles;
		}
	}

}
