package com.x.program.center;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;

import com.x.base.core.project.Application;
import com.x.base.core.project.Applications;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.queue.AbstractQueue;
import com.x.base.core.project.tools.DateTools;
import com.x.base.core.project.tools.ListTools;

public class CenterQueue extends AbstractQueue<CenterQueueBody> {

	private static Logger logger = LoggerFactory.getLogger(CenterQueue.class);

	public static final int REFRESHAPPLICATIONSINTERVAL = 45;

	protected void execute(CenterQueueBody body) throws Exception {

		switch (body.type()) {
			case CenterQueueBody.TYPE_REGISTAPPLICATIONS:
				CenterQueueRegistApplicationsBody centerQueueRegistApplicationsBody = (CenterQueueRegistApplicationsBody) body;
				registApplications(centerQueueRegistApplicationsBody);
				break;
			case CenterQueueBody.TYPE_REFRESHAPPLICATION:
				this.refresh((CenterQueueRefreshBody) body);
				break;
			default:
				break;
		}

	}

	private void registApplications(CenterQueueRegistApplicationsBody centerQueueRegistApplicationsBody)
			throws Exception {
		Applications applications = ThisApplication.context().applications();
		Date now = new Date();
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
				Config.resource_node_applicationsTimestamp(now);
				applications.updateTimestamp(now);
			}
		}
		Config.resource_node_applications(XGsonBuilder.instance().toJsonTree(applications));
	}

	private void refresh(CenterQueueRefreshBody body) throws Exception {
		Applications applications = ThisApplication.context().applications();
		Date now = new Date();
		List<String> removeEntries = new ArrayList<>();
		boolean modify = false;
		for (Entry<String, CopyOnWriteArrayList<Application>> en : applications.entrySet()) {
			List<Application> removeApplications = new ArrayList<>();
			for (Application application : en.getValue()) {
				if ((now.getTime() - application.getReportDate().getTime()) > REFRESHAPPLICATIONSINTERVAL * 2 * 1000) {
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
				ThisApplication.context().applications().remove(str);
			}
		}
		if (modify) {
			applications.updateTimestamp(now);
			Config.resource_node_applicationsTimestamp(now);
			Config.resource_node_applications(XGsonBuilder.instance().toJsonTree(applications));
		}
	}
}
