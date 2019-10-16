package com.x.server.console.node;

import java.util.Date;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.Application;
import com.x.base.core.project.Applications;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.gson.XGsonBuilder;

public class RegistApplicationEvent extends Application implements Event {

	public final String type = Event.TYPE_REGISTAPPLICATION;

	public void execute() throws Exception {
		Applications applications = null;
		if (null != Config.resource_node_applications()) {
			applications = XGsonBuilder.instance().fromJson(Config.resource_node_applications(), Applications.class);
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
	}

	private void update(Applications applications, CopyOnWriteArrayList<Application> list) throws Exception {
		Application application = XGsonBuilder.convert(this, Application.class);
		list.add(application);
		Config.resource_node_applications(XGsonBuilder.instance().toJsonTree(applications));
		Config.resource_node_applicationsTimestamp(new Date());
		Config.resource_node_eventQueue().put(XGsonBuilder.instance().toJsonTree(new RegistApplicationsEvent()));
	}

}