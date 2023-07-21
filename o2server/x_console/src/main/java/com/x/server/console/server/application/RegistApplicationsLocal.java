package com.x.server.console.server.application;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.quickstart.QuickStartWebApp;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.gzip.GzipHandler;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.x.base.core.project.AbstractContext;
import com.x.base.core.project.Application;
import com.x.base.core.project.Applications;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.exception.RunningException;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class RegistApplicationsLocal {

	private static Logger logger = LoggerFactory.getLogger(RegistApplicationsLocal.class);

	private static final Gson gson = XGsonBuilder.instance();

	public void execute(Server server) {
		try {
			List<Application> list = this.listApplication(server);
			this.registApplicationsLocal(list);
		} catch (Exception e) {
			logger.error(e);
		}

	}

	private List<Application> listApplication(Server server) throws Exception {
		List<Application> list = new ArrayList<>();
		GzipHandler gzipHandler = (GzipHandler) server.getHandler();
		HandlerList hanlderList = (HandlerList) gzipHandler.getHandler();
		for (Handler handler : hanlderList.getHandlers()) {
			if (QuickStartWebApp.class.isAssignableFrom(handler.getClass())) {
				QuickStartWebApp app = (QuickStartWebApp) handler;
				if (app.isStarted() && (!StringUtils.equalsIgnoreCase(app.getContextPath(), "/x_program_center"))
						&& (!StringUtils.equalsIgnoreCase(app.getContextPath(), "/"))) {
					try {
						list.add(gson.fromJson(
								app.getServletContext()
										.getAttribute(AbstractContext.SERVLETCONTEXT_ATTRIBUTE_APPLICATION).toString(),
								Application.class));
					} catch (Exception e) {
						logger.error(new RunningException("cannot read application attribute contextPath:{}.",
								app.getContextPath()));
					}
				}
			}
		}
		return list;
	}

	private void registApplicationsLocal(List<Application> list) throws Exception {
		Date now = new Date();
		Applications applications = applications();
		for (Application application : list) {
			Application existed = applications.get(application.getClassName(), application.getNode());
			if (null != existed) {
				existed.setReportDate(now);
			} else {
				application.setReportDate(now);
				applications.add(application.getClassName(), application);
			}
		}
		applications.updateTimestamp(now);
		Config.resource_node_applicationsTimestamp(now);
		Config.resource_node_applications(XGsonBuilder.instance().toJsonTree(applications));
	}

	private Applications applications() throws Exception {
		JsonElement jsonElement = Config.resource_node_applications();
		if ((null != jsonElement) && (!jsonElement.isJsonNull())) {
			return gson.fromJson(Config.resource_node_applications(), Applications.class);
		} else {
			return new Applications();
		}
	}

}