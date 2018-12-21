package com.x.program.center.jaxrs.config;

import java.util.List;

import com.x.base.core.project.Applications;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.connection.CipherConnectionAction;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.program.center.ThisApplication;

class BaseAction extends StandardJaxrsAction {
	public static class AbstractWoProxy extends GsonPropertyObject {

		private String httpProtocol;

		private Center center;

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
	}

}
