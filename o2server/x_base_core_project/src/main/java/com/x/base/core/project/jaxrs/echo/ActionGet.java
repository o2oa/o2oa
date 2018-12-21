package com.x.base.core.project.jaxrs.echo;

import java.util.Date;

import javax.servlet.ServletContext;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

class ActionGet extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionGet.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, ServletContext servletContext) throws Exception {
		logger.debug(effectivePerson, "echo from:{}.", effectivePerson);
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		wo.setServletContextName(servletContext.getServletContextName());
		wo.setServerTime(new Date());
		result.setData(wo);
		return result;
	}

	public static class Wo extends GsonPropertyObject {

		@FieldDescribe("上下文根")
		private String servletContextName;

		@FieldDescribe("服务器时间")
		private Date serverTime;

		public String getServletContextName() {
			return servletContextName;
		}

		public void setServletContextName(String servletContextName) {
			this.servletContextName = servletContextName;
		}

		public Date getServerTime() {
			return serverTime;
		}

		public void setServerTime(Date serverTime) {
			this.serverTime = serverTime;
		}

	}

}