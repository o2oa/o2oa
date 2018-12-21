package com.x.base.core.project;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.tools.JarTools;

public abstract class AssembleC extends Deployable {

	private File concreteStructure(String distPath, String repositoryPath) throws Exception {
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
		this.copyPersistence(metaInf, repositoryPath);
		this.copyJar(lib, repositoryPath);
		this.extractZip(dir, repositoryPath);
		this.createWebXml(webInf);
		return dir;
	}

	// private void createCenterServerFile(File metaInf, String host, Integer
	// port, String cipher) throws Exception {
	// TreeMap<String, Object> map = new TreeMap<>();
	// map.put("host", StringUtils.trimToEmpty(host));
	// map.put("port", null != port ? port : 30080);
	// map.put("cipher", cipher);
	// FileUtils.writeStringToFile(new File(metaInf, "centerServer.json"),
	// XGsonBuilder.toJson(map));
	// }
	//
	// private void createConfigFile(File metaInf, String
	// configApplicationServer) throws Exception {
	// TreeMap<String, Object> map = new TreeMap<>();
	// map.put("applicationServer",
	// StringUtils.trimToEmpty(configApplicationServer));
	// FileUtils.writeStringToFile(new File(metaInf, "config.json"),
	// XGsonBuilder.toJson(map));
	// }

	private void copyJar(File lib, String repositoryPath) throws Exception {
		File repositoryLib = new File(repositoryPath);
		FileUtils.copyDirectory(repositoryLib, lib, new WildcardFileFilter("x_base_core*.jar"));
		FileUtils.copyDirectory(repositoryLib, lib, new WildcardFileFilter("x_common_core*.jar"));
		FileUtils.copyDirectory(new File(repositoryLib, "openjpa"), lib, new WildcardFileFilter("*.jar"));
		FileUtils.copyDirectory(new File(repositoryLib, "ehcache"), lib, new WildcardFileFilter("*.jar"));
		FileUtils.copyDirectory(new File(repositoryLib, "slf4j"), lib, new WildcardFileFilter("*.jar"));
	}

	private void copyPersistence(File metaInf, String repositoryPath) throws Exception {
		FileUtils.copyFile(new File(repositoryPath, "x_persistence_" + this.getName() + ".xml"),
				new File(metaInf, "x_persistence.xml"), false);
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
	// Collection<File> files = FileUtils.listFiles(repositoryLib,
	// new WildcardFileFilter(this.getName() + "-4.0.0.jar"), null);
	// JarTools.unjar(files.iterator().next(), "", classes, true);
	// }

	private void extractZip(File dir, String repositoryPath) throws Exception {
		File file = new File(repositoryPath, this.getName() + ".zip");
		JarTools.unjar(file, "", dir, true);
	}

	@Override
	public String pack(String distPath, String repositoryPath) throws Exception {
		File dir = this.concreteStructure(distPath, repositoryPath);
		// custom(dir, repositoryPath);
		File war = new File(distPath, this.getName() + ".war");
		JarTools.jar(dir, war);
		return war.getAbsolutePath();
	}

	protected String getName() {
		return StringUtils.replace(this.getClass().getSimpleName(), ".", "_");
	}

	public class Argument {

		private String distPath;
		private String repositoryPath;

		public String getDistPath() {
			return distPath;
		}

		public void setDistPath(String distPath) {
			this.distPath = distPath;
		}

		public String getRepositoryPath() {
			return repositoryPath;
		}

		public void setRepositoryPath(String repositoryPath) {
			this.repositoryPath = repositoryPath;
		}

	}
}
