package com.x.server.console.node;

import com.x.base.core.project.Applications;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.connection.ActionResponse;
import com.x.base.core.project.connection.CipherConnectionAction;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.server.console.server.Servers;

public class UpdateApplicationsEvent implements Event {

	public final String type = Event.TYPE_UPDATEAPPLICATIONS;

	public void execute() throws Exception {
		if (Servers.applicationServerIsRunning()) {
			ActionResponse respone = CipherConnectionAction.get(false,
					Config.url_x_program_center_jaxrs("center", "applications"));
			Applications applications = respone.getData(Applications.class);
			Config.resource_node_applications(XGsonBuilder.instance().toJsonTree(applications));
		}
	}
}
