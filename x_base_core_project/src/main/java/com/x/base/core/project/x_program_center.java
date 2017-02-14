package com.x.base.core.project;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.NameFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.StorageType;
import com.x.base.core.gson.XGsonBuilder;
import com.x.base.core.utils.JarTools;

public class x_program_center extends Compilable {

	public static List<JpaObject> containerEntities = new ArrayList<>();
	public static List<StorageType> usedStorageTypes = new ArrayList<>();
	public static List<Class<? extends Compilable>> dependents = new ArrayList<>();

	static {
		dependents.add(x_base_core_project.class);
		dependents.add(x_attendance_core_entity.class);
		dependents.add(x_cms_core_entity.class);
		dependents.add(x_collaboration_core_entity.class);
		dependents.add(x_component_core_entity.class);
		dependents.add(x_file_core_entity.class);
		dependents.add(x_mail_core_entity.class);
		dependents.add(x_meeting_core_entity.class);
		dependents.add(x_okr_core_entity.class);
		dependents.add(x_organization_core_entity.class);
		dependents.add(x_processplatform_core_entity.class);
	}

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
		// File metaInf = new File(classes, "META-INF");
		// FileUtils.forceMkdir(metaInf);
		// this.createCenterServerFile(metaInf, centerHost, centerPort,
		// centerProxyHost, centerProxyPort, centerContext,
		// centerCipher);
		// this.copyResources(metaInf, resourcesPath);
		// this.copyApplicationRepository(webInf, applicationRepositoryPath);
		// this.copyWebRepository(webInf, webRepositoryPath);
		// if (keepRemoteMeta) {
		// this.copyRemoteMeta(metaInf, centerHost, centerPort, centerContext,
		// centerCipher);
		// } else {
		// this.copyLocalMeta(metaInf, applicationsFilePath,
		// applicationServersFilePath, datasFilePath,
		// dataServersFilePath, storagesFilePath, storageServersFilePath,
		// webServersFilePath);
		// }
		this.copyJar(lib, applicationRepositoryPath);
		// this.extractJar(classes, applicationRepositoryPath);
		this.extractZip(dir, applicationRepositoryPath);
		this.createWebXml(webInf);
		return dir;
	}

	private void copyJar(File lib, String repositoryPath) throws Exception {
		File repositoryLib = new File(repositoryPath);
		FileUtils.copyDirectory(repositoryLib, lib, new NameFileFilter(this.getName() + "-" + VERSION + ".jar"));
		FileUtils.copyDirectory(repositoryLib, lib, new NameFileFilter("x_base_core_bean-" + VERSION + ".jar"));
		FileUtils.copyDirectory(repositoryLib, lib, new NameFileFilter("x_base_core_entity-" + VERSION + ".jar"));
		FileUtils.copyDirectory(repositoryLib, lib, new NameFileFilter("x_base_core_exception-" + VERSION + ".jar"));
		FileUtils.copyDirectory(repositoryLib, lib, new NameFileFilter("x_base_core_gson-" + VERSION + ".jar"));
		FileUtils.copyDirectory(repositoryLib, lib, new NameFileFilter("x_base_core_http-" + VERSION + ".jar"));
		FileUtils.copyDirectory(repositoryLib, lib, new NameFileFilter("x_base_core_utils-" + VERSION + ".jar"));
		FileUtils.copyDirectory(repositoryLib, lib, new NameFileFilter("x_attendance_core_entity-" + VERSION + ".jar"));
		FileUtils.copyDirectory(repositoryLib, lib, new NameFileFilter("x_component_core_entity-" + VERSION + ".jar"));
		FileUtils.copyDirectory(repositoryLib, lib, new NameFileFilter("x_file_core_entity-" + VERSION + ".jar"));
		FileUtils.copyDirectory(repositoryLib, lib, new NameFileFilter("x_meeting_core_entity-" + VERSION + ".jar"));
		FileUtils.copyDirectory(repositoryLib, lib, new NameFileFilter("x_okr_core_entity-" + VERSION + ".jar"));
		FileUtils.copyDirectory(repositoryLib, lib,
				new NameFileFilter("x_organization_core_entity-" + VERSION + ".jar"));
		FileUtils.copyDirectory(repositoryLib, lib,
				new NameFileFilter("x_processplatform_core_entity-" + VERSION + ".jar"));
		FileUtils.copyDirectory(repositoryLib, lib,
				new NameFileFilter("x_common_core_application-" + VERSION + ".jar"));
		FileUtils.copyDirectory(repositoryLib, lib, new NameFileFilter("x_common_core_container-" + VERSION + ".jar"));
		FileUtils.copyDirectory(repositoryLib, lib, new NameFileFilter(this.getName() + "-" + VERSION + ".jar"));

		FileUtils.copyDirectory(new File(repositoryLib, "openjpa"), lib, new WildcardFileFilter("*.jar"));
		FileUtils.copyDirectory(new File(repositoryLib, "ehcache"), lib, new WildcardFileFilter("*.jar"));
	}

	private void createWebXml(File webInf) throws Exception {
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "<web-app id=\"" + this.getName()
				+ "\" metadata-complete=\"false\" version=\"3.0\">" + "<display-name>" + this.getName()
				+ "</display-name>";
		// xml += druid_servlet;
		// xml += druid_servlet_mapping;
		// xml += druid_filter;
		// xml += druid_filter_mapping;
		xml += "</web-app>";
		File file = new File(webInf, "web.xml");
		FileUtils.writeStringToFile(file, xml, "UTF-8");
	}

	// private void extractJar(File classes, String repositoryPath) throws
	// Exception {
	// File repositoryLib = new File(repositoryPath);
	// Collection<File> files = FileUtils.listFiles(repositoryLib, new
	// WildcardFileFilter(this.getName() + "*.jar"),
	// null);
	// JarTools.unjar(files.iterator().next(), "", classes, true);
	// }

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
