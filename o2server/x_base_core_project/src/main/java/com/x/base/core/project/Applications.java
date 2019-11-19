package com.x.base.core.project;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.connection.ActionResponse;
import com.x.base.core.project.connection.CipherConnectionAction;
import com.x.base.core.project.connection.HttpConnection;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DefaultCharset;
import com.x.base.core.project.tools.ListTools;
import com.x.base.core.project.tools.StringTools;

public class Applications extends ConcurrentHashMap<String, CopyOnWriteArrayList<Application>> {

	private static Logger logger = LoggerFactory.getLogger(Applications.class);

	private static final long serialVersionUID = -2416559829493154858L;

	private volatile String token = UUID.randomUUID().toString();

	private volatile Date updateTimestamp;

	private static final Random random = new Random(System.currentTimeMillis());

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Application get(String className, String tokenOrNode) throws Exception {
		List<Application> list = this.get(className);
		if (null != list) {
			for (Application application : list) {
				if (StringUtils.equals(tokenOrNode, application.getToken())
						|| StringUtils.equals(tokenOrNode, application.getNode())) {
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
		return CipherConnectionAction.get(xdebugger, application.getUrlJaxrsRoot() + CipherConnectionAction.trim(uri));
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
		return CipherConnectionAction.get(xdebugger, application.getUrlJaxrsRoot() + CipherConnectionAction.trim(uri));
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
		return CipherConnectionAction.delete(xdebugger,
				application.getUrlJaxrsRoot() + CipherConnectionAction.trim(uri));
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
		return CipherConnectionAction.delete(xdebugger,
				application.getUrlJaxrsRoot() + CipherConnectionAction.trim(uri));
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
		return CipherConnectionAction.post(xdebugger, application.getUrlJaxrsRoot() + CipherConnectionAction.trim(uri),
				body);
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
		return CipherConnectionAction.post(xdebugger, application.getUrlJaxrsRoot() + CipherConnectionAction.trim(uri),
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
				StringTools.JoinUrl(application.getUrlJaxrsRoot() + CipherConnectionAction.trim(uri)), body);
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
		return CipherConnectionAction.put(xdebugger, application.getUrlJaxrsRoot() + CipherConnectionAction.trim(uri),
				body);
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
		List<Application> list = this.get(className);
		if (ListTools.isNotEmpty(list)) {
			if (list.size() == 1) {
				list.get(0);
			}
			int cursor = 0;
			TreeMap<Integer, Application> tree = new TreeMap<>();
			for (Application o : list) {
				if (o.getWeight() > 0) {
					cursor += o.getWeight();
					tree.put(cursor, o);
				}
			}
			Application application = tree.tailMap(random.nextInt(++cursor), true).firstEntry().getValue();
			return application;
		}
		throw new Exception("randomWithWeight error: " + className + ".");
	}

	public Application randomWithScheduleWeight(String className) throws Exception {
		List<Application> list = this.get(className);
		if (ListTools.isNotEmpty(list)) {
			if (list.size() == 1) {
				list.get(0);
			}
			int cursor = 0;
			TreeMap<Integer, Application> tree = new TreeMap<>();
			for (Application o : list) {
				if (o.getScheduleWeight() > 0) {
					cursor += o.getScheduleWeight();
					tree.put(cursor, o);
				}
			}
			Application application = tree.tailMap(random.nextInt(++cursor), true).firstEntry().getValue();
			return application;
		}
		throw new Exception("randomWithScheduleWeight error: " + className + ".");
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

	public Date updateTimestamp() {
		return updateTimestamp;
	}

	public void updateTimestamp(Date updateTimestamp) {
		this.updateTimestamp = updateTimestamp;
	}

	public String describeApi(String name) throws Exception {
		String applicationName = this.findApplicationName(name);
		if (StringUtils.isEmpty(applicationName)) {
			throw new Exception("getDescribe can not find application with name:" + name + ".");
		}
		Application application = this.randomWithWeight(applicationName);
		return HttpConnection.getAsString(application.getUrlDescribeApiJson(), null);
	}

}