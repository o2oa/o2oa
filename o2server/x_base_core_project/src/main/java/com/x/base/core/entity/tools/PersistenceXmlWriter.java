package com.x.base.core.entity.tools;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.persistence.MappedSuperclass;

import org.apache.commons.lang3.StringUtils;
import org.apache.openjpa.persistence.PersistenceProviderImpl;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.QName;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import com.x.base.core.project.Deployable;
import com.x.base.core.project.annotation.Module;
import com.x.base.core.project.tools.MainTools;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;

public class PersistenceXmlWriter {

	public static void main(String[] args) throws Exception {
		Argument arg = MainTools.parseArgument(args[0], Argument.class);
		write(arg);
	}

	private static void write(Argument arg) throws Exception {
		try {
			Document document = DocumentHelper.createDocument();
			Element persistence = document.addElement("persistence", "http://java.sun.com/xml/ns/persistence");
			persistence.addAttribute(QName.get("schemaLocation", "xsi", "http://www.w3.org/2001/XMLSchema-instance"),
					"http://java.sun.com/xml/ns/persistence http://java.sun.com/xml/ns/persistence/persistence_2_0.xsd");
			persistence.addAttribute("version", "2.0");
			for (Class<?> cls : scanContainerEntity(arg.getProject())) {
				Element unit = persistence.addElement("persistence-unit");
				unit.addAttribute("name", cls.getCanonicalName());
				unit.addAttribute("transaction-type", "RESOURCE_LOCAL");
				Element provider = unit.addElement("provider");
				provider.addText(PersistenceProviderImpl.class.getCanonicalName());
				for (Class<?> o : scanMappedSuperclass(cls)) {
					Element mapped_element = unit.addElement("class");
					mapped_element.addText(o.getCanonicalName());
				}
				Element slice_unit_properties = unit.addElement("properties");
				Map<String, String> properties = new LinkedHashMap<String, String>();
				for (Entry<String, String> entry : properties.entrySet()) {
					Element property = slice_unit_properties.addElement("property");
					property.addAttribute("name", entry.getKey());
					property.addAttribute("value", entry.getValue());
				}
			}
			OutputFormat format = OutputFormat.createPrettyPrint();
			format.setEncoding("UTF-8");
			File file = new File(arg.getPath());
			XMLWriter writer = new XMLWriter(new FileWriter(file), format);
			writer.write(document);
			writer.close();
			System.out.println("create persistence.xml at path:" + arg.getPath());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static List<Class<?>> scanContainerEntity(String project) throws Exception {
		List<Class<?>> list = new ArrayList<>();
		try (ScanResult scanResult = new ClassGraph().enableAnnotationInfo().scan()) {
			List<ClassInfo> classInfos = scanResult.getClassesWithAnnotation(Module.class.getName());
			for (ClassInfo info : classInfos) {
				if (StringUtils.equals(info.getSimpleName(), project)) {
					Class<?> cls = Class.forName(info.getName());
					Module moudle = cls.getAnnotation(Module.class);
					for (String str : moudle.containerEntities()) {
						if (StringUtils.isNotEmpty(str)) {
							list.add(Class.forName(str));
						}
					}
				}
			}
			return list;
		}
	}

//	@SuppressWarnings("unchecked")
//	private static List<Class<?>> scanContainerEntity(String project) throws Exception {
//		Set<Class<?>> ces = new HashSet<>();
//		String className = "com.x.base.core.project." + project;
//		Class<?> cls = Class.forName(className);
//		for (String str : (List<String>) FieldUtils.readStaticField(cls, "containerEntities")) {
//			if (StringUtils.isNotEmpty(str)) {
//				ces.add(Class.forName(str));
//			}
//		}
//		List<Class<?>> sortedList = new ArrayList<Class<?>>(ces);
//		Collections.sort(sortedList, new Comparator<Class<?>>() {
//			public int compare(Class<?> c1, Class<?> c2) {
//				return c1.getCanonicalName().compareTo(c2.getCanonicalName());
//			}
//		});
//		return sortedList;
//	}

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