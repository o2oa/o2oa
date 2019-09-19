package com.x.base.core.entity.tools;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;

import org.apache.openjpa.enhance.PCEnhancer;
import org.apache.openjpa.lib.util.Options;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.QName;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.StorageObject;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;

public class EnhanceBuilder {

	public static void main(String[] args) throws Exception {

		File directory = new File(args[0]);
		File outputdir = new File(args[1]);

		List<Class<?>> classes = scanEnhanceClass();

		if (!classes.isEmpty()) {

			File xml = createPernsistenceXml(classes, directory);

			Options opts = new Options();
			opts.setFromCmdLine(new String[] { "-p", xml.getAbsolutePath() });

			PCEnhancer.run(toPath(classes, outputdir), opts);

		}
	}

	private static File createPernsistenceXml(List<Class<?>> classes, File directory) throws Exception {
		Document document = DocumentHelper.createDocument();
		Element persistence = document.addElement("persistence", "http://java.sun.com/xml/ns/persistence");
		persistence.addAttribute(QName.get("schemaLocation", "xsi", "http://www.w3.org/2001/XMLSchema-instance"),
				"http://java.sun.com/xml/ns/persistence  http://java.sun.com/xml/ns/persistence/persistence_2_0.xsd");
		persistence.addAttribute("version", "2.0");
		Element unit = persistence.addElement("persistence-unit");
		unit.addAttribute("name", "enhance");
		Set<Class<?>> set = new HashSet<>();
		for (Class<?> o : classes) {
			set.addAll(scanMappedSuperclass(o));
		}
		for (Class<?> o : set) {
			Element element = unit.addElement("class");
			element.addText(o.getCanonicalName());
		}
		OutputFormat format = OutputFormat.createPrettyPrint();
		format.setEncoding("UTF-8");
		File file = new File(directory, "persistence.xml");
		XMLWriter writer = new XMLWriter(new FileWriter(file), format);
		writer.write(document);
		writer.close();
		return file;
	}

	private static String[] toPath(List<Class<?>> classes, File outputdir) throws Exception {
		List<String> list = new ArrayList<>();
		for (Class<?> cls : classes) {
			File file = new File(outputdir,
					cls.getTypeName().replaceAll("\\.", Matcher.quoteReplacement(File.separator)) + ".class");
			list.add(file.getAbsolutePath());
		}
		String[] arr = new String[list.size()];
		return list.toArray(arr);
	}

	private static List<Class<?>> scanEnhanceClass() throws Exception {
		List<Class<?>> list = new ArrayList<Class<?>>();
		try (ScanResult sr = new ClassGraph().enableAnnotationInfo().disableJarScanning().scan()) {
			for (ClassInfo info : sr.getClassesWithAnnotation(Entity.class.getName())) {
				list.add(Class.forName(info.getName()));
			}
			for (ClassInfo info : sr.getClassesWithAnnotation(MappedSuperclass.class.getName())) {
				list.add(Class.forName(info.getName()));
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

}
