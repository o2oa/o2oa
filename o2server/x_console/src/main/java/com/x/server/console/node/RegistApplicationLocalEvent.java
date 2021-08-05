package com.x.server.console.node;

import java.util.Date;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.Application;
import com.x.base.core.project.Applications;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class RegistApplicationLocalEvent extends Application implements Event {

	private static final long serialVersionUID = -5152446763855039174L;

	private static Logger logger = LoggerFactory.getLogger(RegistApplicationLocalEvent.class);

	public final String type = Event.TYPE_REGISTAPPLICATIONLOCAL;

	public void execute() {
		Applications applications = null;
		try {
			if (null != Config.resource_node_applications()) {
				applications = XGsonBuilder.instance().fromJson(Config.resource_node_applications(),
						Applications.class);
				CopyOnWriteArrayList<Application> list = applications.get(this.getClassName());
				if (null == list) {
					list = new CopyOnWriteArrayList<Application>();
					applications.put(this.getClassName(), list);
					this.update(applications, list);
				} else if (list.stream().filter(o -> {
					return StringUtils.equals(o.getNode(), this.getNode());
				}).count() == 0) {
					this.update(applications, list);
				}
			} else {
				applications = new Applications();
				CopyOnWriteArrayList<Application> list = new CopyOnWriteArrayList<>();
				applications.put(this.getClassName(), list);
				this.update(applications, list);
			}
		} catch (Exception e) {
			logger.error(e);
		}

	}

	private void update(Applications applications, CopyOnWriteArrayList<Application> list) throws Exception {
		Application application = XGsonBuilder.convert(this, Application.class);
		list.add(application);
		Config.resource_node_applications(XGsonBuilder.instance().toJsonTree(applications));
		Config.resource_node_applicationsTimestamp(new Date());
		Config.resource_node_eventQueue().put(XGsonBuilder.instance().toJsonTree(new RegistApplicationsEvent()));
	}

}