package com.x.server.console.node;

import org.eclipse.jetty.server.Server;

import com.x.base.core.project.Applications;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.connection.ActionResponse;
import com.x.base.core.project.connection.CipherConnectionAction;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.server.console.server.Servers;

public class UpdateApplicationsEvent implements Event {

	private static Logger logger = LoggerFactory.getLogger(UpdateApplicationsEvent.class);

	public final String type = Event.TYPE_UPDATEAPPLICATIONS;

	public void execute() {
		this.execute(Servers.getApplicationServer());
	}

	public void execute(Server applicationServer) {
		try {
			if ((null != applicationServer) && applicationServer.isStarted()) {
				ActionResponse respone = CipherConnectionAction.get(false, 4000, 8000,
						Config.url_x_program_center_jaxrs("center", "applications"));
				Applications applications = respone.getData(Applications.class);
				Config.resource_node_applications(XGsonBuilder.instance().toJsonTree(applications));
			}
		} catch (Exception e) {
			logger.error(e);
		}
	}
}
