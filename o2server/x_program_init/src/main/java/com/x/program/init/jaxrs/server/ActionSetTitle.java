package com.x.program.init.jaxrs.server;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.program.init.MissionSetSecret;
import com.x.program.init.ThisApplication;
import org.apache.commons.lang3.StringUtils;

class ActionSetTitle extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionSetTitle.class);

	public ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);
		ActionResult<Wo> result = new ActionResult<>();
		Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
		if(StringUtils.isNotBlank(wi.getTitle())){
			Config.collect().setTitle(wi.getTitle());
			Config.collect().save();
			Config.flush();
			Config.resource_commandQueue().add("ctl -flushConfig");
			Thread.sleep(500);
		}
		Wo wo = new Wo();
		wo.setValue(true);
		result.setData(wo);
		return result;
	}

	public static class Wo extends WrapBoolean {

		private static final long serialVersionUID = 7892218945591687635L;

	}

	public static class Wi extends GsonPropertyObject {

		private static final long serialVersionUID = -5726130517002102825L;

		@FieldDescribe("标题.")
		private String title;

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}
	}

}
