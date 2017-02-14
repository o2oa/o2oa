package com.x.program.center.jaxrs.webservers;

import com.x.base.core.project.server.Config;
import com.x.base.core.project.server.WebServers;

public class ActionGet {

	public WebServers execute() throws Exception {
		return Config.nodes().webServers();
	}

}
