package com.x.base.core.project;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.CRC32;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.Nodes;
import com.x.base.core.project.connection.ActionResponse;
import com.x.base.core.project.connection.CipherConnectionAction;
import com.x.base.core.project.connection.FilePart;
import com.x.base.core.project.connection.FormField;
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
		return this.getQuery(false, applicationClass.getName(), uri, null);
	}

	public ActionResponse getQuery(Class<?> applicationClass, String uri, String seed) throws Exception {
		return this.getQuery(false, applicationClass.getName(), uri, seed);
	}

	public ActionResponse getQuery(Boolean xdebugger, Class<?> applicationClass, String uri) throws Exception {
		return this.getQuery(xdebugger, applicationClass.getName(), uri, null);
	}

	public ActionResponse getQuery(Boolean xdebugger, Class<?> applicationClass, String uri, String seed)
			throws Exception {
		return this.getQuery(xdebugger, applicationClass.getName(), uri, seed);
	}

	public ActionResponse getQuery(Application application, String uri) throws Exception {
		return this.getQuery(false, application, uri);
	}

	public ActionResponse getQuery(Boolean xdebugger, Application application, String uri) throws Exception {
		return CipherConnectionAction.get(xdebugger, application.getUrlJaxrsRoot() + CipherConnectionAction.trim(uri));
	}

	public ActionResponse getQuery(String applicationName, String uri) throws Exception {
		return getQuery(false, applicationName, uri, null);
	}

	public ActionResponse getQuery(String applicationName, String uri, String seed) throws Exception {
		return getQuery(false, applicationName, uri, seed);
	}

	public ActionResponse getQuery(Boolean xdebugger, String applicationName, String uri, String seed)
			throws Exception {
		if (applicationName.equalsIgnoreCase(x_program_center.class.getSimpleName())
				|| applicationName.equalsIgnoreCase(x_program_center.class.getName())) {
			Nodes nodes = Config.nodes();
			for (String node : nodes.keySet()) {
				if (BooleanUtils.isTrue(nodes.get(node).getCenter().getEnable())) {
					Integer port = nodes.get(node).getCenter().getPort();
					StringBuilder buffer = new StringBuilder();
					if (BooleanUtils.isTrue(nodes.get(node).getCenter().getSslEnable())) {
						buffer.append("https://").append(StringUtils.isNotEmpty(node) ? node : "127.0.0.1")
								.append(":" + port);
					} else {
						buffer.append("http://").append(StringUtils.isNotEmpty(node) ? node : "127.0.0.1")
								.append(":" + port);
					}
					buffer.append("/x_program_center/jaxrs/");
					return CipherConnectionAction.get(xdebugger, buffer.toString() + CipherConnectionAction.trim(uri));

				}
			}
		}
		String name = this.findApplicationName(applicationName);
		if (StringUtils.isEmpty(name)) {
			throw new ExceptionFindApplicationName(applicationName);
		}
		Application application = null;
		if (StringUtils.isEmpty(seed)) {
			// 如果随机种子是空,那么优先使用本机
			application = this.findApplicationWithNode(name, Config.node());
			if (null == application) {
				application = this.randomWithWeight(name);
			}
		} else {
			application = this.randomWithSeed(name, seed);
		}
		return CipherConnectionAction.get(xdebugger, application.getUrlJaxrsRoot() + CipherConnectionAction.trim(uri));
	}

	public byte[] getQueryBinary(Class<?> applicationClass, String uri) throws Exception {
		return this.getQueryBinary(false, applicationClass.getName(), uri, null);
	}

	public byte[] getQueryBinary(Class<?> applicationClass, String uri, String seed) throws Exception {
		return this.getQueryBinary(false, applicationClass.getName(), uri, seed);
	}

	public byte[] getQueryBinary(Boolean xdebugger, Class<?> applicationClass, String uri) throws Exception {
		return this.getQueryBinary(xdebugger, applicationClass.getName(), uri, null);
	}

	public byte[] getQueryBinary(Boolean xdebugger, Class<?> applicationClass, String uri, String seed)
			throws Exception {
		return this.getQueryBinary(xdebugger, applicationClass.getName(), uri, seed);
	}

	public byte[] getQueryBinary(Application application, String uri) throws Exception {
		return this.getQueryBinary(false, application, uri);
	}

	public byte[] getQueryBinary(Boolean xdebugger, Application application, String uri) throws Exception {
		return CipherConnectionAction.getBinary(xdebugger,
				application.getUrlJaxrsRoot() + CipherConnectionAction.trim(uri));
	}

	public byte[] getQueryBinary(String applicationName, String uri) throws Exception {
		return getQueryBinary(false, applicationName, uri, null);
	}

	public byte[] getQueryBinary(String applicationName, String uri, String seed) throws Exception {
		return getQueryBinary(false, applicationName, uri, seed);
	}

	public byte[] getQueryBinary(Boolean xdebugger, String applicationName, String uri, String seed) throws Exception {
		String name = this.findApplicationName(applicationName);
		if (applicationName.equalsIgnoreCase(x_program_center.class.getSimpleName())
				|| applicationName.equalsIgnoreCase(x_program_center.class.getName())) {
			Nodes nodes = Config.nodes();
			for (String node : nodes.keySet()) {
				if (BooleanUtils.isTrue(nodes.get(node).getCenter().getEnable())) {
					Integer port = nodes.get(node).getCenter().getPort();
					StringBuilder buffer = new StringBuilder();
					if (BooleanUtils.isTrue(nodes.get(node).getCenter().getSslEnable())) {
						buffer.append("https://").append(StringUtils.isNotEmpty(node) ? node : "127.0.0.1")
								.append(":" + port);
					} else {
						buffer.append("http://").append(StringUtils.isNotEmpty(node) ? node : "127.0.0.1")
								.append(":" + port);
					}
					buffer.append("/x_program_center/jaxrs/");
					return CipherConnectionAction.getBinary(xdebugger,
							buffer.toString() + CipherConnectionAction.trim(uri));
				}
			}
		}

		if (StringUtils.isEmpty(name)) {
			throw new ExceptionFindApplicationName(applicationName);
		}
		Application application = null;
		if (StringUtils.isEmpty(seed)) {
			// 如果随机种子是空,那么优先使用本机
			application = this.findApplicationWithNode(name, Config.node());
			if (null == application) {
				application = this.randomWithWeight(name);
			}
		} else {
			application = this.randomWithSeed(name, seed);
		}
		return CipherConnectionAction.getBinary(xdebugger,
				application.getUrlJaxrsRoot() + CipherConnectionAction.trim(uri));
	}

	public ActionResponse deleteQuery(Class<?> applicationClass, String uri) throws Exception {
		return this.deleteQuery(false, applicationClass.getName(), uri, null);
	}

	public ActionResponse deleteQuery(Class<?> applicationClass, String uri, String seed) throws Exception {
		return this.deleteQuery(false, applicationClass.getName(), uri, seed);
	}

	public ActionResponse deleteQuery(Boolean xdebugger, Class<?> applicationClass, String uri) throws Exception {
		return this.deleteQuery(xdebugger, applicationClass.getName(), uri, null);
	}

	public ActionResponse deleteQuery(Boolean xdebugger, Class<?> applicationClass, String uri, String seed)
			throws Exception {
		return this.deleteQuery(xdebugger, applicationClass.getName(), uri, seed);
	}

	public ActionResponse deleteQuery(Application application, String uri) throws Exception {
		return this.deleteQuery(false, application, uri);
	}

	public ActionResponse deleteQuery(Boolean xdebugger, Application application, String uri) throws Exception {
		return CipherConnectionAction.delete(xdebugger,
				application.getUrlJaxrsRoot() + CipherConnectionAction.trim(uri));
	}

	public ActionResponse deleteQuery(String applicationName, String uri) throws Exception {
		return deleteQuery(false, applicationName, uri, null);
	}

	public ActionResponse deleteQuery(String applicationName, String uri, String seed) throws Exception {
		return deleteQuery(false, applicationName, uri, seed);
	}

	public ActionResponse deleteQuery(Boolean xdebugger, String applicationName, String uri, String seed)
			throws Exception {
		if (applicationName.equalsIgnoreCase(x_program_center.class.getSimpleName())
				|| applicationName.equalsIgnoreCase(x_program_center.class.getName())) {
			Nodes nodes = Config.nodes();
			for (String node : nodes.keySet()) {
				if (BooleanUtils.isTrue(nodes.get(node).getCenter().getEnable())) {
					Integer port = nodes.get(node).getCenter().getPort();
					StringBuilder buffer = new StringBuilder();
					if (BooleanUtils.isTrue(nodes.get(node).getCenter().getSslEnable())) {
						buffer.append("https://").append(StringUtils.isNotEmpty(node) ? node : "127.0.0.1")
								.append(":" + port);
					} else {
						buffer.append("http://").append(StringUtils.isNotEmpty(node) ? node : "127.0.0.1")
								.append(":" + port);
					}
					buffer.append("/x_program_center/jaxrs/");

					return CipherConnectionAction.delete(xdebugger,
							buffer.toString() + CipherConnectionAction.trim(uri));
				}
			}
		}
		String name = this.findApplicationName(applicationName);
		if (StringUtils.isEmpty(name)) {
			throw new ExceptionFindApplicationName(applicationName);
		}
		Application application = null;
		if (StringUtils.isEmpty(seed)) {
			// 如果随机种子是空,那么优先使用本机
			application = this.findApplicationWithNode(name, Config.node());
			if (null == application) {
				application = this.randomWithWeight(name);
			}
		} else {
			application = this.randomWithSeed(name, seed);
		}
		return CipherConnectionAction.delete(xdebugger,
				application.getUrlJaxrsRoot() + CipherConnectionAction.trim(uri));
	}

	public byte[] deleteQueryBinary(Class<?> applicationClass, String uri) throws Exception {
		return this.deleteQueryBinary(false, applicationClass.getName(), uri, null);
	}

	public byte[] deleteQueryBinary(Class<?> applicationClass, String uri, String seed) throws Exception {
		return this.deleteQueryBinary(false, applicationClass.getName(), uri, seed);
	}

	public byte[] deleteQueryBinary(Boolean xdebugger, Class<?> applicationClass, String uri) throws Exception {
		return this.deleteQueryBinary(xdebugger, applicationClass.getName(), uri, null);
	}

	public byte[] deleteQueryBinary(Boolean xdebugger, Class<?> applicationClass, String uri, String seed)
			throws Exception {
		return this.deleteQueryBinary(xdebugger, applicationClass.getName(), uri, seed);
	}

	public byte[] deleteQueryBinary(Application application, String uri) throws Exception {
		return this.deleteQueryBinary(false, application, uri);
	}

	public byte[] deleteQueryBinary(Boolean xdebugger, Application application, String uri) throws Exception {
		return CipherConnectionAction.deleteBinary(xdebugger,
				application.getUrlJaxrsRoot() + CipherConnectionAction.trim(uri));
	}

	public byte[] deleteQueryBinary(String applicationName, String uri) throws Exception {
		return deleteQueryBinary(false, applicationName, uri, null);
	}

	public byte[] deleteQueryBinary(String applicationName, String uri, String seed) throws Exception {
		return deleteQueryBinary(false, applicationName, uri, seed);
	}

	public byte[] deleteQueryBinary(Boolean xdebugger, String applicationName, String uri, String seed)
			throws Exception {
		if (applicationName.equalsIgnoreCase(x_program_center.class.getSimpleName())
				|| applicationName.equalsIgnoreCase(x_program_center.class.getName())) {
			Nodes nodes = Config.nodes();
			for (String node : nodes.keySet()) {
				if (BooleanUtils.isTrue(nodes.get(node).getCenter().getEnable())) {
					Integer port = nodes.get(node).getCenter().getPort();
					StringBuilder buffer = new StringBuilder();
					if (BooleanUtils.isTrue(nodes.get(node).getCenter().getSslEnable())) {
						buffer.append("https://").append(StringUtils.isNotEmpty(node) ? node : "127.0.0.1")
								.append(":" + port);
					} else {
						buffer.append("http://").append(StringUtils.isNotEmpty(node) ? node : "127.0.0.1")
								.append(":" + port);
					}
					buffer.append("/x_program_center/jaxrs/");

					return CipherConnectionAction.deleteBinary(xdebugger,
							buffer.toString() + CipherConnectionAction.trim(uri));
				}
			}
		}
		String name = this.findApplicationName(applicationName);
		if (StringUtils.isEmpty(name)) {
			throw new ExceptionFindApplicationName(applicationName);
		}
		Application application = null;
		if (StringUtils.isEmpty(seed)) {
			// 如果随机种子是空,那么优先使用本机
			application = this.findApplicationWithNode(name, Config.node());
			if (null == application) {
				application = this.randomWithWeight(name);
			}
		} else {
			application = this.randomWithSeed(name, seed);
		}
		return CipherConnectionAction.deleteBinary(xdebugger,
				application.getUrlJaxrsRoot() + CipherConnectionAction.trim(uri));
	}

	public ActionResponse postQuery(Class<?> applicationClass, String uri, Object body) throws Exception {
		return this.postQuery(false, applicationClass.getName(), uri, body, null);
	}

	public ActionResponse postQuery(Class<?> applicationClass, String uri, Object body, String seed) throws Exception {
		return this.postQuery(false, applicationClass.getName(), uri, body, seed);
	}

	public ActionResponse postQuery(Boolean xdebugger, Class<?> applicationClass, String uri, Object body)
			throws Exception {
		return this.postQuery(xdebugger, applicationClass.getName(), uri, body, null);
	}

	public ActionResponse postQuery(Boolean xdebugger, Class<?> applicationClass, String uri, Object body, String seed)
			throws Exception {
		return this.postQuery(xdebugger, applicationClass.getName(), uri, body, seed);
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
		return this.postQuery(false, applicationName, uri, body, null);
	}

	public ActionResponse postQuery(String applicationName, String uri, Object body, String seed) throws Exception {
		return this.postQuery(false, applicationName, uri, body, seed);
	}

	public ActionResponse postQuery(Boolean xdebugger, String applicationName, String uri, Object body, String seed)
			throws Exception {
		if (applicationName.equalsIgnoreCase(x_program_center.class.getSimpleName())
				|| applicationName.equalsIgnoreCase(x_program_center.class.getName())) {
			Nodes nodes = Config.nodes();
			for (String node : nodes.keySet()) {
				if (BooleanUtils.isTrue(nodes.get(node).getCenter().getEnable())) {
					Integer port = nodes.get(node).getCenter().getPort();
					StringBuilder buffer = new StringBuilder();
					if (BooleanUtils.isTrue(nodes.get(node).getCenter().getSslEnable())) {
						buffer.append("https://").append(StringUtils.isNotEmpty(node) ? node : "127.0.0.1")
								.append(":" + port);
					} else {
						buffer.append("http://").append(StringUtils.isNotEmpty(node) ? node : "127.0.0.1")
								.append(":" + port);
					}
					buffer.append("/x_program_center/jaxrs/");

					return CipherConnectionAction.post(xdebugger, buffer.toString() + CipherConnectionAction.trim(uri),
							body);
				}
			}
		}
		String name = this.findApplicationName(applicationName);
		if (StringUtils.isEmpty(name)) {
			throw new ExceptionFindApplicationName(applicationName);
		}
		Application application = null;
		if (StringUtils.isEmpty(seed)) {
			// 如果随机种子是空,那么优先使用本机
			application = this.findApplicationWithNode(name, Config.node());
			if (null == application) {
				application = this.randomWithWeight(name);
			}
		} else {
			application = this.randomWithSeed(name, seed);
		}
		return CipherConnectionAction.post(xdebugger, application.getUrlJaxrsRoot() + CipherConnectionAction.trim(uri),
				body);
	}

	public byte[] postQueryBinary(Class<?> applicationClass, String uri, Object body) throws Exception {
		return this.postQueryBinary(false, applicationClass.getName(), uri, body, null);
	}

	public byte[] postQueryBinary(Class<?> applicationClass, String uri, Object body, String seed) throws Exception {
		return this.postQueryBinary(false, applicationClass.getName(), uri, body, seed);
	}

	public byte[] postQueryBinary(Boolean xdebugger, Class<?> applicationClass, String uri, Object body)
			throws Exception {
		return this.postQueryBinary(xdebugger, applicationClass.getName(), uri, body, null);
	}

	public byte[] postQueryBinary(Boolean xdebugger, Class<?> applicationClass, String uri, Object body, String seed)
			throws Exception {
		return this.postQueryBinary(xdebugger, applicationClass.getName(), uri, body, seed);
	}

	public byte[] postQueryBinary(Application application, String uri, Object body) throws Exception {
		return this.postQueryBinary(false, application, uri, body);
	}

	public byte[] postQueryBinary(Boolean xdebugger, Application application, String uri, Object body)
			throws Exception {
		return CipherConnectionAction.postBinary(xdebugger,
				application.getUrlJaxrsRoot() + CipherConnectionAction.trim(uri), body);
	}

	public byte[] postQueryBinary(String applicationName, String uri, Object body) throws Exception {
		return this.postQueryBinary(false, applicationName, uri, body, null);
	}

	public byte[] postQueryBinary(String applicationName, String uri, Object body, String seed) throws Exception {
		return this.postQueryBinary(false, applicationName, uri, body, seed);
	}

	public byte[] postQueryBinary(Boolean xdebugger, String applicationName, String uri, Object body, String seed)
			throws Exception {
		if (applicationName.equalsIgnoreCase(x_program_center.class.getSimpleName())
				|| applicationName.equalsIgnoreCase(x_program_center.class.getName())) {
			Nodes nodes = Config.nodes();
			for (String node : nodes.keySet()) {
				if (BooleanUtils.isTrue(nodes.get(node).getCenter().getEnable())) {
					Integer port = nodes.get(node).getCenter().getPort();
					StringBuilder buffer = new StringBuilder();
					if (BooleanUtils.isTrue(nodes.get(node).getCenter().getSslEnable())) {
						buffer.append("https://").append(StringUtils.isNotEmpty(node) ? node : "127.0.0.1")
								.append(":" + port);
					} else {
						buffer.append("http://").append(StringUtils.isNotEmpty(node) ? node : "127.0.0.1")
								.append(":" + port);
					}
					buffer.append("/x_program_center/jaxrs/");

					return CipherConnectionAction.postBinary(xdebugger,
							buffer.toString() + CipherConnectionAction.trim(uri), body);
				}
			}
		}

		String name = this.findApplicationName(applicationName);
		if (StringUtils.isEmpty(name)) {
			throw new ExceptionFindApplicationName(applicationName);
		}
		Application application = null;
		if (StringUtils.isEmpty(seed)) {
			// 如果随机种子是空,那么优先使用本机
			application = this.findApplicationWithNode(name, Config.node());
			if (null == application) {
				application = this.randomWithWeight(name);
			}
		} else {
			application = this.randomWithSeed(name, seed);
		}
		return CipherConnectionAction.postBinary(xdebugger,
				application.getUrlJaxrsRoot() + CipherConnectionAction.trim(uri), body);
	}

	public byte[] postQueryMultiPartinary(Class<?> applicationClass, String uri, Collection<FormField> formFields,
			Collection<FilePart> fileParts) throws Exception {
		return this.postQueryMultiPartBinary(false, applicationClass.getName(), uri, formFields, fileParts, null);
	}

	public byte[] postQueryMultiPartBinary(Class<?> applicationClass, String uri, Collection<FormField> formFields,
			Collection<FilePart> fileParts, String seed) throws Exception {
		return this.postQueryMultiPartBinary(false, applicationClass.getName(), uri, formFields, fileParts, seed);
	}

	public byte[] postQueryMultiPartBinary(Boolean xdebugger, Class<?> applicationClass, String uri,
			Collection<FormField> formFields, Collection<FilePart> fileParts) throws Exception {
		return this.postQueryMultiPartBinary(xdebugger, applicationClass.getName(), uri, formFields, fileParts, null);
	}

	public byte[] postQueryMultiPartBinary(Boolean xdebugger, Class<?> applicationClass, String uri,
			Collection<FormField> formFields, Collection<FilePart> fileParts, String seed) throws Exception {
		return this.postQueryMultiPartBinary(xdebugger, applicationClass.getName(), uri, formFields, fileParts, seed);
	}

	public byte[] postQueryMultiPartBinary(Application application, String uri, Collection<FormField> formFields,
			Collection<FilePart> fileParts) throws Exception {
		return this.postQueryMultiPartBinary(false, application, uri, formFields, fileParts);
	}

	public byte[] postQueryMultiPartBinary(Boolean xdebugger, Application application, String uri,
			Collection<FormField> formFields, Collection<FilePart> fileParts) throws Exception {
		return CipherConnectionAction.postMultiPartBinary(xdebugger,
				application.getUrlJaxrsRoot() + CipherConnectionAction.trim(uri), formFields, fileParts);
	}

	public byte[] postQueryMultiPartBinary(String applicationName, String uri, Collection<FormField> formFields,
			Collection<FilePart> fileParts) throws Exception {
		return this.postQueryMultiPartBinary(false, applicationName, uri, formFields, fileParts, null);
	}

	public byte[] postQueryMultiPartBinary(String applicationName, String uri, Collection<FormField> formFields,
			Collection<FilePart> fileParts, String seed) throws Exception {
		return this.postQueryMultiPartBinary(false, applicationName, uri, formFields, fileParts, seed);
	}

	public byte[] postQueryMultiPartBinary(Boolean xdebugger, String applicationName, String uri,
			Collection<FormField> formFields, Collection<FilePart> fileParts, String seed) throws Exception {
		if (applicationName.equalsIgnoreCase(x_program_center.class.getSimpleName())
				|| applicationName.equalsIgnoreCase(x_program_center.class.getName())) {
			Nodes nodes = Config.nodes();
			for (String node : nodes.keySet()) {
				if (BooleanUtils.isTrue(nodes.get(node).getCenter().getEnable())) {
					Integer port = nodes.get(node).getCenter().getPort();
					StringBuilder buffer = new StringBuilder();
					if (BooleanUtils.isTrue(nodes.get(node).getCenter().getSslEnable())) {
						buffer.append("https://").append(StringUtils.isNotEmpty(node) ? node : "127.0.0.1")
								.append(":" + port);
					} else {
						buffer.append("http://").append(StringUtils.isNotEmpty(node) ? node : "127.0.0.1")
								.append(":" + port);
					}
					buffer.append("/x_program_center/jaxrs/");

					return CipherConnectionAction.postMultiPartBinary(xdebugger,
							buffer.toString() + CipherConnectionAction.trim(uri), formFields, fileParts);
				}
			}
		}

		String name = this.findApplicationName(applicationName);
		if (StringUtils.isEmpty(name)) {
			throw new ExceptionFindApplicationName(applicationName);
		}
		Application application = null;
		if (StringUtils.isEmpty(seed)) {
			// 如果随机种子是空,那么优先使用本机
			application = this.findApplicationWithNode(name, Config.node());
			if (null == application) {
				application = this.randomWithWeight(name);
			}
		} else {
			application = this.randomWithSeed(name, seed);
		}
		return CipherConnectionAction.postMultiPartBinary(xdebugger,
				application.getUrlJaxrsRoot() + CipherConnectionAction.trim(uri), formFields, fileParts);
	}

	public ActionResponse putQuery(Class<?> applicationClass, String uri, Object body) throws Exception {
		return this.putQuery(false, applicationClass.getName(), uri, body, null);
	}

	public ActionResponse putQuery(Class<?> applicationClass, String uri, Object body, String seed) throws Exception {
		return this.putQuery(false, applicationClass.getName(), uri, body, seed);
	}

	public ActionResponse putQuery(Boolean xdebugger, Class<?> applicationClass, String uri, Object body)
			throws Exception {
		return this.putQuery(xdebugger, applicationClass.getName(), uri, body, null);
	}

	public ActionResponse putQuery(Boolean xdebugger, Class<?> applicationClass, String uri, Object body, String seed)
			throws Exception {
		return this.putQuery(xdebugger, applicationClass.getName(), uri, body, seed);
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
		return this.putQuery(false, applicationName, uri, body, null);
	}

	public ActionResponse putQuery(String applicationName, String uri, Object body, String seed) throws Exception {
		return this.putQuery(false, applicationName, uri, body, seed);
	}

	public ActionResponse putQuery(Boolean xdebugger, String applicationName, String uri, Object body, String seed)
			throws Exception {
		if (applicationName.equalsIgnoreCase(x_program_center.class.getSimpleName())
				|| applicationName.equalsIgnoreCase(x_program_center.class.getName())) {
			Nodes nodes = Config.nodes();
			for (String node : nodes.keySet()) {
				if (BooleanUtils.isTrue(nodes.get(node).getCenter().getEnable())) {
					Integer port = nodes.get(node).getCenter().getPort();
					StringBuilder buffer = new StringBuilder();
					if (BooleanUtils.isTrue(nodes.get(node).getCenter().getSslEnable())) {
						buffer.append("https://").append(StringUtils.isNotEmpty(node) ? node : "127.0.0.1")
								.append(":" + port);
					} else {
						buffer.append("http://").append(StringUtils.isNotEmpty(node) ? node : "127.0.0.1")
								.append(":" + port);
					}
					buffer.append("/x_program_center/jaxrs/");

					return CipherConnectionAction.put(xdebugger, buffer.toString() + CipherConnectionAction.trim(uri),
							body);
				}
			}
		}

		String name = this.findApplicationName(applicationName);
		if (StringUtils.isEmpty(name)) {
			throw new ExceptionFindApplicationName(applicationName);
		}
		Application application = null;
		if (StringUtils.isEmpty(seed)) {
			// 如果随机种子是空,那么优先使用本机
			application = this.findApplicationWithNode(name, Config.node());
			if (null == application) {
				application = this.randomWithWeight(name);
			}
		} else {
			application = this.randomWithSeed(name, seed);
		}
		return CipherConnectionAction.put(xdebugger, application.getUrlJaxrsRoot() + CipherConnectionAction.trim(uri),
				body);
	}

	public String findApplicationName(String name) throws Exception {
		for (String str : this.keySet()) {
			if (StringUtils.equalsIgnoreCase(str, name) || StringUtils.endsWithIgnoreCase(str, "." + name)) {
				return str;
			}
		}
		if (name.equalsIgnoreCase(x_program_center.class.getSimpleName())
				|| name.equalsIgnoreCase(x_program_center.class.getName())) {
			return x_program_center.class.getName();
		}
		return null;
	}

	public Application findApplicationWithNode(String className, String node) throws Exception {
		for (Application o : this.get(className)) {
			if (o.getNode().equals(node)) {
				return o;
			}
		}
		return null;
	}

	public Application randomWithWeight(String className) throws IllegalStateException {
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
		throw new IllegalStateException("randomWithWeight error: " + className + ".");
	}

	public Application randomWithScheduleWeight(String className) throws IllegalStateException {
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
		throw new IllegalStateException("randomWithScheduleWeight error: " + className + ".");
	}

	public Application randomWithSeed(String className, String seed) throws Exception {
		List<Application> list = this.get(className);
		CRC32 crc32 = new CRC32();
		crc32.update(seed.getBytes(DefaultCharset.charset));
		int idx = (int) crc32.getValue() % list.size();
		return list.get(Math.abs(idx));
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
		String urlDescribeApiJson = "";
		if (name.equalsIgnoreCase(x_program_center.class.getSimpleName())
				|| name.equalsIgnoreCase(x_program_center.class.getName())) {
			Nodes nodes = Config.nodes();
			for (String node : nodes.keySet()) {
				if (BooleanUtils.isTrue(nodes.get(node).getCenter().getEnable())) {
					Integer port = nodes.get(node).getCenter().getPort();
					StringBuilder buffer = new StringBuilder();
					if (BooleanUtils.isTrue(nodes.get(node).getCenter().getSslEnable())) {
						buffer.append("https://").append(StringUtils.isNotEmpty(node) ? node : "127.0.0.1")
								.append(":" + port);
					} else {
						buffer.append("http://").append(StringUtils.isNotEmpty(node) ? node : "127.0.0.1")
								.append(":" + port);
					}
					buffer.append("/x_program_center/describe/api.json");
					urlDescribeApiJson = buffer.toString();
					break;
				}
			}
		} else {
			String applicationName = this.findApplicationName(name);
			if (StringUtils.isEmpty(applicationName)) {
				throw new Exception("getDescribe can not find application with name:" + name + ".");
			}
			Application application = this.randomWithWeight(applicationName);
			urlDescribeApiJson = application.getUrlDescribeApiJson();
		}
		return HttpConnection.getAsString(urlDescribeApiJson, null);
	}

}