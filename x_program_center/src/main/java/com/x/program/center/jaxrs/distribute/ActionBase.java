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

import com.x.base.core.Packages;
import com.x.base.core.application.Application;
import com.x.base.core.project.Assemble;
import com.x.base.core.project.server.Config;
import com.x.base.core.project.server.Node;
import com.x.base.core.project.server.WebServer;
import com.x.base.core.project.server.WebServers;
import com.x.program.center.ThisApplication;

import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;
import io.github.lukehutch.fastclasspathscanner.scanner.ScanResult;

public class ActionBase {

	private static CopyOnWriteArrayList<Class<? extends Assemble>> assembles;

	private static String HOST_LOCALHOST = "localhost";

	protected WrapOutWebServer getRandomWebServer(HttpServletRequest request, String source) throws Exception {
		Boolean fromProxy = this.formProxy(request, source);
		if (fromProxy) {
			return this.getRandomWebServerProxy(request);
		} else {
			return this.getRandomWebServer(request);
		}
	}

	private WrapOutWebServer getRandomWebServer(HttpServletRequest request) throws Exception {
		WrapOutWebServer wrap = null;
		Entry<String, WebServer> entry = Config.nodes().webServers().getRandom();
		if (null == entry) {
			return null;
		}
		wrap = new WrapOutWebServer();
		WebServer webServer = entry.getValue();
		if (StringUtils.equalsIgnoreCase(entry.getKey(), HOST_LOCALHOST)
				|| StringUtils.startsWith(entry.getKey(), "127.0.0.")) {
			URL url = new URL(request.getRequestURL().toString());
			wrap.setHost(url.getHost());
		} else {
			wrap.setHost(entry.getKey());
		}
		wrap.setPort(webServer.getPort());
		wrap.setSslEnable(webServer.getSslEnable());
		return wrap;
	}

	private WrapOutWebServer getRandomWebServerProxy(HttpServletRequest request) throws Exception {
		WrapOutWebServer wrap = null;
		WebServers webServers = Config.nodes().webServers();
		if (!webServers.isEmpty()) {
			wrap = new WrapOutWebServer();
			Entry<String, WebServer> en = webServers.firstEntry();
			WebServer webServer = en.getValue();
			wrap.setHost(webServer.getProxyHost());
			wrap.setPort(webServer.getProxyPort());
		}
		return wrap;
	}

	protected Map<String, WrapOutAssemble> getRandomAssembles(HttpServletRequest request, String source)
			throws Exception {
		Boolean fromProxy = this.formProxy(request, source);
		if (fromProxy) {
			return this.getRandomAssemblesProxy(request);
		} else {
			return this.getRandomAssembles(request);
		}
	}

	private Boolean formProxy(HttpServletRequest request, String source) throws Exception {
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

	private Map<String, WrapOutAssemble> getRandomAssembles(HttpServletRequest request) throws Exception {
		Map<String, WrapOutAssemble> map = new HashMap<>();
		for (Class<? extends Assemble> o : listAssemble()) {
			WrapOutAssemble wrap = new WrapOutAssemble();
			Application application = ThisApplication.applications.randomWithWeight(o);
			if (null != application) {
				wrap.setContext(application.getContext());
				if (StringUtils.isEmpty(application.getHost())
						|| (StringUtils.equalsIgnoreCase(application.getHost(), HOST_LOCALHOST))
						|| (StringUtils.startsWith(application.getHost(), "127.0.0."))) {
					URL url = new URL(request.getRequestURL().toString());
					wrap.setHost(url.getHost());
				} else {
					wrap.setHost(application.getHost());
				}
				wrap.setPort(application.getPort());
			}
			map.put(o.getSimpleName(), wrap);
		}
		return map;
	}

	private Map<String, WrapOutAssemble> getRandomAssemblesProxy(HttpServletRequest request) throws Exception {
		Map<String, WrapOutAssemble> map = new HashMap<>();
		for (Class<? extends Assemble> o : listAssemble()) {
			WrapOutAssemble wrap = new WrapOutAssemble();
			Application application = ThisApplication.applications.randomWithWeight(o);
			if (null != application) {
				wrap.setContext(application.getContext());
				wrap.setHost(application.getProxyHost());
				wrap.setPort(application.getProxyPort());
			}
			map.put(o.getSimpleName(), wrap);
		}
		return map;
	}

	@SuppressWarnings("unchecked")
	private List<Class<? extends Assemble>> listAssemble() throws Exception {
		if (null == assembles) {
			synchronized (ActionBase.class) {
				if (null == assembles) {
					ScanResult scanResult = new FastClasspathScanner(Packages.PREFIX).scan();
					List<String> assembleList = scanResult.getNamesOfSubclassesOf(Assemble.class);
					List<String> list = new ArrayList<>();
					list.addAll(assembleList);
					Collections.sort(list, new Comparator<String>() {
						public int compare(String s1, String s2) {
							return s1.compareTo(s2);
						}
					});
					assembles = new CopyOnWriteArrayList<Class<? extends Assemble>>();
					for (String str : list) {
						assembles.add((Class<Assemble>) Class.forName(str));
					}
				}
			}
		}
		return assembles;
	}
}