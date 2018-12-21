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

	public Application get(Class<?> clz, String token) throws Exception {
		List<Application> list = this.get(clz.getName());
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

	public void add(Class<?> applicationClass, Application application) throws Exception {
		CopyOnWriteArrayList<Application> list = this.get(applicationClass.getName());
		if (null == list) {
			list = new CopyOnWriteArrayList<Application>();
			this.put(applicationClass.getName(), list);
		}
		list.add(application);
	}

	public ActionResponse getQuery(Boolean xdebugger, Class<?> applicationClass, String uri) throws Exception {
		Application application = this.randomWithWeight(applicationClass);
		return CipherConnectionAction.get(xdebugger, application.getUrlRoot() + CipherConnectionAction.trim(uri));
	}

	public ActionResponse getQuery(Class<?> applicationClass, String uri) throws Exception {
		return this.getQuery(false, applicationClass, uri);
	}

	public ActionResponse getQuery(Boolean xdebugger, Application application, String uri) throws Exception {
		return CipherConnectionAction.get(xdebugger,
				StringTools.JoinUrl(application.getUrlRoot(), CipherConnectionAction.trim(uri)));
	}

	public ActionResponse getQuery(Application application, String uri) throws Exception {
		return this.getQuery(false, application, uri);
	}

	public ActionResponse getQuery(Boolean xdebugger, String applicationName, String uri) throws Exception {
		Class<?> cls = Class.forName(Applications.class.getPackage().getName() + "." + applicationName);
		return this.getQuery(xdebugger, cls, uri);
	}

	public ActionResponse getQuery(String applicationName, String uri) throws Exception {
		Class<?> cls = Class.forName(Applications.class.getPackage().getName() + "." + applicationName);
		return this.getQuery(false, cls, uri);
	}

	public ActionResponse deleteQuery(Boolean xdebugger, Class<?> applicationClass, String uri) throws Exception {
		Application application = this.randomWithWeight(applicationClass);
		return CipherConnectionAction.delete(xdebugger,
				StringTools.JoinUrl(application.getUrlRoot() + CipherConnectionAction.trim(uri)));
	}

	public ActionResponse deleteQuery(Class<?> applicationClass, String uri) throws Exception {
		return this.deleteQuery(false, applicationClass, uri);
	}

	public ActionResponse deleteQuery(Boolean xdebugger, Application application, String uri) throws Exception {
		return CipherConnectionAction.delete(xdebugger, application.getUrlRoot() + CipherConnectionAction.trim(uri));
	}

	public ActionResponse deleteQuery(Application application, String uri) throws Exception {
		return this.deleteQuery(false, application, uri);
	}

	public ActionResponse deleteQuery(Boolean xdebugger, String applicationName, String uri) throws Exception {
		Class<?> cls = Class.forName(Applications.class.getPackage().getName() + "." + applicationName);
		return this.deleteQuery(xdebugger, cls, uri);
	}

	public ActionResponse deleteQuery(String applicationName, String uri) throws Exception {
		Class<?> cls = Class.forName(Applications.class.getPackage().getName() + "." + applicationName);
		return this.deleteQuery(false, cls, uri);
	}

	public ActionResponse postQuery(Boolean xdebugger, Class<?> applicationClass, String uri, Object o)
			throws Exception {
		Application application = this.randomWithWeight(applicationClass);
		return CipherConnectionAction.post(xdebugger,
				StringTools.JoinUrl(application.getUrlRoot() + CipherConnectionAction.trim(uri)), o);
	}

	public ActionResponse postQuery(Class<?> applicationClass, String uri, Object o) throws Exception {
		return this.postQuery(false, applicationClass, uri, o);
	}

	public ActionResponse postQuery(Boolean xdebugger, Application application, String uri, Object o) throws Exception {
		return CipherConnectionAction.post(xdebugger, application.getUrlRoot() + CipherConnectionAction.trim(uri), o);
	}

	public ActionResponse postQuery(Application application, String uri, Object o) throws Exception {
		return this.postQuery(false, application, uri, o);
	}

	public ActionResponse postQuery(Boolean xdebugger, String applicationName, String uri, String body)
			throws Exception {
		Class<?> cls = Class.forName(Applications.class.getPackage().getName() + "." + applicationName);
		return this.postQuery(xdebugger, cls, uri, body);
	}

	public ActionResponse postQuery(String applicationName, String uri, String body) throws Exception {
		Class<?> cls = Class.forName(Applications.class.getPackage().getName() + "." + applicationName);
		return this.postQuery(false, cls, uri, body);
	}

	public ActionResponse putQuery(Boolean xdebugger, Class<?> applicationClass, String uri, Object o)
			throws Exception {
		Application application = this.randomWithWeight(applicationClass);
		return CipherConnectionAction.put(xdebugger, application.getUrlRoot() + CipherConnectionAction.trim(uri), o);
	}

	public ActionResponse putQuery(Class<?> applicationClass, String uri, Object o) throws Exception {
		return this.putQuery(false, applicationClass, uri, o);
	}

	public ActionResponse putQuery(Boolean xdebugger, Application application, String uri, Object o) throws Exception {
		return CipherConnectionAction.put(xdebugger,
				StringTools.JoinUrl(application.getUrlRoot() + CipherConnectionAction.trim(uri)), o);
	}

	public ActionResponse putQuery(Application application, String uri, Object o) throws Exception {
		return this.putQuery(false, application, uri, o);
	}

	public ActionResponse putQuery(Boolean xdebugger, String applicationName, String uri, String body)
			throws Exception {
		Class<?> cls = Class.forName(Applications.class.getPackage().getName() + "." + applicationName);
		return this.putQuery(xdebugger, cls, uri, body);
	}

	public ActionResponse putQuery(String applicationName, String uri, String body) throws Exception {
		Class<?> cls = Class.forName(Applications.class.getPackage().getName() + "." + applicationName);
		return this.putQuery(false, cls, uri, body);
	}

	public Application randomWithWeight(Class<?> clz) throws Exception {
		List<Application> availabeApplications = new ArrayList<>();
		List<Application> list = this.get(clz.getName());
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