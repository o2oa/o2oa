package com.x.base.core.container.factory;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.BooleanUtils;
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
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.entity.dynamic.DynamicBaseEntity;
import com.x.base.core.entity.dynamic.DynamicEntity;
import com.x.base.core.entity.tools.JpaObjectTools;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.config.Node;
import com.x.base.core.project.tools.ClassLoaderTools;
import com.x.base.core.project.tools.ListTools;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;

public class PersistenceXmlHelper {

	private PersistenceXmlHelper() {

	}

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

	public static void writeForDdl(String path) throws Exception {
		try {
			Document document = DocumentHelper.createDocument();
			Element persistence = document.addElement("persistence", "http://java.sun.com/xml/ns/persistence");
			persistence.addAttribute(QName.get("schemaLocation", "xsi", "http://www.w3.org/2001/XMLSchema-instance"),
					"http://java.sun.com/xml/ns/persistence http://java.sun.com/xml/ns/persistence/persistence_2_0.xsd");
			persistence.addAttribute("version", "2.0");
			Element unit = persistence.addElement("persistence-unit");
			unit.addAttribute("name", "enhance");
			unit.addAttribute("transaction-type", "RESOURCE_LOCAL");
			Element provider = unit.addElement("provider");
			provider.addText(PersistenceProviderImpl.class.getName());
			List<String> entities = new ArrayList<>();
			for (String className : (List<String>) Config.resource(Config.RESOURCE_CONTAINERENTITYNAMES)) {
				Class<? extends JpaObject> clazz = (Class<JpaObject>) Class.forName(className);
				for (Class<?> o : JpaObjectTools.scanMappedSuperclass(clazz)) {
					entities.add(o.getName());
				}
			}
			entities = ListTools.trim(entities, true, true);
			for (String className : entities) {
				Element class_element = unit.addElement("class");
				class_element.addText(className);
			}
			Element properties = unit.addElement("properties");
			if (BooleanUtils.isTrue(Config.externalDataSources().enable())) {
				writeForDdlExternalProperty(properties);
			} else {
				writeForDdlInternalProperty(properties);
			}
			OutputFormat format = OutputFormat.createPrettyPrint();
			format.setEncoding("UTF-8");
			File file = new File(path);
			FileUtils.touch(file);
			XMLWriter writer = new XMLWriter(new FileWriter(file), format);
			writer.write(document);
			writer.close();
		} catch (Exception e) {
			throw new Exception("writeForDdl error.", e);
		}
	}

	private static void writeForDdlExternalProperty(Element properties) throws Exception {
		Element property = properties.addElement("property");
		property.addAttribute("name", "javax.persistence.jdbc.driver");
		property.addAttribute("value", Config.externalDataSources().get(0).getDriverClassName());
		property = properties.addElement("property");
		property.addAttribute("name", "javax.persistence.jdbc.url");
		property.addAttribute("value", Config.externalDataSources().get(0).getUrl());
		property = properties.addElement("property");
		property.addAttribute("name", "javax.persistence.jdbc.user");
		property.addAttribute("value", Config.externalDataSources().get(0).getUsername());
		property = properties.addElement("property");
		property.addAttribute("name", "javax.persistence.jdbc.password");
		property.addAttribute("value", Config.externalDataSources().get(0).getPassword());
		property = properties.addElement("property");
		property.addAttribute("name", "openjpa.DynamicEnhancementAgent");
		property.addAttribute("value", "false");
	}

	private static void writeForDdlInternalProperty(Element properties) throws Exception {
		Element property = properties.addElement("property");
		property.addAttribute("name", "javax.persistence.jdbc.driver");
		property.addAttribute("value", SlicePropertiesBuilder.driver_h2);
		property = properties.addElement("property");
		property.addAttribute("name", "javax.persistence.jdbc.url");
		Node node = Config.currentNode();
		String url = "jdbc:h2:tcp://" + Config.node() + ":" + node.getData().getTcpPort() + "/X;JMX="
				+ (node.getData().getJmxEnable() ? "TRUE" : "FALSE") + ";CACHE_SIZE="
				+ (node.getData().getCacheSize() * 1024);
		property.addAttribute("value", url);
		property = properties.addElement("property");
		property.addAttribute("name", "javax.persistence.jdbc.user");
		property.addAttribute("value", "sa");
		property = properties.addElement("property");
		property.addAttribute("name", "javax.persistence.jdbc.password");
		property.addAttribute("value", Config.token().getPassword());
		property = properties.addElement("property");
		property.addAttribute("name", "openjpa.DynamicEnhancementAgent");
		property.addAttribute("value", "false");
	}

