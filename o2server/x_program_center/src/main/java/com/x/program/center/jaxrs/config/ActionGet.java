package com.x.program.center.jaxrs.config;

import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.config.CenterServer;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;

class ActionGet extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wo = new Wo();
		wo.setCenterServer(WoCenterServer.copier.copy(Config.nodes().centerServers().first().getValue()));

		result.setData(wo);
		return result;
	}

	public static class Wo extends GsonPropertyObject {
		private WoCenterServer centerServer;

		public WoCenterServer getCenterServer() {
			return centerServer;
		}

		public void setCenterServer(WoCenterServer centerServer) {
			this.centerServer = centerServer;
		}

	}

	public static class WoCenterServer extends CenterServer {

		static WrapCopier<CenterServer, WoCenterServer> copier = WrapCopierFactory.wo(CenterServer.class,
				WoCenterServer.class, null, null);

	}

}