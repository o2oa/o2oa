package com.x.program.center;

import java.util.Date;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.Application;
import com.x.base.core.project.Applications;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.queue.AbstractQueue;
import com.x.base.core.project.tools.ListTools;

public class CenterQueue extends AbstractQueue<CenterQueueBody> {

	private static Logger logger = LoggerFactory.getLogger(CenterQueue.class);

	protected void execute(CenterQueueBody body) throws Exception {
		if (StringUtils.equals(body.type(), CenterQueueBody.TYPE_REGISTAPPLICATIONS)) {
			CenterQueueRegistApplicationsBody centerQueueRegistApplicationsBody = (CenterQueueRegistApplicationsBody) body;
			registApplications(centerQueueRegistApplicationsBody);
		}
	}

	private void registApplications(CenterQueueRegistApplicationsBody centerQueueRegistApplicationsBody)
			throws Exception {
		Applications applications = ThisApplication.context().applications();
		Date now = new Date();
		if (centerQueueRegistApplicationsBody.isEmpty()
				&& StringUtils.isNotEmpty(centerQueueRegistApplicationsBody.getNode())) {
			this.clearNode(applications, centerQueueRegistApplicationsBody.getNode());
		} else {
			for (Application body : centerQueueRegistApplicationsBody) {
				Application application = applications.get(body.getClassName(), body.getNode());
				if (null != application) {
					application.setReportDate(now);
				} else {
					if (ListTools.isNotEmpty(applications.get(body.getClassName()))) {
						logger.print("cluster add application: {}, node: {}.", body.getNode(), body.getClassName());
					}
					body.setReportDate(now);
					applications.add(body.getClassName(), body);
					applications.updateTimestamp(now);
				}
			}
		}
		Config.resource_node_applicationsTimestamp(now);
		Config.resource_node_applications(XGsonBuilder.instance().toJsonTree(applications));
	}

	private void clearNode(Applications applications, String node) {
		Set<String> removeEntries = new HashSet<>();
		for (Entry<String, CopyOnWriteArrayList<Application>> entry : applications.entrySet()) {
			Set<Application> removeApplications = new HashSet<>();
			for (Application application : entry.getValue()) {
				if (StringUtils.equals(application.getNode(), node)) {
					removeApplications.add(application);
				}

			}
			entry.getValue().removeAll(removeApplications);
			if (entry.getValue().isEmpty()) {
				removeEntries.add(entry.getKey());
			}
		}
		for (String key : removeEntries) {
			applications.remove(key);
		}
	}

}