	@SuppressWarnings("unchecked")
	public static List<String> write(String path, List<String> entities, boolean loadDynamic) {
		List<String> names = new ArrayList<>();
		String name = "";
		try {
			names.addAll((List<String>) Config.resource(Config.RESOURCE_CONTAINERENTITYNAMES));
			names = ListTools.includesExcludesWildcard(names, entities, null);
			Document document = DocumentHelper.createDocument();
			Element persistence = document.addElement("persistence", "http://java.sun.com/xml/ns/persistence");
			persistence.addAttribute(QName.get("schemaLocation", "xsi", "http://www.w3.org/2001/XMLSchema-instance"),
					"http://java.sun.com/xml/ns/persistence http://java.sun.com/xml/ns/persistence/persistence_2_0.xsd");
			persistence.addAttribute("version", "2.0");
			ClassLoader cl = Thread.currentThread().getContextClassLoader();
			for (String className : names) {
				name = className;
				Class<? extends JpaObject> clazz = (Class<JpaObject>) cl.loadClass(className);
				Element unit = persistence.addElement("persistence-unit");
				unit.addAttribute("name", className);
				unit.addAttribute("transaction-type", "RESOURCE_LOCAL");
				Element provider = unit.addElement("provider");
				provider.addText(PersistenceProviderImpl.class.getName());
				for (Class<?> o : JpaObjectTools.scanMappedSuperclass(clazz)) {
					unit.addElement("class").addText(o.getName());
				}
			}
			if (loadDynamic) {
				addDynamicClass(persistence);
//				for (String className : names) {
//					if (className.startsWith(DynamicEntity.CLASS_PACKAGE)) {
//						dyClasses.add(className);
//					}
//				}
//				if (!dyClasses.isEmpty()) {
//					String dyClassName = DynamicBaseEntity.class.getName();
//					names.add(dyClassName);
//
//					Element unit = persistence.addElement("persistence-unit");
//					unit.addAttribute("name", dyClassName);
//					unit.addAttribute("transaction-type", "RESOURCE_LOCAL");
//					Element provider = unit.addElement("provider");
//					provider.addText(PersistenceProviderImpl.class.getName());
//					for (String dyClass : dyClasses) {
//						unit.addElement("class").addText(dyClass);
//					}
//					for (Class<?> o : JpaObjectTools.scanMappedSuperclass(DynamicBaseEntity.class)) {
//						if (!o.getName().equals(DynamicBaseEntity.class.getName())) {
//							unit.addElement("class").addText(o.getName());
//						}
//					}
//				}
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
			throw new IllegalStateException("write error.className:" + name, e);
		}
	}

	private static void addDynamicClass(Element persistence) throws Exception {
		Set<String> names = new TreeSet<>();
		ClassLoader cl = ClassLoaderTools.urlClassLoader(true, false, false, false, true);
		try (ScanResult sr = new ClassGraph().addClassLoader(cl).enableAnnotationInfo().scan()) {
			for (ClassInfo info : sr.getClassesWithAnnotation(ContainerEntity.class.getName())) {
				Class<?> cls = cl.loadClass(info.getName());
				if (StringUtils.startsWith(cls.getName(), DynamicEntity.CLASS_PACKAGE)) {
					names.add(cls.getName());
					for (Class<?> o : JpaObjectTools.scanMappedSuperclass(cls)) {
						names.add(o.getName());
					}
				}
			}
		}
		Element unit = persistence.addElement("persistence-unit");
		unit.addAttribute("name", DynamicBaseEntity.class.getName());
		unit.addAttribute("transaction-type", "RESOURCE_LOCAL");
		Element provider = unit.addElement("provider");
		provider.addText(PersistenceProviderImpl.class.getName());
		for (String name : names) {
			unit.addElement("class").addText(name);
		}

	}

	public static Properties properties(String className, boolean sliceFeatureEnable) throws Exception {
		if (sliceFeatureEnable) {
			if (BooleanUtils.isTrue(Config.externalDataSources().enable())) {
				return propertiesExternalSlice(className);
			} else {
				return propertiesInternalSlice(className);
			}
		} else {
			if (BooleanUtils.isTrue(Config.externalDataSources().enable())) {
				return propertiesExternalSingle(className);
			} else {
				return propertiesInternalSingle(className);
			}
		}
	}

	private static Properties propertiesBaseSlice(String className) throws Exception {
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

	private static Properties propertiesExternalSlice(String className) throws Exception {
		Properties properties = propertiesBaseSlice(className);
		properties.put("openjpa.jdbc.DBDictionary", Config.externalDataSources().dictionary());
		/* 如果是DB2 添加 Schema,mysql 不需要Schema 如果用了Schema H2数据库就会报错说没有Schema */
		if (Config.externalDataSources().hasSchema()) {
			properties.put("openjpa.jdbc.Schema", JpaObject.default_schema);
		}
		properties.put("openjpa.slice.Names",
				StringUtils.join(Config.externalDataSources().findNamesOfContainerEntity(className), ","));
		for (String name : Config.externalDataSources().findNamesOfContainerEntity(className)) {
			properties.put("openjpa.slice." + name + ".ConnectionFactoryName", Config.RESOURCE_JDBC_PREFIX + name);
			properties.put("openjpa.slice." + name + ".Log", Config.externalDataSources().log(name));
		}
		return properties;
	}

	private static Properties propertiesInternalSlice(String className) throws Exception {
		Properties properties = propertiesBaseSlice(className);
		properties.put("openjpa.jdbc.DBDictionary", SlicePropertiesBuilder.dictionary_h2);
		properties.put("openjpa.slice.Names",
				StringUtils.join(Config.nodes().dataServers().findNamesOfContainerEntity(className), ","));
		for (String name : Config.nodes().dataServers().findNamesOfContainerEntity(className)) {
			properties.put("openjpa.slice." + name + ".ConnectionFactoryName", Config.RESOURCE_JDBC_PREFIX + name);
			properties.put("openjpa.slice." + name + ".Log", Config.nodes().dataServers().log(name));
		}
		return properties;
	}

	private static Properties propertiesBaseSingle(String className) throws Exception {
		Properties properties = new Properties();
		properties.put("openjpa.QueryCompilationCache", "false");
		properties.put("openjpa.IgnoreChanges", "true");
		properties.put("openjpa.QueryCache", "false");
		properties.put("openjpa.QueryCompilationCache", "false");
		properties.put("openjpa.LockManager", "none");
		properties.put("openjpa.jdbc.ResultSetType", "scroll-insensitive");
		// 使用ture支持多线程访问,但是是通过lock同步执行的.
		properties.put("openjpa.Multithreaded", "true");
		/* 如果启用本地初始化会导致classLoad的问题 */
		properties.put("openjpa.DynamicEnhancementAgent", "false");
		properties.put("openjpa.jdbc.SynchronizeMappings", "buildSchema(ForeignKeys=false)");
		return properties;
	}

	private static Properties propertiesExternalSingle(String className) throws Exception {
		Properties properties = propertiesBaseSingle(className);
		properties.put("openjpa.jdbc.DBDictionary", Config.externalDataSources().dictionary());
		/* 如果是DB2 添加 Schema,mysql 不需要Schema 如果用了Schema H2数据库就会报错说没有Schema */
		if (Config.externalDataSources().hasSchema()) {
			properties.put("openjpa.jdbc.Schema", JpaObject.default_schema);
		}
		if (StringUtils.isNotEmpty(Config.externalDataSources().getTransactionIsolation())) {
			properties.put("openjpa.jdbc.TransactionIsolation", Config.externalDataSources().getTransactionIsolation());
		}
		for (String name : Config.externalDataSources().findNamesOfContainerEntity(className)) {
			properties.put("openjpa.ConnectionFactoryName", Config.RESOURCE_JDBC_PREFIX + name);
			properties.put("openjpa.Log", Config.externalDataSources().log(name));
			break;
		}
		return properties;
	}

	private static Properties propertiesInternalSingle(String className) throws Exception {
		Properties properties = propertiesBaseSingle(className);
		properties.put("openjpa.jdbc.DBDictionary", SlicePropertiesBuilder.dictionary_h2);
		for (String name : Config.nodes().dataServers().findNamesOfContainerEntity(className)) {
			properties.put("openjpa.ConnectionFactoryName", Config.RESOURCE_JDBC_PREFIX + name);
			properties.put("openjpa.Log", Config.nodes().dataServers().log(name));
			break;
		}
		return properties;
	}

}