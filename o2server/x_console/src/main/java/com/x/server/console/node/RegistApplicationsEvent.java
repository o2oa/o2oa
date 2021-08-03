package com.x.server.console.node;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.eclipse.jetty.quickstart.QuickStartWebApp;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.gzip.GzipHandler;

import com.google.gson.Gson;
import com.x.base.core.project.AbstractContext;
import com.x.base.core.project.Application;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.config.CenterServer;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.connection.CipherConnectionAction;
import com.x.base.core.project.exception.RunningException;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.jaxrs.WrapString;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.server.console.server.Servers;

public class RegistApplicationsEvent implements Event {

	private static Logger logger = LoggerFactory.getLogger(RegistApplicationsEvent.class);

	private static final Gson gson = XGsonBuilder.instance();

	public final String type = Event.TYPE_REGISTAPPLICATIONS;

	// private AtomicInteger loop = new AtomicInteger(0);

	public void execute() {

		try {
			if (BooleanUtils.isTrue(Servers.applicationServerIsStarted())
					&& (null != Config.resource_node_applications())) {
				List<Application> list = listApplication();
				// list = removeNotRegisted(list);
				if (BooleanUtils.isTrue(Config.currentNode().getSelfHealthCheckEnable()) && (!this.healthCheck(list))) {
					logger.warn("health check result is false.");
					list.clear();
				}

				// if (list.isEmpty() || 0 == loop.getAndUpdate(o -> (++o % 2))) {

				Req req = new Req();

				req.setNode(Config.node());

				req.setValue(gson.toJson(list));

				for (Entry<String, CenterServer> entry : Config.nodes().centerServers().orderedEntry()) {
					CipherConnectionAction.put(false, 1000, 2000,
							Config.url_x_program_center_jaxrs(entry, "center", "regist", "applications"), req);
				}

				// }
			}
		} catch (Exception e) {
			logger.error(e);
		}
	}

//	private List<Application> removeNotRegisted(List<Application> list) throws Exception {
//		List<String> contextPaths = ListTools.extractProperty(list, "contextPath", String.class, true, true);
//		List<String> removes = new ArrayList<>();
//		GzipHandler gzipHandler = (GzipHandler) Servers.applicationServer.getHandler();
//		HandlerList hanlderList = (HandlerList) gzipHandler.getHandler();
//		for (Handler handler : hanlderList.getHandlers()) {
//			if (QuickStartWebApp.class.isAssignableFrom(handler.getClass())) {
//				QuickStartWebApp app = (QuickStartWebApp) handler;
//				if ((!contextPaths.contains(app.getContextPath())) || (!app.isStarted())) {
//					removes.add(app.getContextPath());
//				}
//			}
//		}
//		if (!removes.isEmpty()) {
//			list = list.stream().filter(o -> !removes.contains(o.getContextPath())).collect(Collectors.toList());
//		}
//		return list;
//	}

	// 保证从已经regist中取出,否则可能在启动阶段即被访问
//	private List<Application> listRegistedApplication() throws Exception {
//		Applications applications = gson.fromJson(Config.resource_node_applications(), Applications.class);
//		List<Application> list = new ArrayList<>();
//		final String node = Config.node();
//		for (List<Application> o : applications.values()) {
//			for (Application application : o) {
//				if (StringUtils.equals(node, application.getNode())) {
//					list.add(application);
//				}
//			}
//		}
//		return list;
//	}

	private List<Application> listApplication() throws Exception {
		List<Application> list = new ArrayList<>();
		GzipHandler gzipHandler = (GzipHandler) Servers.applicationServer.getHandler();
		HandlerList hanlderList = (HandlerList) gzipHandler.getHandler();
		for (Handler handler : hanlderList.getHandlers()) {
			if (QuickStartWebApp.class.isAssignableFrom(handler.getClass())) {
				QuickStartWebApp app = (QuickStartWebApp) handler;
				if (app.isStarted()) {
					list.add(gson.fromJson(app.getServletContext()
							.getAttribute(AbstractContext.SERVLETCONTEXT_ATTRIBUTE_APPLICATION).toString(),
							Application.class));
				}
			}
		}
		return list;
	}

	private boolean healthCheck(List<Application> list) {
		List<CompletableFuture<Long>> futures = new ArrayList<>();
		try {
			for (Application o : list) {
				futures.add(healthCheckTask(o));
			}
			long min = 0;
			long max = 0;
			for (CompletableFuture<Long> future : futures) {
				long difference = future.get(3000, TimeUnit.MILLISECONDS);
				min = Math.min(min, difference);
				max = Math.max(max, difference);
			}
			if (max > 30 * 1000) {
				logger.warn("server time difference is too large: {}ms.", max);
			}
			if (min < 0) {
				return false;
			}
		} catch (Exception e) {
			logger.error(new RunningException(e, "health check error."));
			Thread.currentThread().interrupt();
			return false;
		}
		return true;
	}

	private CompletableFuture<Long> healthCheckTask(Application application) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				Resp resp = CipherConnectionAction.get(false, 1000, 1000, application, "echo").getData(Resp.class);
				Date date = resp.getServerTime();
				return Math.abs(date.getTime() - ((new Date()).getTime()));
			} catch (Exception e) {
				logger.error(new RunningException(e, "health check failure:{},{}.", application.getNode(),
						application.getContextPath()));
			}
			return -1L;
		}, Inner.executorService);
	}

	private static class Inner {
		private static final ExecutorService executorService = Executors.newFixedThreadPool(5,
				new BasicThreadFactory.Builder().namingPattern("RegistApplicationsEvent-healthCheck-%d").daemon(true)
						.build());
	}

	public static class Resp {

		@FieldDescribe("上下文根")
		private String servletContextName;

		@FieldDescribe("服务器时间")
		private Date serverTime;

		public String getServletContextName() {
			return servletContextName;
		}

		public void setServletContextName(String servletContextName) {
			this.servletContextName = servletContextName;
		}

		public Date getServerTime() {
			return serverTime;
		}

		public void setServerTime(Date serverTime) {
			this.serverTime = serverTime;
		}

	}

	public static class Req extends WrapString {

		private static final long serialVersionUID = -2855209663719641934L;

		private String node;

		public String getNode() {
			return node;
		}

		public void setNode(String node) {
			this.node = node;
		}

	}

}