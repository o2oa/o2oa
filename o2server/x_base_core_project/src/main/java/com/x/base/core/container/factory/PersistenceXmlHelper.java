package com.x.base.core.container.factory;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.openjpa.persistence.PersistenceProviderImpl;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.QName;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import com.x.base.core.container.FactorDistributionPolicy;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.tools.JpaObjectTools;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.tools.ListTools;

public class PersistenceXmlHelper {

	public static List<String> directWrite(String path, List<String> classNames) throws Exception {
		try {
			Document document = DocumentHelper.createDocument();
			Element persistence = document.addElement("persistence", "http://java.sun.com/xml/ns/persistence");
			persistence.addAttribute(QName.get("schemaLocation", "xsi", "http://www.w3.org/2001/XMLSchema-instance"),
					"http://java.sun.com/xml/ns/persistence http://java.sun.com/xml/ns/persistence/persistence_2_0.xsd");
			persistence.addAttribute("version", "2.0");
			for (String className : classNames) {
				Element unit = persistence.addElement("persistence-unit");
				unit.addAttribute("name", className);
				unit.addAttribute("transaction-type", "RESOURCE_LOCAL");
				Element provider = unit.addElement("provider");
				provider.addText(PersistenceProviderImpl.class.getName());
				Element mapped_element = unit.addElement("class");
				mapped_element.addText(className);
				Element sliceJpaObject_element = unit.addElement("class");
				sliceJpaObject_element.addText("com.x.base.core.entity.SliceJpaObject");
				Element jpaObject_element = unit.addElement("class");
				jpaObject_element.addText("com.x.base.core.entity.JpaObject");
			}
			OutputFormat format = OutputFormat.createPrettyPrint();
			format.setEncoding("UTF-8");
			File file = new File(path);
			FileUtils.touch(file);
			XMLWriter writer = new XMLWriter(new FileWriter(file), format);
			writer.write(document);
			writer.close();
			return classNames;
		} catch (Exception e) {
			throw new Exception("registContainerEntity error.className:" + ListTools.toStringJoin(classNames), e);
		}
	}

	public static List<String> write(String path, List<String> entities) throws Exception {
		List<String> names = new ArrayList<>();
		String name = "";
		try {
			names.addAll((List<String>) Config.resource(Config.RESOUCE_CONTAINERENTITYNAMES));
			names = ListTools.includesExcludesWildcard(names, entities, null);
			Document document = DocumentHelper.createDocument();
			Element persistence = document.addElement("persistence", "http://java.sun.com/xml/ns/persistence");
			persistence.addAttribute(QName.get("schemaLocation", "xsi", "http://www.w3.org/2001/XMLSchema-instance"),
					"http://java.sun.com/xml/ns/persistence http://java.sun.com/xml/ns/persistence/persistence_2_0.xsd");
			persistence.addAttribute("version", "2.0");
			for (String className : names) {
				name = className;
				Class<? extends JpaObject> clazz = (Class<JpaObject>) Class.forName(className);
				Element unit = persistence.addElement("persistence-unit");
				unit.addAttribute("name", className);
				unit.addAttribute("transaction-type", "RESOURCE_LOCAL");
				Element provider = unit.addElement("provider");
				provider.addText(PersistenceProviderImpl.class.getName());
				for (Class<?> o : JpaObjectTools.scanMappedSuperclass(clazz)) {
					Element mapped_element = unit.addElement("class");
					mapped_element.addText(o.getName());
				}
			}
			OutputFormat format = OutputFormat.createPrettyPrint();
			format.setEncoding("UTF-8");
			File file = new File(path);
			FileUtils.touch(file);
			XMLWriter writer = new XMLWriter(new FileWriter(file), format);
			writer.write(document);
			writer.close();
			return names;
		} catch (Exception e) {
			throw new Exception("registContainerEntity error.className:" + name, e);
		}
	}

	public static Properties properties(String className, boolean sliceFeatureEnable) throws Exception {
		if (sliceFeatureEnable) {
			if (Config.externalDataSources().enable()) {
				return properties_external_slice(className);
			} else {
				return properties_internal_slice(className);
			}
		} else {
			if (Config.externalDataSources().enable()) {
				return properties_external_single(className);
			} else {
				return properties_internal_single(className);
			}
		}

	}

