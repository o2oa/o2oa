package com.x.base.core.project;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.connection.ActionResponse;
import com.x.base.core.project.connection.CipherConnectionAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DefaultCharset;
import com.x.base.core.project.tools.StringTools;

public class Applications extends ConcurrentHashMap<String, CopyOnWriteArrayList<Application>> {

	private static Logger logger = LoggerFactory.getLogger(Applications.class);

	private static final long serialVersionUID = -2416559829493154858L;

	private volatile String token = UUID.randomUUID().toString();

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Application get(String className, String token) throws Exception {
		List<Application> list = this.get(className);
		if (null != list) {
			for (Application application : list) {
				if (StringUtils.equals(token, application.getToken())) {
					return application;
				}
			}
		}
		return null;
	}

	public List<Application> get(Class<?> clz) throws Exception {
		return this.get(clz.getName());
	}

	public void add(String className, Application application) throws Exception {
		CopyOnWriteArrayList<Application> list = this.get(className);
		if (null == list) {
			list = new CopyOnWriteArrayList<Application>();
			this.put(className, list);
		}
		list.add(application);
	}

	public ActionResponse getQuery(Class<?> applicationClass, String uri) throws Exception {
		return this.getQuery(false, applicationClass.getName(), uri);
	}

	public ActionResponse getQuery(Boolean xdebugger, Class<?> applicationClass, String uri) throws Exception {
		return this.getQuery(xdebugger, applicationClass.getName(), uri);
	}

	public ActionResponse getQuery(Application application, String uri) throws Exception {
		return this.getQuery(false, application, uri);
	}

	public ActionResponse getQuery(Boolean xdebugger, Application application, String uri) throws Exception {
		return CipherConnectionAction.get(xdebugger, application.getUrlRoot() + CipherConnectionAction.trim(uri));
	}

	public ActionResponse getQuery(String applicationName, String uri) throws Exception {
		return getQuery(false, applicationName, uri);
	}

	public ActionResponse getQuery(Boolean xdebugger, String applicationName, String uri) throws Exception {
		String name = this.findApplicationName(applicationName);
		if (StringUtils.isEmpty(name)) {
			throw new Exception("getQuery can not find application with name:" + applicationName + ".");
		}
		Application application = this.randomWithWeight(name);
		return CipherConnectionAction.get(xdebugger, application.getUrlRoot() + CipherConnectionAction.trim(uri));
	}

	public ActionResponse deleteQuery(Class<?> applicationClass, String uri) throws Exception {
		return this.deleteQuery(false, applicationClass.getName(), uri);
	}

	public ActionResponse deleteQuery(Boolean xdebugger, Class<?> applicationClass, String uri) throws Exception {
		return this.deleteQuery(xdebugger, applicationClass.getName(), uri);
	}

	public ActionResponse deleteQuery(Application application, String uri) throws Exception {
		return this.deleteQuery(false, application, uri);
	}

	public ActionResponse deleteQuery(Boolean xdebugger, Application application, String uri) throws Exception {
		return CipherConnectionAction.delete(xdebugger, application.getUrlRoot() + CipherConnectionAction.trim(uri));
	}

	public ActionResponse deleteQuery(String applicationName, String uri) throws Exception {
		return deleteQuery(false, applicationName, uri);
	}

	public ActionResponse deleteQuery(Boolean xdebugger, String applicationName, String uri) throws Exception {
		String name = this.findApplicationName(applicationName);
		if (StringUtils.isEmpty(name)) {
			throw new Exception("deleteQuery can not find application with name:" + applicationName + ".");
		}
		Application application = this.randomWithWeight(name);
		return CipherConnectionAction.delete(xdebugger, application.getUrlRoot() + CipherConnectionAction.trim(uri));
	}

	public ActionResponse postQuery(Class<?> applicationClass, String uri, Object body) throws Exception {
		return this.postQuery(false, applicationClass.getName(), uri, body);
	}

