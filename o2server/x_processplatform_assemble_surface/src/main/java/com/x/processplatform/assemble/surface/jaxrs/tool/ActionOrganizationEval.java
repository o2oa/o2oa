package com.x.processplatform.assemble.surface.jaxrs.tool;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import com.google.gson.JsonElement;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.assemble.surface.Business;

class ActionOrganizationEval extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionOrganizationEval.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson, JsonElement jsonElement) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Wi wi = this.convertToWrapIn(jsonElement, Wi.class);
			Wo wo = new Wo();
			Business business = new Business(emc);
			ScriptEngineManager factory = new ScriptEngineManager();
			ScriptEngine engine = factory.getEngineByName(Config.SCRIPTING_ENGINE_NAME);
			engine.put("org", business.organization());
			engine.put("effectivePerson", effectivePerson);
			logger.warn("eval script: {}", wi.getScript());
			Object o = engine.eval(wi.getScript());
			wo.setValue(XGsonBuilder.toJson(o));
			result.setData(wo);
			return result;
		}
	}

	public static class Wi extends GsonPropertyObject {

		@FieldDescribe("执行脚本")
		private String script;

		public String getScript() {
			return script;
		}

		public void setScript(String script) {
			this.script = script;
		}
	}

	public static class Wo extends GsonPropertyObject {

		@FieldDescribe("执行结果")
		private String value;

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}
	}

}
