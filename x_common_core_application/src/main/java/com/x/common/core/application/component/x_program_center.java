package com.x.common.core.application.component;

import java.io.File;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeMap;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.AndFileFilter;
import org.apache.commons.io.filefilter.NotFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.gson.XGsonBuilder;
import com.x.base.core.utils.net.Host;
import com.x.base.core.utils.zip.JarTools;
import com.x.common.core.application.AbstractThisApplication;

public class x_program_center {

	protected static final String druid_servlet = "<servlet><servlet-name>DruidStatView</servlet-name><servlet-class>com.alibaba.druid.support.http.StatViewServlet</servlet-class></servlet>";
	protected static final String druid_servlet_mapping = "<servlet-mapping><servlet-name>DruidStatView</servlet-name><url-pattern>/druid/*</url-pattern></servlet-mapping>";
	protected static final String druid_filter = "<filter><filter-name>DruidWebStatFilter</filter-name><filter-class>com.alibaba.druid.support.http.WebStatFilter</filter-class><init-param><param-name>exclusions</param-name><param-value>*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*</param-value></init-param></filter>";
	protected static final String druid_filter_mapping = "<filter-mapping><filter-name>DruidWebStatFilter</filter-name><url-pattern>/*</url-pattern></filter-mapping>";

	public static List<JpaObject> containerEntities = new ArrayList<>();

	private File concreteStructure(String distPath, String applicationRepositoryPath, String webRepositoryPath,
			String resourcesPath, String centerHost, Integer centerPort, String centerProxyHost,
			Integer centerProxyPort, String centerContext, String centerCipher, Boolean keepRemoteMeta,
			String applicationsFilePath, String applicationServersFilePath, String datasFilePath,
			String dataServersFilePath, String storagesFilePath, String storageServersFilePath,
			String webServersFilePath) throws Exception {
		File dir = new File(distPath, this.getName());
		FileUtils.forceMkdir(dir);
		FileUtils.cleanDirectory(dir);
		File webInf = new File(dir, "WEB-INF");
		FileUtils.forceMkdir(webInf);
		File lib = new File(webInf, "lib");
		FileUtils.forceMkdir(lib);
		File classes = new File(webInf, "classes");
		FileUtils.forceMkdir(classes);
		File metaInf = new File(classes, "META-INF");
		FileUtils.forceMkdir(metaInf);
		this.createCenterServerFile(metaInf, centerHost, centerPort, centerProxyHost, centerProxyPort, centerContext,
				centerCipher);
		this.copyResources(metaInf, resourcesPath);
		this.copyApplicationRepository(webInf, applicationRepositoryPath);
		this.copyWebRepository(webInf, webRepositoryPath);
		if (keepRemoteMeta) {
			this.copyRemoteMeta(metaInf, centerHost, centerPort, centerContext, centerCipher);
		} else {
			this.copyLocalMeta(metaInf, applicationsFilePath, applicationServersFilePath, datasFilePath,
					dataServersFilePath, storagesFilePath, storageServersFilePath, webServersFilePath);
		}
		this.copyJar_independent(lib, applicationRepositoryPath);
		this.extractJar(classes, applicationRepositoryPath);
		this.extractZip(dir, applicationRepositoryPath);
		this.createWebXml(webInf);
		return dir;
	}

	private void createCenterServerFile(File metaInf, String host, Integer port, String proxyHost, Integer proxyPort,
			String context, String cipher) throws Exception {
		TreeMap<String, Object> map = new TreeMap<>();
		map.put("host", StringUtils.trimToEmpty(host));
		map.put("port", (null != port) ? port : 30080);
		map.put("proxyHost", StringUtils.trimToEmpty(proxyHost));
		map.put("proxyPort", (null != proxyPort) ? proxyPort : 30080);
		map.put("cipher", cipher);
		FileUtils.writeStringToFile(new File(metaInf, "centerServer.json"), XGsonBuilder.toJson(map));
	}

	private void copyResources(File metaInf, String resourcesPath) throws Exception {
		File file = new File(resourcesPath);
		FileUtils.copyDirectory(file, new File(metaInf, "resources"));
	}

	private void copyApplicationRepository(File webinf, String applicationRepositoryPath) throws Exception {
		FileUtils.copyDirectory(new File(applicationRepositoryPath), new File(webinf, "applicationRepository"));
	}

	private void copyWebRepository(File webinf, String webRepositoryPath) throws Exception {
		FileUtils.copyDirectory(new File(webRepositoryPath), new File(webinf, "webRepository"));
	}

