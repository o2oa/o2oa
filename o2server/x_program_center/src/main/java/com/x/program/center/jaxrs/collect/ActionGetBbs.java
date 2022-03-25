package com.x.program.center.jaxrs.collect;

import org.apache.commons.lang3.BooleanUtils;

import com.google.gson.Gson;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.connection.ActionResponse;
import com.x.base.core.project.connection.ConnectionAction;
import com.x.base.core.project.http.ActionResult;

class ActionGetBbs extends BaseAction {

	ActionResult<Wo> execute() throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		if (BooleanUtils.isNotTrue(this.connect())) {
			throw new ExceptionUnableConnect();
		}
		Gson gson = new Gson();
		String url = Config.collect()
				.url("/o2_collect_assemble/jaxrs/collect/config/key/(0)");
		ActionResponse resp = ConnectionAction.get(url, null);
		ResponsePojoVO os = gson.fromJson(resp.toString(), ResponsePojoVO.class);
		Wo wo = new Wo();
		if ("success".equals(os.getType())) {
			wo = os.getData();
		}
		result.setData(wo);
		return result;
	}

	public static class Wo  {
		private  String bbsUrlPath;
		private  String bbsUrl;

		public String getBbsUrlPath() {
			return bbsUrlPath;
		}

		public void setBbsUrlPath(String bbsUrlPath) {
			this.bbsUrlPath = bbsUrlPath;
		}

		public String getBbsUrl() {
			return bbsUrl;
		}

		public void setBbsUrl(String bbsUrl) {
			this.bbsUrl = bbsUrl;
		}
	}

	public class ResponsePojoVO {
		private String type;
		private  Wo data;
		private String message;
		private String date;

		public String getDate() {
			return date;
		}

		public void setDate(String date) {
			this.date = date;
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public Wo getData() {
			return data;
		}

		public void setData(Wo data) {
			this.data = data;
		}


	}

}
