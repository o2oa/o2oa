package com.x.program.center.jaxrs.center;

import java.util.Date;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

class ActionVersion extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionVersion.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = XGsonBuilder.instance().fromJson(Config.version(), Wo.class);
		result.setData(wo);
		return result;
	}

	public static class Wo extends GsonPropertyObject {

		private String version;

		private Date date;

		public String getVersion() {
			return version;
		}

		public void setVersion(String version) {
			this.version = version;
		}

		public Date getDate() {
			return date;
		}

		public void setDate(Date date) {
			this.date = date;
		}

	}

}