	private void copyRemoteMeta(File metaInf, String centerHost, Integer centerPort, String centerContext,
			String cihperValue) throws Exception {
		String url = Host.httpHost(centerHost, centerPort, Host.ROLLBACK_IPV4, Host.CENTER_PORT) + centerContext
				+ "/jaxrs/export/" + URLEncoder.encode(cihperValue, "utf-8");
		String json = AbstractThisApplication.getHttpURLConnectionAsString(url);
		Gson gson = XGsonBuilder.instance();
		JsonObject jsonObject = gson.fromJson(json, JsonObject.class);
		if (!StringUtils.equalsIgnoreCase(jsonObject.get("type").getAsString(), "success")) {
			throw new Exception(url + " connect failure, remote error:" + jsonObject.get("message").getAsString());
		}
		JsonElement jsonElement = null;
		if (jsonObject.has("data")) {
			jsonElement = jsonObject.get("data");
		}
		byte[] bytes = Base64.decodeBase64(jsonElement.getAsString());
		JarTools.unjar(bytes, "", metaInf, true);
	}

	private void copyLocalMeta(File metaInf, String applicationsFilePath, String applicationServersFilePath,
			String datasFilePath, String dataServersFilePath, String storagesFilePath, String storageServersFilePath,
			String webServersFilePath) throws Exception {
		// FileUtils.copyFile(new File(applicationsFilePath), new File(metaInf,
		// "applications.json"));
		FileUtils.copyFile(new File(applicationServersFilePath), new File(metaInf, "applicationServers.json"));
		FileUtils.copyFile(new File(datasFilePath), new File(metaInf, "datas.json"));
		FileUtils.copyFile(new File(dataServersFilePath), new File(metaInf, "dataServers.json"));
		FileUtils.copyFile(new File(storagesFilePath), new File(metaInf, "storages.json"));
		FileUtils.copyFile(new File(storageServersFilePath), new File(metaInf, "storageServers.json"));
		FileUtils.copyFile(new File(webServersFilePath), new File(metaInf, "webServers.json"));
	}

	private void copyJar_independent(File lib, String repositoryPath) throws Exception {
		File repositoryLib = new File(repositoryPath);
		FileUtils.copyDirectory(repositoryLib, lib, new AndFileFilter(new WildcardFileFilter("*.jar"),
				new NotFileFilter(new WildcardFileFilter(this.getName() + "*.jar"))));
		FileUtils.copyDirectory(new File(repositoryLib, "openjpa"), lib, new WildcardFileFilter("*.jar"));
		FileUtils.copyDirectory(new File(repositoryLib, "ehcache"), lib, new WildcardFileFilter("*.jar"));
		FileUtils.copyDirectory(new File(repositoryLib, "slf4j"), lib, new WildcardFileFilter("*.jar"));
	}

	private void createWebXml(File webInf) throws Exception {
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "<web-app id=\"" + this.getName()
				+ "\" metadata-complete=\"false\" version=\"3.0\">" + "<display-name>" + this.getName()
				+ "</display-name>";
		xml += druid_servlet;
		xml += druid_servlet_mapping;
		xml += druid_filter;
		xml += druid_filter_mapping;
		xml += "</web-app>";
		File file = new File(webInf, "web.xml");
		FileUtils.writeStringToFile(file, xml, "UTF-8");
	}

	private void extractJar(File classes, String repositoryPath) throws Exception {
		File repositoryLib = new File(repositoryPath);
		Collection<File> files = FileUtils.listFiles(repositoryLib, new WildcardFileFilter(this.getName() + "*.jar"),
				null);
		JarTools.unjar(files.iterator().next(), "", classes, true);
	}

	private void extractZip(File dir, String repositoryPath) throws Exception {
		File file = new File(repositoryPath, this.getName() + ".zip");
		JarTools.unjar(file, "", dir, true);
	}

	public String pack(String distPath, String applicationRepositoryPath, String webRepositoryPath,
			String resourcesPath, String centerHost, Integer centerPort, String centerProxyHost,
			Integer centerProxyPort, String centerContext, String centerCipher, Boolean keepRemoteMeta,
			String applicationsFilePath, String applicationServersFilePath, String datasFilePath,
			String dataServersFilePath, String storagesFilePath, String storageServersFilePath,
			String webServersFilePath) throws Exception {
		File dir = this.concreteStructure(distPath, applicationRepositoryPath, webRepositoryPath, resourcesPath,
				centerHost, centerPort, centerProxyHost, centerProxyPort, centerContext, centerCipher, keepRemoteMeta,
				applicationsFilePath, applicationServersFilePath, datasFilePath, dataServersFilePath, storagesFilePath,
				storageServersFilePath, webServersFilePath);
		File war = new File(distPath, this.getName() + ".war");
		JarTools.jar(dir, war);
		return war.getAbsolutePath();
	}

	public void compile(String projectPath, String name) throws Exception {
		File buildFile = new File(projectPath, name + "_build.xml");
		Project p = new Project();
		p.setUserProperty("ant.file", buildFile.getAbsolutePath());
		p.init();
		ProjectHelper helper = ProjectHelper.getProjectHelper();
		p.addReference("ant.projectHelper", helper);
		helper.parse(p, buildFile);
		p.executeTarget(p.getDefaultTarget());
	}

