package com.x.processplatform.assemble.surface.jaxrs.test;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.assemble.surface.Business;

class ActionTest3 extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionTest3.class);

	ActionResult<Object> execute(EffectivePerson effectivePerson) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Object> result = new ActionResult<>();
			Business business = new Business(emc);
			ScriptEngineManager factory = new ScriptEngineManager();
			ScriptEngine engine = factory.getEngineByName("nashorn");
			engine.put("org", business.organization());
			String eval = "org.listSupUnit('aaaaa', 'stringaaa');";
			engine.eval(eval);
			eval = "org.listSupUnit('aaaaa', 111);";
			engine.eval(eval);
			eval = "var l= new Array('a','b','c'); org.listSupUnit('aaaaa', l);";
			engine.eval(eval);
			return result;
		}
	}
}
