package com.x.query.service.processing.jaxrs.test;

import javax.script.ScriptEngine;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.script.ScriptFactory;

class ActionGroup2 extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionGroup2.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			ScriptEngine engine = ScriptFactory.newScriptEngine();
			Object o = engine.eval("this.data && this.data.length");
			System.out.println(o.getClass());
			return result;
		}
	}

	public static class Wo extends WrapBoolean {
	}

}