	private static Properties properties_base_slice(String className) throws Exception {
		Properties properties = new Properties();
		properties.put("openjpa.BrokerFactory", "slice");
		properties.put("openjpa.slice.Lenient", "false");
		properties.put("openjpa.slice.DistributionPolicy", FactorDistributionPolicy.class.getName());
		properties.put("openjpa.QueryCompilationCache", "false");
		properties.put("openjpa.IgnoreChanges", "true");
		properties.put("openjpa.QueryCache", "false");
		properties.put("openjpa.QueryCompilationCache", "false");
		properties.put("openjpa.LockManager", "none");
		properties.put("openjpa.jdbc.ResultSetType", "scroll-insensitive");
		/* 如果启用本地初始化会导致classLoad的问题 */
		properties.put("openjpa.DynamicEnhancementAgent", "false");
		properties.put("openjpa.jdbc.SynchronizeMappings", "buildSchema(ForeignKeys=false)");
		properties.put("openjpa.Log", "DefaultLevel=WARN");
		return properties;
	}

	private static Properties properties_external_slice(String className) throws Exception {
		Properties properties = properties_base_slice(className);
		properties.put("openjpa.jdbc.DBDictionary", Config.externalDataSources().dictionary());
		/* 如果是DB2 添加 Schema,mysql 不需要Schema 如果用了Schema H2数据库就会报错说没有Schema */
		if (Config.externalDataSources().hasSchema()) {
			properties.put("openjpa.jdbc.Schema", JpaObject.default_schema);
		}
		properties.put("openjpa.slice.Names",
				StringUtils.join(Config.externalDataSources().findNamesOfContainerEntity(className), ","));
		for (String name : Config.externalDataSources().findNamesOfContainerEntity(className)) {
			properties.put("openjpa.slice." + name + ".ConnectionFactoryName", Config.RESOUCE_JDBC_PREFIX + name);
			properties.put("openjpa.slice." + name + ".Log", Config.externalDataSources().log(name));
		}
		return properties;
	}

	private static Properties properties_internal_slice(String className) throws Exception {
		Properties properties = properties_base_slice(className);
		properties.put("openjpa.jdbc.DBDictionary", SlicePropertiesBuilder.dictionary_h2);
		properties.put("openjpa.slice.Names",
				StringUtils.join(Config.nodes().dataServers().findNamesOfContainerEntity(className), ","));
		for (String name : Config.nodes().dataServers().findNamesOfContainerEntity(className)) {
			properties.put("openjpa.slice." + name + ".ConnectionFactoryName", Config.RESOUCE_JDBC_PREFIX + name);
			properties.put("openjpa.slice." + name + ".Log", Config.nodes().dataServers().log(name));
		}
		return properties;
	}

	private static Properties properties_base_single(String className) throws Exception {
		Properties properties = new Properties();
		properties.put("openjpa.QueryCompilationCache", "false");
		properties.put("openjpa.IgnoreChanges", "true");
		properties.put("openjpa.QueryCache", "false");
		properties.put("openjpa.QueryCompilationCache", "false");
		properties.put("openjpa.LockManager", "none");
		properties.put("openjpa.jdbc.ResultSetType", "scroll-insensitive");
		properties.put("openjpa.Multithreaded", "true");
		/* 如果启用本地初始化会导致classLoad的问题 */
		properties.put("openjpa.DynamicEnhancementAgent", "false");
		properties.put("openjpa.jdbc.SynchronizeMappings", "buildSchema(ForeignKeys=false)");
		return properties;
	}

	private static Properties properties_external_single(String className) throws Exception {
		Properties properties = properties_base_single(className);
		properties.put("openjpa.jdbc.DBDictionary", Config.externalDataSources().dictionary());
		/* 如果是DB2 添加 Schema,mysql 不需要Schema 如果用了Schema H2数据库就会报错说没有Schema */
		if (Config.externalDataSources().hasSchema()) {
			properties.put("openjpa.jdbc.Schema", JpaObject.default_schema);
		}
		for (String name : Config.externalDataSources().findNamesOfContainerEntity(className)) {
			properties.put("openjpa.ConnectionFactoryName", Config.RESOUCE_JDBC_PREFIX + name);
			properties.put("openjpa.Log", Config.externalDataSources().log(name));
			break;
		}
		return properties;
	}

	private static Properties properties_internal_single(String className) throws Exception {
		Properties properties = properties_base_single(className);
		properties.put("openjpa.jdbc.DBDictionary", SlicePropertiesBuilder.dictionary_h2);
		for (String name : Config.nodes().dataServers().findNamesOfContainerEntity(className)) {
			properties.put("openjpa.ConnectionFactoryName", Config.RESOUCE_JDBC_PREFIX + name);
			properties.put("openjpa.Log", Config.nodes().dataServers().log(name));
			break;
		}
		return properties;
	}

}