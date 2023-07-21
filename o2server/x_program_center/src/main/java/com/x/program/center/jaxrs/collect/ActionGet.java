package com.x.program.center.jaxrs.collect;

import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.config.Collect;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.http.ActionResult;

class ActionGet extends BaseAction {

	ActionResult<Wo> execute() throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = Wo.copier.copy(Config.collect());
		result.setData(wo);
		return result;
	}

	public static class Wo extends Collect {

		static WrapCopier<Collect, Wo> copier = WrapCopierFactory.wo(Collect.class, Wo.class, null, null);
	}

}
