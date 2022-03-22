package com.x.base.core.entity.tools;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;

import org.apache.commons.io.FileUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.QName;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import com.x.base.core.project.tools.MainTools;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;

public class EnhancePersistenceXmlWriter {
	public static void main(String[] args) throws Exception {
		Argument arg = MainTools.parseArgument(args[0], Argument.class);
		write(arg);
	}

	private static void write(Argument arg) throws Exception {
		try {
			Document document = DocumentHelper.createDocument();
			Element persistence = document.addElement("persistence", "http://java.sun.com/xml/ns/persistence");
			persistence.addAttribute(QName.get("schemaLocation", "xsi", "http://www.w3.org/2001/XMLSchema-instance"),
					"http://java.sun.com/xml/ns/persistence  http://java.sun.com/xml/ns/persistence/persistence_2_0.xsd");
			persistence.addAttribute("version", "2.0");
			Element unit = persistence.addElement("persistence-unit");
			unit.addAttribute("name", "enhance");
			Set<Class<?>> classes = new HashSet<>();
			for (Class<?> o : scanEnhanceClass()) {
				classes.addAll(scanMappedSuperclass(o));
			}
			for (Class<?> o : classes) {
				Element element = unit.addElement("class");
				element.addText(o.getCanonicalName());
			}
			OutputFormat format = OutputFormat.createPrettyPrint();
			format.setEncoding("UTF-8");
			File dir = new File(arg.getPath(), "target/classes/META-INF");
			// File dir = new File(arg.getPath(), "src/main/resources/META-INF");
			FileUtils.forceMkdir(dir);
			File file = new File(dir, "persistence.xml");
			XMLWriter writer = new XMLWriter(new FileWriter(file), format);
			writer.write(document);
			writer.close();
			System.out.println("create enhance persistence.xml at path:" + arg.getPath());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static List<Class<?>> scanEnhanceClass() throws Exception {
		List<Class<?>> list = new ArrayList<Class<?>>();
		try (ScanResult scanResult = new ClassGraph().enableAnnotationInfo().scan()) {
			List<ClassInfo> classInfos = scanResult.getClassesWithAnnotation(Entity.class.getName());
			for (ClassInfo info : classInfos) {
				list.add(Thread.currentThread().getContextClassLoader().loadClass(info.getName()));
			}
			return list.stream().sorted(Comparator.comparing(Class::getName)).collect(Collectors.toList());
		}
	}

	private static Set<Class<?>> scanMappedSuperclass(Class<?> clz) throws Exception {
		Set<Class<?>> set = new HashSet<Class<?>>();
		set.add(clz);
		Class<?> s = clz.getSuperclass();
		while (null != s) {
			if (null != s.getAnnotation(MappedSuperclass.class)) {
				set.add(s);
			}
			s = s.getSuperclass();
		}
		return set;
	}

	public class Argument {
		private String path;

		public String getPath() {
			return path;
		}

		public void setPath(String path) {
			this.path = path;
		}
	}
}