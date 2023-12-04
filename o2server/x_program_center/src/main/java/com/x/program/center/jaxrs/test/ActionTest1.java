package com.x.program.center.jaxrs.test;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;

import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

class ActionTest1 extends BaseAction {

	private static final Logger LOGGER = LoggerFactory.getLogger(ActionTest1.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson) throws Exception {
		LOGGER.debug("execute:{}.", effectivePerson::getDistinguishedName);
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		try (Context context = Context.create()) {
			Value function = context.eval("js", "x => x+1");
			assert function.canExecute();
			int x = function.execute(41).asInt();
			wo.setValue(x);
		}
		result.setData(wo);
		return result;
	}

	public static class Wo extends GsonPropertyObject {

		private Object value;

		public Object getValue() {
			return value;
		}

		public void setValue(Object value) {
			this.value = value;
		}

		private static final long serialVersionUID = 1213098053102799729L;

	}
}