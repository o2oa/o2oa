package com.x.server.console.node;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;

import com.google.gson.Gson;
import com.x.base.core.project.Application;
import com.x.base.core.project.Applications;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.tools.ListTools;

public class RefreshApplicationsEvent implements Event {

	private static Logger logger = LoggerFactory.getLogger(RefreshApplicationsEvent.class);

	private static final Gson gson = XGsonBuilder.instance();

	public final String type = Event.TYPE_REFRESHAPPLICATIONS;

	public void execute() {
		try {
			if (null != Config.resource_node_applications()) {
				Applications applications = applications();
				if (null != applications) {
					Date now = new Date();
					boolean modify = this.refresh(applications, now);
					if (modify) {
						Config.resource_node_applications(gson.toJsonTree(applications));
					}
					Config.resource_node_applicationsTimestamp(now);
				}
			}
		} catch (Exception e) {
			logger.error(e);
		}
	}

	private boolean refresh(Applications applications, Date date) throws Exception {
		List<String> removeEntries = new ArrayList<>();
		boolean modify = false;
		for (Entry<String, CopyOnWriteArrayList<Application>> en : applications.entrySet()) {
			List<Application> removeApplications = new ArrayList<>();
			for (Application application : en.getValue()) {
				long diffence = Math.abs((date.getTime() - application.getReportDate().getTime()));
				if (diffence > (10 * 2 * 1000) + 5000) {
					removeApplications.add(application);
					logger.warn("cluster dropped application: {}, node: {}, report date: {}.", en.getKey(),
							application.getNode(), DateTools.format(application.getReportDate()));
				}
			}
			modify = en.getValue().removeAll(removeApplications) || modify;
			if (en.getValue().isEmpty()) {
				removeEntries.add(en.getKey());
			}
		}
		if (ListTools.isNotEmpty(removeEntries)) {
			modify = true;
			for (String str : removeEntries) {
				applications().remove(str);
			}
		}
		return modify;
	}

	private Applications applications() throws Exception {
		return gson.fromJson(Config.resource_node_applications(), Applications.class);
	}

}