package com.x.program.center.jaxrs.config;

import java.util.List;
import java.util.Map;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.config.CenterServer;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.connection.CipherConnectionAction;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.program.center.ThisApplication;

class BaseAction extends StandardJaxrsAction {

	protected static final String SLASH = "/";

	public static class AbstractWoProxy extends GsonPropertyObject {

		private static final long serialVersionUID = -4901521017475819615L;

		@FieldDescribe("http协议")
		private String httpProtocol;

		@FieldDescribe("center服务器")
		private Center center;

		@FieldDescribe("web服务器")
		private Web web;

		private List<Application> applicationList;

		public String getHttpProtocol() {
			return httpProtocol;
		}

		public void setHttpProtocol(String httpProtocol) {
			this.httpProtocol = httpProtocol;
		}

		public static class Center extends GsonPropertyObject {

			private String proxyHost;

			private Integer proxyPort;

			public String getProxyHost() {
				return proxyHost;
			}

			public void setProxyHost(String proxyHost) {
				this.proxyHost = proxyHost;
			}

			public Integer getProxyPort() {
				return proxyPort;
			}

			public void setProxyPort(Integer proxyPort) {
				this.proxyPort = proxyPort;
			}

		}

		public static class Web extends GsonPropertyObject {

			private String proxyHost;

			private Integer proxyPort;

			public String getProxyHost() {
				return proxyHost;
			}

			public void setProxyHost(String proxyHost) {
				this.proxyHost = proxyHost;
			}

			public Integer getProxyPort() {
				return proxyPort;
			}

			public void setProxyPort(Integer proxyPort) {
				this.proxyPort = proxyPort;
			}

		}

		public static class Application extends GsonPropertyObject {

			private String node;

			private String proxyHost;

			private Integer proxyPort;

			public String getProxyHost() {
				return proxyHost;
			}

			public void setProxyHost(String proxyHost) {
				this.proxyHost = proxyHost;
			}

			public Integer getProxyPort() {
				return proxyPort;
			}

			public void setProxyPort(Integer proxyPort) {
				this.proxyPort = proxyPort;
			}

			public String getNode() {
				return node;
			}

			public void setNode(String node) {
				this.node = node;
			}

		}

		public Center getCenter() {
			return center;
		}

		public void setCenter(Center center) {
			this.center = center;
		}

		public Web getWeb() {
			return web;
		}

		public void setWeb(Web web) {
			this.web = web;
		}

		public List<Application> getApplicationList() {
			return applicationList;
		}

		public void setApplicationList(List<Application> applicationList) {
			this.applicationList = applicationList;
		}

	}

	public void configFlush(EffectivePerson effectivePerson) throws Exception {
		Config.flush();
		ThisApplication.context().applications().values().forEach(o -> {
			o.stream().forEach(app -> {
				try {
					CipherConnectionAction.get(effectivePerson.getDebugger(), app, "cache", "config", "flush");
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
		});
		List<Map.Entry<String, CenterServer>> centerList = Config.nodes().centerServers().orderedEntry();
		for (Map.Entry<String, CenterServer> centerEntry : centerList) {
			try {
				CipherConnectionAction.get(effectivePerson.getDebugger(),
						Config.url_x_program_center_jaxrs(centerEntry, "cache", "config", "flush"));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static class CacheLogObject extends GsonPropertyObject {
		private String userToken;

		private String node;

		private long lastPoint;

		public long getLastPoint() {
			return lastPoint;
		}

		public void setLastPoint(long lastPoint) {
			this.lastPoint = lastPoint;
		}

		public String getNode() {
			return node;
		}

		public void setNode(String node) {
			this.node = node;
		}

		public String getUserToken() {
			return userToken;
		}

		public void setUserToken(String userToken) {
			this.userToken = userToken;
		}
	}

}
