package com.x.program.center.jaxrs.distribute;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.Application;
import com.x.base.core.project.AssembleA;
import com.x.base.core.project.Packages;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.Node;
import com.x.base.core.project.config.WebServer;
import com.x.base.core.project.config.WebServers;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.program.center.ThisApplication;

import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;
import io.github.lukehutch.fastclasspathscanner.scanner.ScanResult;

abstract class BaseAction extends StandardJaxrsAction {

	private static CopyOnWriteArrayList<Class<? extends AssembleA>> assembles;

	private static String HOST_LOCALHOST = "localhost";

	private String getHost(HttpServletRequest request) throws Exception {
		URL url = new URL(request.getRequestURL().toString());
		return url.getHost();
	}

	// private boolean isUndefindHost(String host) {
	// if (StringUtils.isEmpty(host) || StringUtils.equalsIgnoreCase(host,
	// HOST_LOCALHOST)
	// || StringUtils.startsWith(host, "127.0.0.")) {
	// return true;
	// }
	// return false;
	// }

	/* 判断请求是否来自proxyHost,center,application,web都需要单独判断 */
	private Boolean fromProxy(HttpServletRequest request, String source) throws Exception {
		if (StringUtils.isEmpty(source)) {
			return false;
		}
		if (StringUtils.equals(Config.centerServer().getProxyHost(), source)) {
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
		for (Class<? extends AssembleA> o : listAssemble()) {
			WoAssemble wrap = new WoAssemble();
			Application application = ThisApplication.context().applications().randomWithWeight(o);
			if (null != application) {
				wrap.setContext(application.getContext());
				wrap.setHost(StringUtils.isNotEmpty(source) ? source : this.getHost(request));
				wrap.setPort(application.getPort());
				wrap.setName(application.getName());
			}
			map.put(o.getSimpleName(), wrap);
		}
		return map;
	}

	private Map<String, WoAssemble> getRandomAssemblesProxy() throws Exception {
		Map<String, WoAssemble> map = new HashMap<>();
		for (Class<? extends AssembleA> o : listAssemble()) {
			WoAssemble wrap = new WoAssemble();
			Application application = ThisApplication.context().applications().randomWithWeight(o);
			if (null != application) {
				wrap.setContext(application.getContext());
				wrap.setHost(application.getProxyHost());
				wrap.setPort(application.getProxyPort());
				wrap.setName(application.getName());
			}
			map.put(o.getSimpleName(), wrap);
		}
		return map;
	}

	private Map<String, WoAssemble> getRandomAssemblesNode() throws Exception {
		Map<String, WoAssemble> map = new HashMap<>();
		for (Class<? extends AssembleA> o : listAssemble()) {
			WoAssemble wrap = new WoAssemble();
			Application application = ThisApplication.context().applications().randomWithWeight(o);
			if (null != application) {
				wrap.setContext(application.getContext());
				wrap.setHost(application.getNode());
				wrap.setPort(application.getPort());
				wrap.setName(application.getName());
			}
			map.put(o.getSimpleName(), wrap);
		}
		return map;
	}

	@SuppressWarnings("unchecked")
	private List<Class<? extends AssembleA>> listAssemble() throws Exception {
		if (null == assembles) {
			synchronized (BaseAction.class) {
				if (null == assembles) {
					ScanResult scanResult = new FastClasspathScanner(Packages.PREFIX).scan();
					List<String> assembleList = scanResult.getNamesOfSubclassesOf(AssembleA.class);
					List<String> list = new ArrayList<>();
					list.addAll(assembleList);
					Collections.sort(list, new Comparator<String>() {
						public int compare(String s1, String s2) {
							return s1.compareTo(s2);
						}
					});
					assembles = new CopyOnWriteArrayList<Class<? extends AssembleA>>();
					for (String str : list) {
						assembles.add((Class<AssembleA>) Class.forName(str));
					}
				}
			}
		}
		return assembles;
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