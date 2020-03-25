package com.x.program.center.jaxrs.distribute;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.Application;
import com.x.base.core.project.config.CenterServer;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.Node;
import com.x.base.core.project.config.WebServer;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.program.center.ThisApplication;

abstract class BaseAction extends StandardJaxrsAction {

	protected static CopyOnWriteArrayList<Class<?>> assembles;

	protected static String HOST_LOCALHOST = "localhost";

	private String getHost(HttpServletRequest request) throws Exception {
		URL url = new URL(request.getRequestURL().toString());
		return url.getHost();
	}

	/* 判断请求是否来自proxyHost,center,application,web都需要单独判断 */
	private Boolean fromProxy(HttpServletRequest request, String source) throws Exception {
		if (StringUtils.isEmpty(source)) {
			return false;
		}
		CenterServer centerServer = Config.nodes().centerServers().get(Config.node());
		if (StringUtils.equals(centerServer.getProxyHost(), source)) {
			return true;
		}
		for (Node o : Config.nodes().values()) {
			if (BooleanUtils.isTrue(o.getApplication().getEnable())) {
				if (StringUtils.equals(o.getApplication().getProxyHost(), source)) {
					return true;
				}
			}
			if (BooleanUtils.isTrue(o.getWeb().getEnable())) {
				if (StringUtils.equals(o.getWeb().getProxyHost(), source)) {
					return true;
				}
			}
		}
		return false;
	}

	private Boolean fromNode(HttpServletRequest request, String source) throws Exception {
		if (StringUtils.isEmpty(source)) {
			return false;
		}
		for (Entry<String, Node> en : Config.nodes().entrySet()) {
			if (BooleanUtils.isTrue(en.getValue().getEnable())) {
				if (StringUtils.equalsIgnoreCase(en.getKey(), source)) {
					return true;
				}
			}
		}
		return false;
	}

	WoWebServer getRandomWebServer(HttpServletRequest request, String source) throws Exception {
		if (this.fromProxy(request, source)) {
			return this.getRandomWebServerProxy();
		} else if (this.fromNode(request, source)) {
			return this.getRandomWebServerNode();
		} else {
			return this.getRandomWebServerFrom(request, source);
		}
	}

	private WoWebServer getRandomWebServerFrom(HttpServletRequest request, String source) throws Exception {
		WoWebServer wrap = null;
		Entry<String, WebServer> entry = Config.nodes().webServers().getRandom();
		if (null != entry) {
			wrap = new WoWebServer();
			WebServer webServer = entry.getValue();
			wrap.setHost(StringUtils.isNotEmpty(source) ? source : this.getHost(request));
			wrap.setPort(webServer.getPort());
			wrap.setSslEnable(webServer.getSslEnable());
		}
		return wrap;
	}

	private WoWebServer getRandomWebServerProxy() throws Exception {
		WoWebServer wrap = null;
		Entry<String, WebServer> entry = Config.nodes().webServers().getRandom();
		if (null != entry) {
			wrap = new WoWebServer();
			WebServer webServer = entry.getValue();
			wrap.setHost(webServer.getProxyHost());
			wrap.setPort(webServer.getProxyPort());
			wrap.setSslEnable(webServer.getSslEnable());
		}
		return wrap;
	}

	private WoWebServer getRandomWebServerNode() throws Exception {
		WoWebServer wrap = null;
		Entry<String, WebServer> entry = Config.nodes().webServers().getRandom();
		if (null != entry) {
			wrap = new WoWebServer();
			wrap.setHost(entry.getKey());
			wrap.setPort(entry.getValue().getPort());
			wrap.setSslEnable(entry.getValue().getSslEnable());
		}
		return wrap;

	}

	Map<String, WoAssemble> getRandomAssembles(HttpServletRequest request, String source) throws Exception {
		if (this.fromProxy(request, source)) {
			return this.getRandomAssemblesProxy();
		} else if (this.fromNode(request, source)) {
			return this.getRandomAssemblesNode();
		} else {
			return this.getRandomAssemblesFrom(request, source);
		}
	}

	private Map<String, WoAssemble> getRandomAssemblesFrom(HttpServletRequest request, String source) throws Exception {
		Map<String, WoAssemble> map = new HashMap<>();
		for (String str : ThisApplication.context().applications().keySet()) {
			WoAssemble wrap = new WoAssemble();
			Application application = ThisApplication.context().applications().randomWithWeight(str);
			if (null != application) {
				wrap.setContext(application.getContextPath());
				wrap.setHost(StringUtils.isNotEmpty(source) ? source : this.getHost(request));
				wrap.setPort(application.getPort());
				wrap.setName(application.getName());
			}
			map.put(StringUtils.substringAfterLast(str, "."), wrap);
		}
		return map;
	}

	private Map<String, WoAssemble> getRandomAssemblesProxy() throws Exception {
		Map<String, WoAssemble> map = new HashMap<>();
		for (String str : ThisApplication.context().applications().keySet()) {
			WoAssemble wrap = new WoAssemble();
			Application application = ThisApplication.context().applications().randomWithWeight(str);
			if (null != application) {
				wrap.setContext(application.getContextPath());
				wrap.setHost(application.getProxyHost());
				wrap.setPort(application.getProxyPort());
				wrap.setName(application.getName());
			}
			map.put(StringUtils.substringAfterLast(str, "."), wrap);
		}
		return map;
	}

	private Map<String, WoAssemble> getRandomAssemblesNode() throws Exception {
		Map<String, WoAssemble> map = new HashMap<>();
		for (String str : ThisApplication.context().applications().keySet()) {
			WoAssemble wrap = new WoAssemble();
			Application application = ThisApplication.context().applications().randomWithWeight(str);
			if (null != application) {
				wrap.setContext(application.getContextPath());
				wrap.setHost(application.getNode());
				wrap.setPort(application.getPort());
				wrap.setName(application.getName());
			}
			map.put(StringUtils.substringAfterLast(str, "."), wrap);
		}
		return map;
	}

	public static class WoAssemble extends GsonPropertyObject {

		private String name;
		private Boolean sslEnable;
		private String host;
		private Integer port;
		private String context;

		public String getHost() {
			return host;
		}

		public void setHost(String host) {
			this.host = host;
		}

		public Integer getPort() {
			return port;
		}

		public void setPort(Integer port) {
			this.port = port;
		}

		public String getContext() {
			return context;
		}

		public void setContext(String context) {
			this.context = context;
		}

		public Boolean getSslEnable() {
			return sslEnable;
		}

		public void setSslEnable(Boolean sslEnable) {
			this.sslEnable = sslEnable;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}

	public static class WoWebServer extends GsonPropertyObject {

		private Boolean sslEnable;
		private String host;
		private Integer port;

		public String getHost() {
			return host;
		}

		public void setHost(String host) {
			this.host = host;
		}

		public Boolean getSslEnable() {
			return sslEnable;
		}

		public void setSslEnable(Boolean sslEnable) {
			this.sslEnable = sslEnable;
		}

		public Integer getPort() {
			return port;
		}

		public void setPort(Integer port) {
			this.port = port;
		}

	}

}