package com.x.server.console.node;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.quickstart.QuickStartWebApp;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.gzip.GzipHandler;

import com.x.base.core.project.Application;
import com.x.base.core.project.Applications;
import com.x.base.core.project.config.CenterServer;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.connection.CipherConnectionAction;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.jaxrs.WrapString;
import com.x.base.core.project.tools.ListTools;
import com.x.server.console.server.Servers;

public class RegistApplicationsEvent implements Event {

	public final String type = Event.TYPE_REGISTAPPLICATIONS;

	public void execute() throws Exception {

		if (null != Config.resource_node_applications()) {

			Applications applications = XGsonBuilder.instance().fromJson(Config.resource_node_applications(),
					Applications.class);

			List<Application> list = new ArrayList<>();

			out: for (List<Application> o : applications.values()) {
				for (Application application : o) {
					if (StringUtils.equals(Config.node(), application.getNode())) {
						list.add(application);
						continue out;
					}
				}
			}

			if (Servers.applicationServerIsRunning()) {
				List<String> contextPaths = ListTools.extractProperty(list, "contextPath", String.class, true, true);
				List<String> removes = new ArrayList<>();
				GzipHandler gzipHandler = (GzipHandler) Servers.applicationServer.getHandler();
				HandlerList hanlderList = (HandlerList) gzipHandler.getHandler();
				for (Handler handler : hanlderList.getHandlers()) {
					if (QuickStartWebApp.class.isAssignableFrom(handler.getClass())) {
						QuickStartWebApp app = (QuickStartWebApp) handler;
						if (!contextPaths.contains(app.getContextPath())) {
							removes.add(app.getContextPath());
						}
					}
				}
				if (!removes.isEmpty()) {
					list = list.stream().filter(o -> {
						return !removes.contains(o.getContextPath());
					}).collect(Collectors.toList());
				}
			}

			Req req = new Req();

			req.setValue(XGsonBuilder.toJson(list));

			for (Entry<String, CenterServer> entry : Config.nodes().centerServers().orderedEntry()) {
				CipherConnectionAction.put(false,
						Config.url_x_program_center_jaxrs(entry, "center", "regist", "applications"), req);

			}

			Config.resource_node_eventQueue().put(XGsonBuilder.instance().toJsonTree(new UpdateApplicationsEvent()));
		}

	}

	public static class Req extends WrapString {

	}

}