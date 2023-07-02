package com.x.base.core.entity.tools;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

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

public class EnhanceBaseBuilder {

	public static void main(String[] args) throws Exception {

		File directory = new File(args[0]);
		File outputdir = new File(args[1]);

		List<Class<?>> classes = new ArrayList<>();

		classes.add(JpaObject.class);
		classes.add(SliceJpaObject.class);
		classes.add(StorageObject.class);

		File xml = createPernsistenceXml(classes, directory);

		Options opts = new Options();
		opts.setFromCmdLine(new String[] { "-p", xml.getAbsolutePath() });

		PCEnhancer.run(toPath(classes, outputdir), opts);

	}

	private static File createPernsistenceXml(List<Class<?>> classes, File directory) throws Exception {
		Document document = DocumentHelper.createDocument();
		Element persistence = document.addElement("persistence", "http://java.sun.com/xml/ns/persistence");
		persistence.addAttribute(QName.get("schemaLocation", "xsi", "http://www.w3.org/2001/XMLSchema-instance"),
				"http://java.sun.com/xml/ns/persistence  http://java.sun.com/xml/ns/persistence/persistence_2_0.xsd");
		persistence.addAttribute("version", "2.0");
		Element unit = persistence.addElement("persistence-unit");
		unit.addAttribute("name", "enhance");
		for (Class<?> o : classes) {
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
					cls.getName().replaceAll("\\.", Matcher.quoteReplacement(File.separator)) + ".class");
			list.add(file.getAbsolutePath());
		}
		String[] arr = new String[list.size()];
		return list.toArray(arr);
	}

}
