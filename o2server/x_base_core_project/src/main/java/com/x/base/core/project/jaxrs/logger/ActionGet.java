package com.x.base.core.project.jaxrs.logger;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapString;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

class ActionGet extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(ActionGet.class);

	ActionResult<Wo> execute(EffectivePerson effectivePerson) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		result.setData(new Wo(LoggerFactory.getLevel()));
		return result;
	}

	public static class Wo extends WrapString {

		public Wo(String level) {
			super(level);
		}

	}

}