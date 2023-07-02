package com.x.program.center.jaxrs.jest;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.JsonObject;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.tools.BaseTools;

class ActionVersion extends BaseAction {
	ActionResult<Wo> execute(HttpServletRequest request, String source) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		String text = BaseTools.readString(Config.PATH_VERSION);
		if (XGsonBuilder.isJsonObject(text)) {
			JsonObject obj = XGsonBuilder.instance().fromJson(text, JsonObject.class);
			 wo.setVersion(obj.get("version").getAsString());
			 wo.setDate(obj.get("date").getAsString());
		}
		result.setData(wo);
		return result;
	}

	public static class Wo extends GsonPropertyObject {
		
		private String version;
        private String date;
        
		public String getVersion() {
			return version;
		}

		public void setVersion(String version) {
			this.version = version;
		}

		public String getDate() {
			return date;
		}

		public void setDate(String date) {
			this.date = date;
		}

	}

}