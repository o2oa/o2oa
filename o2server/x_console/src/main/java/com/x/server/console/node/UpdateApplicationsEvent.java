package com.x.server.console.node;

import org.apache.commons.lang3.BooleanUtils;

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
		try {
			if (BooleanUtils.isTrue(Servers.applicationServerIsRunning())) {
				ActionResponse respone = CipherConnectionAction.get(false, 1000, 2000,
						Config.url_x_program_center_jaxrs("center", "applications"));
				Applications applications = respone.getData(Applications.class);
				Config.resource_node_applications(XGsonBuilder.instance().toJsonTree(applications));
			}
		} catch (Exception e) {
			logger.error(e);
		}
	}
}
