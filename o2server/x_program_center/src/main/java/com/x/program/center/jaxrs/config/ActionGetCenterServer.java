package com.x.program.center.jaxrs.config;

import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.config.CenterServer;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;

class ActionGetCenterServer extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = Wo.copier.copy(Config.nodes().centerServers().first().getValue());
		result.setData(wo);
		return result;
	}

	public static class Wo extends CenterServer {

		static WrapCopier<CenterServer, Wo> copier = WrapCopierFactory.wo(CenterServer.class, Wo.class, null, null);

	}
}