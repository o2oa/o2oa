package com.x.program.init.jaxrs.server;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

class ActionGetTitle extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionGetTitle.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson) throws Exception{

		ActionResult<Wo> result = new ActionResult<>();
		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);
		Wo wo = new Wo();
		wo.setTitle(Config.collect().getTitle());
		result.setData(wo);
		return result;
	}

	public static class Wo extends GsonPropertyObject {
		@FieldDescribe("系统标题.")
		private String title;

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}
	}

}
