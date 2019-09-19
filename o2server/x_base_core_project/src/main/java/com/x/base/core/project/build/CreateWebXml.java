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

		File directory = new File(args[0]);
		String project = args[1];

		String thisApplicationClassName = thisApplicationClassName(project);
		String xml = metadataCompleteFalse(project, thisApplicationClassName);
		File dir = new File(directory, "src/main/webapp/WEB-INF");
		FileUtils.forceMkdir(dir);
		File file = new File(dir, "web.xml");
		FileUtils.writeStringToFile(file, xml, DefaultCharset.charset_utf_8);
		System.out.println("create web.xml:" + file.getAbsolutePath());
	}

	private static String metadataCompleteFalse(String project, String thisApplicationClassName) throws Exception {
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

}
