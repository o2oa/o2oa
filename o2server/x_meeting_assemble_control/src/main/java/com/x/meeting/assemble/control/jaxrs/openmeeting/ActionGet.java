package com.x.meeting.assemble.control.jaxrs.openmeeting;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;

public class ActionGet extends BaseAction {

	public ActionResult<Wo> execute() throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		wo.setEnable(Config.meeting().getEnable());
		/* 如果启用了再加载 */
		if (BooleanUtils.isTrue(Config.meeting().getEnable())) {
			wo.setHost(Config.meeting().getHost());
			wo.setPort(Config.meeting().getPort());
			wo.setHttpProtocol(Config.meeting().getHttpProtocol());
			wo.setOauth2Id(Config.meeting().getOauth2Id());
		}
		result.setData(wo);
		return result;
	}

	public static class Wo extends GsonPropertyObject {

		private Boolean enable;
		private String host;
		private Integer port;
		private String oauth2Id;
		private String httpProtocol;

		public String getHost() {
			return host;
		}

		public void setHost(String host) {
			this.host = host;
		}

		public Integer getPort() {
			return port;
		}

		public void setPort(Integer port) {
			this.port = port;
		}

		public String getOauth2Id() {
			return oauth2Id;
		}

		public void setOauth2Id(String oauth2Id) {
			this.oauth2Id = oauth2Id;
		}

		public Boolean getEnable() {
			return enable;
		}

		public void setEnable(Boolean enable) {
			this.enable = enable;
		}

		public String getHttpProtocol() {
			return httpProtocol;
		}

		public void setHttpProtocol(String httpProtocol) {
			this.httpProtocol = httpProtocol;
		}

	}

}
