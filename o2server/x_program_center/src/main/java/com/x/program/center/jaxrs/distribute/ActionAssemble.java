package com.x.program.center.jaxrs.distribute;

import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.tools.Crypto;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

class ActionAssemble extends BaseAction {

	ActionResult<Wo> execute(HttpServletRequest request, String source) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Map<String, WoAssemble> o = this.getRandomAssembles(request, source);
		result.setData(new Wo(Crypto.encodeAES(XGsonBuilder.toJson(o), Crypto.DESCRIBE_AES_KEY)));
		return result;
	}

	public static class Wo extends GsonPropertyObject {

		private static final long serialVersionUID = 6772463745917569315L;

		public Wo (String data) {
			this.data = data;
		}

		@Schema(description = "数据.")
		private String data;

		public String getData() {
			return data;
		}

		public void setData(String data) {
			this.data = data;
		}
	}

}
