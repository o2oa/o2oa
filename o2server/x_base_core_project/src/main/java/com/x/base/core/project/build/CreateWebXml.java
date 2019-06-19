package com.x.base.core.project.build;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebServlet;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.Compilable;
import com.x.base.core.project.annotation.Module;
import com.x.base.core.project.tools.DefaultCharset;
import com.x.base.core.project.tools.MainTools;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;

public class CreateWebXml {
	public static void main(String[] args) throws Exception {
		Argument arg = MainTools.parseArgument(args[0], Argument.class);
		String thisApplicationClassName = thisApplicationClassName(arg.getProject());
//		String xml = metadataCompleteTrue(arg.getPath(), arg.getProject(), thisApplicationClassName);
		String xml = metadataCompleteFalse(arg.getPath(), arg.getProject(), thisApplicationClassName);
		File dir = new File(arg.getPath(), "src/main/webapp/WEB-INF");
		FileUtils.forceMkdir(dir);
		File file = new File(dir, "web.xml");
		FileUtils.writeStringToFile(file, xml, DefaultCharset.charset_utf_8);
		System.out.println("create web.xml:" + file.getAbsolutePath());
	}

	private static String metadataCompleteTrue(String path, String project, String thisApplicationClassName)
			throws Exception {
		Class<?> thisApplication = Class.forName(thisApplicationClassName);
		try (ScanResult scanResult = new ClassGraph().enableAnnotationInfo()
				.whitelistPackages(thisApplication.getPackage().getName(), Compilable.class.getPackage().getName())
				.scan()) {
			StringBuffer sb = new StringBuffer();
			String moduleClassName = moduleClassName(scanResult, project);
			sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			sb.append("<web-app id=\"" + project + "\" metadata-complete=\"true\" version=\"3.0\">");
			sb.append("<display-name>" + project + "</display-name>");
			sb.append("<context-param>");
			sb.append("<param-name>project</param-name>");
			sb.append("<param-value>" + moduleClassName + "</param-value>");
			sb.append("</context-param>");
			sb.append("<listener>");
			sb.append("<listener-class>" + thisApplication.getPackage().getName()
					+ ".ApplicationServletContextListener</listener-class>");
			sb.append("</listener>");
			sb.append("<servlet>");
			sb.append("<servlet-name>ActionApplication</servlet-name>");
			sb.append("<servlet-class>org.glassfish.jersey.servlet.ServletContainer</servlet-class>");
			sb.append("<async-supported>true</async-supported>");
			sb.append("<init-param>");
			sb.append("<param-name>javax.ws.rs.Application</param-name>");
			sb.append("<param-value>" + thisApplication.getPackage().getName()
					+ ".jaxrs.ActionApplication</param-value>");
			sb.append("</init-param>");
			sb.append("</servlet>");
			for (String str : listWebFilter(scanResult, path)) {
				Class<?> cls = Class.forName(str);
				WebFilter webFilter = cls.getAnnotation(WebFilter.class);
				sb.append("<filter>");
				sb.append("<filter-name>" + cls.getSimpleName() + "</filter-name>");
				sb.append("<filter-class>" + cls.getName() + "</filter-class>");
				sb.append("<async-supported>true</async-supported>");
				sb.append("</filter>");
				sb.append("<filter-mapping>");
				sb.append("<filter-name>" + cls.getSimpleName() + "</filter-name>");
				sb.append("<url-pattern>" + StringUtils.join(webFilter.urlPatterns(), ",") + "</url-pattern>");
				sb.append("</filter-mapping>");
			}
			for (String str : listWebServlet(scanResult, path)) {
				Class<?> cls = Class.forName(str);
				WebServlet webServlet = cls.getAnnotation(WebServlet.class);
				sb.append("<servlet>");
				sb.append("<servlet-name>" + cls.getSimpleName() + "</servlet-name>");
				sb.append("<servlet-class>" + cls.getName() + "</servlet-class>");
				sb.append("<async-supported>true</async-supported>");
				sb.append("</servlet>");
				sb.append("<servlet-mapping>");
				sb.append("<servlet-name>" + cls.getSimpleName() + "</servlet-name>");
				sb.append("<url-pattern>" + StringUtils.join(webServlet.urlPatterns(), ",") + "</url-pattern>");
				sb.append("<servlet-mapping>");
			}
			sb.append("</web-app>");
			return sb.toString();
		}
	}

	private static String metadataCompleteFalse(String path, String project, String thisApplicationClassName)
			throws Exception {
		Class<?> thisApplication = Class.forName(thisApplicationClassName);
		try (ScanResult scanResult = new ClassGraph().enableAnnotationInfo()
				.whitelistPackages(thisApplication.getPackage().getName(), Compilable.class.getPackage().getName())
				.scan()) {
			StringBuffer sb = new StringBuffer();
			String moduleClassName = moduleClassName(scanResult, project);
			sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			sb.append("<web-app id=\"" + project + "\" metadata-complete=\"false\" version=\"3.0\">");
			sb.append("<display-name>" + project + "</display-name>");
			sb.append("<context-param>");
			sb.append("<param-name>project</param-name>");
			sb.append("<param-value>" + moduleClassName + "</param-value>");
			sb.append("</context-param>");
			sb.append("</web-app>");
			return sb.toString();
		}
	}

	private static String moduleClassName(ScanResult scanResult, String project) throws Exception {
		List<ClassInfo> classInfos = scanResult.getClassesWithAnnotation(Module.class.getName());
		for (ClassInfo info : classInfos) {
			if (StringUtils.equals(info.getSimpleName(), project)) {
				return info.getName();
			}
		}
		return null;
	}

	private static List<String> listWebServlet(ScanResult scanResult, String project) throws Exception {
		List<String> list = new ArrayList<>();
		List<ClassInfo> classInfos = scanResult.getClassesWithAnnotation(WebServlet.class.getName());
		for (ClassInfo info : classInfos) {
			list.add(info.getName());
		}
		return list;
	}

	private static List<String> listWebFilter(ScanResult scanResult, String project) throws Exception {
		List<String> list = new ArrayList<>();
//		list.add(CacheJaxrsFilter.class.getName());
//		list.add(LoggerJaxrsFilter.class.getName());
//		list.add(EchoJaxrsFilter.class.getName());
		List<ClassInfo> classInfos = scanResult.getClassesWithAnnotation(WebFilter.class.getName());
		for (ClassInfo info : classInfos) {
			list.add(info.getName());
		}
		return list;
	}

	private static String thisApplicationClassName(String project) throws Exception {
		try (ScanResult scanResult = new ClassGraph().enableAnnotationInfo().scan()) {
			List<ClassInfo> classInfos = scanResult.getClassesWithAnnotation(Module.class.getName());
			for (ClassInfo info : classInfos) {
				if (StringUtils.equals(info.getSimpleName(), project)) {
					Class<?> cls = Class.forName(info.getName());
					Module module = cls.getAnnotation(Module.class);
					return module.packageName() + ".ThisApplication";
				}
			}
			return null;
		}
	}

	public class Argument {
		private String path;
		private String project;

		public String getPath() {
			return path;
		}

		public void setPath(String path) {
			this.path = path;
		}

		public String getProject() {
			return project;
		}

		public void setProject(String project) {
			this.project = project;
		}
	}
}