	public class Argument {
		private String distPath;
		private String applicationRepositoryPath;
		private String webRepositoryPath;
		private String resourcesPath;
		private String centerHost;
		private Integer centerPort;
		private String centerProxyHost;
		private Integer centerProxyPort;
		private String centerContext;
		private String centerCipher;
		private Boolean keepRemoteMeta;
		private String applicationsFilePath;
		private String applicationServersFilePath;
		private String datasFilePath;
		private String dataServersFilePath;
		private String storagesFilePath;
		private String storageServersFilePath;
		private String webServersFilePath;

		public String getDistPath() {
			return distPath;
		}

		public void setDistPath(String distPath) {
			this.distPath = distPath;
		}

		public String getResourcesPath() {
			return resourcesPath;
		}

		public void setResourcesPath(String resourcesPath) {
			this.resourcesPath = resourcesPath;
		}

		public String getCenterHost() {
			return centerHost;
		}

		public void setCenterHost(String centerHost) {
			this.centerHost = centerHost;
		}

		public Integer getCenterPort() {
			return centerPort;
		}

		public void setCenterPort(Integer centerPort) {
			this.centerPort = centerPort;
		}

		public String getCenterContext() {
			return centerContext;
		}

		public void setCenterContext(String centerContext) {
			this.centerContext = centerContext;
		}

		public String getApplicationsFilePath() {
			return applicationsFilePath;
		}

		public void setApplicationsFilePath(String applicationsFilePath) {
			this.applicationsFilePath = applicationsFilePath;
		}

		public String getApplicationServersFilePath() {
			return applicationServersFilePath;
		}

		public void setApplicationServersFilePath(String applicationServersFilePath) {
			this.applicationServersFilePath = applicationServersFilePath;
		}

		public String getDatasFilePath() {
			return datasFilePath;
		}

		public void setDatasFilePath(String datasFilePath) {
			this.datasFilePath = datasFilePath;
		}

		public String getDataServersFilePath() {
			return dataServersFilePath;
		}

		public void setDataServersFilePath(String dataServersFilePath) {
			this.dataServersFilePath = dataServersFilePath;
		}

		public String getStoragesFilePath() {
			return storagesFilePath;
		}

		public void setStoragesFilePath(String storagesFilePath) {
			this.storagesFilePath = storagesFilePath;
		}

		public String getStorageServersFilePath() {
			return storageServersFilePath;
		}

		public void setStorageServersFilePath(String storageServersFilePath) {
			this.storageServersFilePath = storageServersFilePath;
		}

		public String getWebServersFilePath() {
			return webServersFilePath;
		}

		public void setWebServersFilePath(String webServersFilePath) {
			this.webServersFilePath = webServersFilePath;
		}

		public String getApplicationRepositoryPath() {
			return applicationRepositoryPath;
		}

		public void setApplicationRepositoryPath(String applicationRepositoryPath) {
			this.applicationRepositoryPath = applicationRepositoryPath;
		}

		public String getWebRepositoryPath() {
			return webRepositoryPath;
		}

		public void setWebRepositoryPath(String webRepositoryPath) {
			this.webRepositoryPath = webRepositoryPath;
		}

		public Boolean getKeepRemoteMeta() {
			return keepRemoteMeta;
		}

		public void setKeepRemoteMeta(Boolean keepRemoteMeta) {
			this.keepRemoteMeta = keepRemoteMeta;
		}

		public String getCenterCipher() {
			return centerCipher;
		}

		public void setCenterCipher(String centerCipher) {
			this.centerCipher = centerCipher;
		}

		public String getCenterProxyHost() {
			return centerProxyHost;
		}

		public void setCenterProxyHost(String centerProxyHost) {
			this.centerProxyHost = centerProxyHost;
		}

		public Integer getCenterProxyPort() {
			return centerProxyPort;
		}

		public void setCenterProxyPort(Integer centerProxyPort) {
			this.centerProxyPort = centerProxyPort;
		}
	}

	protected String getName() {
		return StringUtils.replace(this.getClass().getSimpleName(), ".", "_");
	}

	public static void main(String[] args) {
		try {
			String str = args[0];
			str = StringUtils.replace(str, "\\", "/");
			Argument arg = XGsonBuilder.instance().fromJson(str, Argument.class);
			x_program_center o = new x_program_center();
			o.pack(arg.getDistPath(), arg.getApplicationRepositoryPath(), arg.getWebRepositoryPath(),
					arg.getResourcesPath(), arg.getCenterHost(), arg.getCenterPort(), arg.getCenterProxyHost(),
					arg.getCenterProxyPort(), arg.getCenterContext(), arg.getCenterCipher(), arg.getKeepRemoteMeta(),
					arg.getApplicationsFilePath(), arg.getApplicationServersFilePath(), arg.getDatasFilePath(),
					arg.getDataServersFilePath(), arg.getStoragesFilePath(), arg.getStorageServersFilePath(),
					arg.getWebServersFilePath());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