	public ActionResponse postQuery(Boolean xdebugger, Class<?> applicationClass, String uri, Object body)
			throws Exception {
		return this.postQuery(xdebugger, applicationClass.getName(), uri, body);
	}

	public ActionResponse postQuery(Application application, String uri, Object body) throws Exception {
		return this.postQuery(false, application, uri, body);
	}

	public ActionResponse postQuery(Boolean xdebugger, Application application, String uri, Object body)
			throws Exception {
		return CipherConnectionAction.post(xdebugger, application.getUrlRoot() + CipherConnectionAction.trim(uri), 	body);
	}

	public ActionResponse postQuery(String applicationName, String uri, Object body) throws Exception {
		return this.postQuery(false, applicationName, uri, body);
	}

	public ActionResponse postQuery(Boolean xdebugger, String applicationName, String uri, Object body)
			throws Exception {
		String name = this.findApplicationName(applicationName);
		if (StringUtils.isEmpty(name)) {
			throw new Exception("postQuery can not find application with name:" + applicationName + ".");
		}
		Application application = this.randomWithWeight(name);
		return CipherConnectionAction.post(xdebugger, application.getUrlRoot() + CipherConnectionAction.trim(uri),
				body);
	}

	public ActionResponse putQuery(Class<?> applicationClass, String uri, Object body) throws Exception {
		return this.putQuery(false, applicationClass.getName(), uri, body);
	}

	public ActionResponse putQuery(Boolean xdebugger, Class<?> applicationClass, String uri, Object body)
			throws Exception {
		return this.putQuery(xdebugger, applicationClass.getName(), uri, body);
	}

	public ActionResponse putQuery(Application application, String uri, Object body) throws Exception {
		return this.putQuery(false, application, uri, body);
	}

	public ActionResponse putQuery(Boolean xdebugger, Application application, String uri, Object body)
			throws Exception {
		return CipherConnectionAction.put(xdebugger,
				StringTools.JoinUrl(application.getUrlRoot() + CipherConnectionAction.trim(uri)), body);
	}

	public ActionResponse putQuery(String applicationName, String uri, Object body) throws Exception {
		return this.putQuery(false, applicationName, uri, body);
	}

	public ActionResponse putQuery(Boolean xdebugger, String applicationName, String uri, Object body)
			throws Exception {
		String name = this.findApplicationName(applicationName);
		if (StringUtils.isEmpty(name)) {
			throw new Exception("putQuery can not find application with name:" + applicationName + ".");
		}
		Application application = this.randomWithWeight(name);
		return CipherConnectionAction.put(xdebugger, application.getUrlRoot() + CipherConnectionAction.trim(uri), body);
	}

	public String findApplicationName(String name) throws Exception {
		for (String str : this.keySet()) {
			if (StringUtils.equalsIgnoreCase(str, name) || StringUtils.endsWithIgnoreCase(str, "." + name)) {
				return str;
			}
		}
		return null;
	}

	public Application randomWithWeight(String className) throws Exception {
		List<Application> availabeApplications = new ArrayList<>();
		List<Application> list = this.get(className);
		if (null != list) {
			for (Application app : list) {
				availabeApplications.add(app);
			}
		}
		if (availabeApplications.isEmpty()) {
			return null;
		}
		int total = 0;
		for (Application application : availabeApplications) {
			total += application.getWeight();
		}
		Random random = new Random();
		int rdm = random.nextInt(total);
		int current = 0;
		for (Application application : availabeApplications) {
			current += application.getWeight();
			if (rdm <= current) {
				return application;
			}
		}
		throw new Exception("randomWithWeight error.");
	}

	public static String joinQueryUri(String... parts) {
		return Stream.of(parts).map(s -> {
			try {
				return URLEncoder.encode(s, DefaultCharset.name);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				return "";
			}
		}).collect(Collectors.joining("/"));
	}
}