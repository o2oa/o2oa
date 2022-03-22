package com.x.program.center.jaxrs.jest;

import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Objects;
import com.x.base.core.project.Application;
import com.x.base.core.project.annotation.Module;
import com.x.base.core.project.annotation.ModuleType;
import com.x.base.core.project.config.CenterServer;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.Node;
import com.x.base.core.project.config.WebServer;
import com.x.base.core.project.config.WebServers;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.tools.ClassLoaderTools;
import com.x.program.center.ThisApplication;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;

abstract class BaseAction extends StandardJaxrsAction {

	private static CopyOnWriteArrayList<Class<?>> assembles;

	private static final String HOST_LOCALHOST = "localhost";

	protected String getHost(HttpServletRequest request) throws Exception {
		return new URL(request.getRequestURL().toString()).getHost();
	}

	protected boolean isUndefindHost(String host) {
		if (StringUtils.isEmpty(host) || StringUtils.equalsIgnoreCase(host, HOST_LOCALHOST)
				|| StringUtils.startsWith(host, "127.0.0.")) {
			return true;
		}
		return false;
	}

	protected Boolean formProxy(HttpServletRequest request, String source) throws Exception {
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

	WoWebServer getRandomWebServer(HttpServletRequest request, String source) throws Exception {
		Boolean fromProxy = this.formProxy(request, source);
		if (fromProxy) {
			return this.getRandomWebServerProxy(request);
		} else {
			return this.getRandomWebServer(request);
		}
	}

	private WoWebServer getRandomWebServer(HttpServletRequest request) throws Exception {
		WoWebServer wrap = null;
		Entry<String, WebServer> entry = Config.nodes().webServers().getRandom();
		if (null == entry) {
			return null;
		}
		wrap = new WoWebServer();
		WebServer webServer = entry.getValue();
		if (this.isUndefindHost(entry.getKey())) {
			wrap.setHost(this.getHost(request));
		} else {
			wrap.setHost(entry.getKey());
		}
		wrap.setPort(webServer.getPort());
		wrap.setSslEnable(webServer.getSslEnable());
		return wrap;
	}

	private WoWebServer getRandomWebServerProxy(HttpServletRequest request) throws Exception {
		WoWebServer wrap = null;
		WebServers webServers = Config.nodes().webServers();
		if (!webServers.isEmpty()) {
			wrap = new WoWebServer();
			Entry<String, WebServer> en = webServers.firstEntry();
			WebServer webServer = en.getValue();
			if (this.isUndefindHost(webServer.getProxyHost())) {
				wrap.setHost(this.getHost(request));
			} else {
				wrap.setHost(webServer.getProxyHost());
			}
			wrap.setPort(webServer.getProxyPort());
		}
		return wrap;
	}

	Map<String, WoAssemble> getRandomAssembles(HttpServletRequest request, String source) throws Exception {
		Boolean fromProxy = this.formProxy(request, source);
		if (fromProxy) {
			return this.getRandomAssemblesProxy(request);
		} else {
			return this.getRandomAssembles(request);
		}
	}

	private Map<String, WoAssemble> getRandomAssembles(HttpServletRequest request) throws Exception {
		Map<String, WoAssemble> map = new HashMap<>();
		for (Class<?> o : listAssemble()) {
			WoAssemble wrap = new WoAssemble();
			Application application = ThisApplication.context().applications().randomWithWeight(o.getName());
			if (null != application) {
				wrap.setContext(application.getContextPath());
				if (this.isUndefindHost(application.getNode())) {
					wrap.setHost(this.getHost(request));
				} else {
					wrap.setHost(application.getNode());
				}
				wrap.setPort(application.getPort());
				wrap.setName(application.getName());
			}
			map.put(o.getSimpleName(), wrap);
		}
		return map;
	}

	private Map<String, WoAssemble> getRandomAssemblesProxy(HttpServletRequest request) throws Exception {
		Map<String, WoAssemble> map = new HashMap<>();
		for (Class<?> o : listAssemble()) {
			WoAssemble wrap = new WoAssemble();
			Application application = ThisApplication.context().applications().randomWithWeight(o.getName());
			if (null != application) {
				wrap.setContext(application.getContextPath());
				if (this.isUndefindHost(application.getProxyHost())) {
					wrap.setHost(this.getHost(request));
				} else {
					wrap.setHost(application.getProxyHost());
				}
				wrap.setPort(application.getProxyPort());
				wrap.setName(application.getName());
			}
			map.put(o.getSimpleName(), wrap);
		}
		return map;
	}

	@SuppressWarnings("unchecked")
	private synchronized List<Class<?>> listAssemble() throws Exception {
		if (null == assembles) {
			try (ScanResult scanResult = new ClassGraph().addClassLoader(
					ClassLoaderTools.urlClassLoader(ClassLoader.getSystemClassLoader(), false, false, false, false))
					.enableAnnotationInfo().scan()) {
				assembles = new CopyOnWriteArrayList<Class<?>>();
				List<ClassInfo> list = new ArrayList<>();
				list.addAll(scanResult.getClassesWithAnnotation(Module.class.getName()));
				list = list.stream().sorted(Comparator.comparing(ClassInfo::getName)).collect(Collectors.toList());
				for (ClassInfo info : list) {
					Class<?> cls = Thread.currentThread().getContextClassLoader().loadClass(info.getName());
					Module module = cls.getAnnotation(Module.class);
					if (Objects.equal(module.type(), ModuleType.ASSEMBLE)) {
						assembles.add(cls);
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