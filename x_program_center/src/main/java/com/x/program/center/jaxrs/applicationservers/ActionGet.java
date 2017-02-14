package com.x.program.center.jaxrs.applicationservers;

import com.x.base.core.project.server.ApplicationServers;
import com.x.base.core.project.server.Config;

public class ActionGet {

	public ApplicationServers execute() throws Exception {
		return Config.nodes().applicationServers();
	}

}