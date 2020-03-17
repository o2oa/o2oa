package com.x.program.center.jaxrs.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import com.x.base.core.project.Applications;
import com.x.base.core.project.config.CenterServer;
import com.x.base.core.project.config.CenterServers;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;

class ActionTest1 extends BaseAction {

	ActionResult<Wo> execute(EffectivePerson effectivePerson) throws Exception {

		ActionResult<Wo> result = new ActionResult<>();

		if (!effectivePerson.isManager()) {
			throw new ExceptionAccessDenied(effectivePerson);
		}

		Wo wo = new Wo();

		wo.applications = gson.fromJson(Config.resource_node_applications(), Applications.class);
		wo.resource_node_centersPirmaryNode = Config.resource_node_centersPirmaryNode();
		wo.resource_node_centersPirmaryPort = Config.resource_node_centersPirmaryPort();
		wo.resource_node_centersPirmarySslEnable = Config.resource_node_centersPirmarySslEnable();
		for (Entry<String, CenterServer> en : Config.nodes().centerServers().orderedEntry()) {
			wo.nodes_centerServers_ordered.add(en.getValue());
		}
		wo.centerServers = Config.nodes().centerServers();
		result.setData(wo);

		return result;
	}

	public static class Wo extends GsonPropertyObject {

		private Applications applications;

		private String resource_node_centersPirmaryNode;

		private Integer resource_node_centersPirmaryPort;

		private Boolean resource_node_centersPirmarySslEnable;

		private List<CenterServer> nodes_centerServers_ordered = new ArrayList<>();

		private CenterServers centerServers;

	}
}