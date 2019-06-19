package com.x.base.core.container.factory;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Id;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.openjpa.persistence.OpenJPAPersistence;
import org.apache.openjpa.persistence.PersistenceProductDerivation;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.CheckRemove;
import com.x.base.core.entity.annotation.Flag;
import com.x.base.core.entity.annotation.RestrictFlag;

public abstract class SliceEntityManagerContainerFactory {

	protected static String META_INF = "META-INF";
	protected static String PERSISTENCE_XML_PATH = META_INF + "/persistence.xml";

	/* class 与 entityManagerFactory 映射表 */
	protected Map<Class<? extends JpaObject>, EntityManagerFactory> entityManagerFactoryMap = new ConcurrentHashMap<Class<? extends JpaObject>, EntityManagerFactory>();
	/* class 与 @Flag字段 映射表 */
	protected Map<Class<? extends JpaObject>, List<Field>> flagMap = new ConcurrentHashMap<Class<? extends JpaObject>, List<Field>>();
	/* class 与 entityManagerFactory 映射表 */
	protected Map<Class<? extends JpaObject>, List<Field>> restrictFlagMap = new ConcurrentHashMap<Class<? extends JpaObject>, List<Field>>();
	/* class 与 class 中需要检查 Persist 字段的对应表 */
	protected Map<Class<? extends JpaObject>, Map<Field, CheckPersist>> checkPersistFieldMap = new ConcurrentHashMap<Class<? extends JpaObject>, Map<Field, CheckPersist>>();
	/* class 与 class 中需要检查 Remove 字段的对应表 */
	protected Map<Class<? extends JpaObject>, Map<Field, CheckRemove>> checkRemoveFieldMap = new ConcurrentHashMap<Class<? extends JpaObject>, Map<Field, CheckRemove>>();

	protected SliceEntityManagerContainerFactory(String webApplicationDirectory, List<String> entities)
			throws Exception {
		File path = new File(webApplicationDirectory + "/WEB-INF/classes/" + PERSISTENCE_XML_PATH);
		List<String> classNames = PersistenceXmlHelper.write(path.getAbsolutePath(), entities);
		for (String className : classNames) {
			Class<? extends JpaObject> clz = (Class<? extends JpaObject>) Class.forName(className);
			checkPersistFieldMap.put(clz, this.loadCheckPersistField(clz));
			checkRemoveFieldMap.put(clz, this.loadCheckRemoveField(clz));
			Properties properties = PersistenceXmlHelper.properties(clz.getName());
			entityManagerFactoryMap.put(clz,
					OpenJPAPersistence.createEntityManagerFactory(clz.getName(), PERSISTENCE_XML_PATH, properties));
			List<Field> flagFields = new ArrayList<>();
			List<Field> restrictFlagFields = new ArrayList<>();
			for (Field o : FieldUtils.getFieldsListWithAnnotation(clz, Id.class)) {
				flagFields.add(o);
				restrictFlagFields.add(o);
			}
			for (Field o : FieldUtils.getFieldsListWithAnnotation(clz, Flag.class)) {
				flagFields.add(o);
				restrictFlagFields.add(o);
			}
			for (Field o : FieldUtils.getFieldsListWithAnnotation(clz, RestrictFlag.class)) {
				restrictFlagFields.add(o);
			}
			flagMap.put(clz, Collections.unmodifiableList(flagFields));
			restrictFlagMap.put(clz, Collections.unmodifiableList(restrictFlagFields));
		}
	}

//	protected SliceEntityManagerContainerFactory(String webApplicationDirectory, DataMappings dataMappings,
//			List<String> entities) throws Exception {
//		Set<Class<? extends JpaObject>> classes = persistenceXml(webApplicationDirectory, dataMappings, entities);
//		for (Class<? extends JpaObject> clz : classes) {
//			checkPersistFieldMap.put(clz, this.loadCheckPersistField(clz));
//			checkRemoveFieldMap.put(clz, this.loadCheckRemoveField(clz));
//			entityManagerFactoryMap.put(clz,
//					OpenJPAPersistence.createEntityManagerFactory(clz.getName(), PERSISTENCE_XML_PATH));
//			List<Field> flagFields = new ArrayList<>();
//			List<Field> restrictFlagFields = new ArrayList<>();
//			for (Field o : FieldUtils.getFieldsListWithAnnotation(clz, Id.class)) {
//				flagFields.add(o);
//				restrictFlagFields.add(o);
//			}
//			for (Field o : FieldUtils.getFieldsListWithAnnotation(clz, Flag.class)) {
//				flagFields.add(o);
//				restrictFlagFields.add(o);
//			}
//			for (Field o : FieldUtils.getFieldsListWithAnnotation(clz, RestrictFlag.class)) {
//				restrictFlagFields.add(o);
//			}
//			flagMap.put(clz, Collections.unmodifiableList(flagFields));
//			restrictFlagMap.put(clz, Collections.unmodifiableList(restrictFlagFields));
//		}
//	}

	protected SliceEntityManagerContainerFactory(String source) throws Exception {
		Set<Class<? extends JpaObject>> classes = this.listUitClass(source);
		for (Class<? extends JpaObject> clz : classes) {
			checkPersistFieldMap.put(clz, this.loadCheckPersistField(clz));
			checkRemoveFieldMap.put(clz, this.loadCheckRemoveField(clz));
			Properties properties = PersistenceXmlHelper.properties(clz.getName());
			entityManagerFactoryMap.put(clz,
					OpenJPAPersistence.createEntityManagerFactory(clz.getName(), source, properties));
			List<Field> flagFields = new ArrayList<>();
			List<Field> restrictFlagFields = new ArrayList<>();
			for (Field o : FieldUtils.getFieldsListWithAnnotation(clz, Id.class)) {
				flagFields.add(o);
				restrictFlagFields.add(o);
			}
			for (Field o : FieldUtils.getFieldsListWithAnnotation(clz, Flag.class)) {
				flagFields.add(o);
				restrictFlagFields.add(o);
			}
			for (Field o : FieldUtils.getFieldsListWithAnnotation(clz, RestrictFlag.class)) {
				restrictFlagFields.add(o);
			}
			flagMap.put(clz, Collections.unmodifiableList(flagFields));
			restrictFlagMap.put(clz, Collections.unmodifiableList(restrictFlagFields));
		}
	}

//	/** 扫描受管实体,生成 x_perisitence.xml */
//	@SuppressWarnings("unchecked")
//	private Set<Class<? extends JpaObject>> persistenceXml(String webApplicationDirectory, DataMappings dataMappings,
//			List<String> entities) throws Exception {
//		String name = "";
//		Set<Class<? extends JpaObject>> classes = new HashSet<>();
//		try {
//			List<String> names = new ArrayList<>();
//			names.addAll(dataMappings.keySet());
//			names = ListTools.includesExcludesWildcard(names, entities, null);
//			Document document = DocumentHelper.createDocument();
//			Element persistence = document.addElement("persistence", "http://java.sun.com/xml/ns/persistence");
//			persistence.addAttribute(QName.get("schemaLocation", "xsi", "http://www.w3.org/2001/XMLSchema-instance"),
//					"http://java.sun.com/xml/ns/persistence http://java.sun.com/xml/ns/persistence/persistence_2_0.xsd");
//			persistence.addAttribute("version", "2.0");
//			for (String className : names) {
//				name = className;
//				Class<? extends JpaObject> clazz = (Class<JpaObject>) Class.forName(className);
//				Element unit = persistence.addElement("persistence-unit");
//				unit.addAttribute("name", className);
//				unit.addAttribute("transaction-type", "RESOURCE_LOCAL");
//				Element provider = unit.addElement("provider");
//				provider.addText(PersistenceProviderImpl.class.getName());
//				for (Class<?> o : JpaObjectTools.scanMappedSuperclass(clazz)) {
//					Element mapped_element = unit.addElement("class");
//					mapped_element.addText(o.getName());
//				}
//				Element slice_unit_properties = unit.addElement("properties");
//				for (Entry<String, String> entry : SlicePropertiesBuilder.getPropertiesDBCP(dataMappings.get(className))
//						.entrySet()) {
//					Element property = slice_unit_properties.addElement("property");
//					property.addAttribute("name", entry.getKey());
//					property.addAttribute("value", entry.getValue());
//				}
//				classes.add(clazz);
//			}
//			OutputFormat format = OutputFormat.createPrettyPrint();
//			format.setEncoding("UTF-8");
//			File dir = new File(webApplicationDirectory + "/WEB-INF/classes/" + META_INF);
//			FileUtils.forceMkdir(dir);
//			File file = new File(webApplicationDirectory + "/WEB-INF/classes/" + PERSISTENCE_XML_PATH);
//			XMLWriter writer = new XMLWriter(new FileWriter(file), format);
//			writer.write(document);
//			writer.close();
//			return classes;
//		} catch (Exception e) {
//			throw new Exception("registContainerEntity error.className:" + name, e);
//		}
//	}
//
//	/** 扫描受管实体,生成 x_perisitence.xml */
//	@SuppressWarnings("unchecked")
//	private Set<Class<? extends JpaObject>> persist1enceXml(String webApplicationDirectory, List<String> entities)
//			throws Exception {
//		String name = "";
//		Set<Class<? extends JpaObject>> classes = new HashSet<>();
//		try {
//			List<String> names = new ArrayList<>();
//			names.addAll((List<String>) Config.resource(Config.RESOUCE_CONTAINERENTITYNAMES));
//			names = ListTools.includesExcludesWildcard(names, entities, null);
//			Document document = DocumentHelper.createDocument();
//			Element persistence = document.addElement("persistence", "http://java.sun.com/xml/ns/persistence");
//			persistence.addAttribute(QName.get("schemaLocation", "xsi", "http://www.w3.org/2001/XMLSchema-instance"),
//					"http://java.sun.com/xml/ns/persistence http://java.sun.com/xml/ns/persistence/persistence_2_0.xsd");
//			persistence.addAttribute("version", "2.0");
//			for (String className : names) {
//				name = className;
//				Class<? extends JpaObject> clazz = (Class<JpaObject>) Class.forName(className);
//				Element unit = persistence.addElement("persistence-unit");
//				unit.addAttribute("name", className);
//				unit.addAttribute("transaction-type", "RESOURCE_LOCAL");
//				Element provider = unit.addElement("provider");
//				provider.addText(PersistenceProviderImpl.class.getName());
//				for (Class<?> o : JpaObjectTools.scanMappedSuperclass(clazz)) {
//					Element mapped_element = unit.addElement("class");
//					mapped_element.addText(o.getName());
//				}
//				classes.add(clazz);
//			}
//			OutputFormat format = OutputFormat.createPrettyPrint();
//			format.setEncoding("UTF-8");
//			File dir = new File(webApplicationDirectory + "/WEB-INF/classes/" + META_INF);
//			FileUtils.forceMkdir(dir);
//			File file = new File(webApplicationDirectory + "/WEB-INF/classes/" + PERSISTENCE_XML_PATH);
//			XMLWriter writer = new XMLWriter(new FileWriter(file), format);
//			writer.write(document);
//			writer.close();
//			return classes;
//		} catch (Exception e) {
//			throw new Exception("registContainerEntity error.className:" + name, e);
//		}
//	}

	@SuppressWarnings("unchecked")
	public <T> Class<T> assignableFrom(Class<T> cls) throws Exception {
		for (Class<?> clazz : this.entityManagerFactoryMap.keySet()) {
			if (clazz.isAssignableFrom(cls)) {
				return (Class<T>) clazz;
			}
		}
		throw new Exception("can not find jpa assignable class for " + cls + ".");
	}

	private <T extends JpaObject> Map<Field, CheckPersist> loadCheckPersistField(Class<T> cls) throws Exception {
		Map<Field, CheckPersist> map = new HashMap<Field, CheckPersist>();
		for (Field fld : cls.getDeclaredFields()) {
			CheckPersist checkPersist = fld.getAnnotation(CheckPersist.class);
			if (null != checkPersist) {
				map.put(fld, checkPersist);
			}
		}
		return map;
	}

	private <T extends JpaObject> Map<Field, CheckRemove> loadCheckRemoveField(Class<T> cls) throws Exception {
		Map<Field, CheckRemove> map = new HashMap<Field, CheckRemove>();
		for (Field fld : cls.getDeclaredFields()) {
			CheckRemove checkRemove = fld.getAnnotation(CheckRemove.class);
			if (null != checkRemove) {
				map.put(fld, checkRemove);
			}
		}
		return map;
	}

	private Set<Class<? extends JpaObject>> listUitClass(String source) throws Exception {
		try {
			Set<Class<? extends JpaObject>> classes = new HashSet<>();
			URL url;
			if (StringUtils.isEmpty(source)) {
				url = this.getClass().getClassLoader().getResource(PersistenceProductDerivation.RSRC_DEFAULT);
			} else {
				url = this.getClass().getClassLoader().getResource(source);
			}
			if (null == url) {
				throw new Exception("can not load resource: " + source + ".");
			}
			File file = new File(url.toURI());
			SAXReader reader = new SAXReader();
			Document document = reader.read(file);
			for (Object o : document.getRootElement().elements("persistence-unit")) {
				Element unit = (Element) o;
				classes.add((Class<JpaObject>) Class.forName(unit.attribute("name").getValue()));
			}
			return classes;
		} catch (Exception e) {
			throw new Exception("list unit error:" + source, e);
		}
	}

//	private Properties properties(String className) throws Exception {
//		if (Config.externalDataSources().enable()) {
//			return properties_external(className);
//		} else {
//			return properties_internal(className);
//		}
//	}
//
//	private Properties properties_external(String className) throws Exception {
//		Properties properties = new Properties();
//		properties.put("openjpa.jdbc.DBDictionary", Config.externalDataSources().dictionary());
//		properties.put("openjpa.BrokerFactory", "slice");
//		/* 如果是DB2 添加 Schema,mysql 不需要Schema 如果用了Schema H2数据库就会报错说没有Schema */
//		if (Config.externalDataSources().hasSchema()) {
//			properties.put("openjpa.jdbc.Schema", JpaObject.default_schema);
//		}
//		properties.put("openjpa.slice.Lenient", "false");
//		properties.put("openjpa.slice.DistributionPolicy", FactorDistributionPolicy.class.getName());
//		properties.put("openjpa.slice.Names",
//				StringUtils.join(Config.externalDataSources().findNamesOfContainerEntity(className), ","));
//		// properties.put("openjpa.ConnectionDriverName",
//		// DruidDataSource.class.getName());
//		properties.put("openjpa.QueryCompilationCache", "false");
//		properties.put("openjpa.IgnoreChanges", "true");
//		properties.put("openjpa.QueryCache", "false");
//		properties.put("openjpa.QueryCompilationCache", "false");
//		properties.put("openjpa.LockManager", "none");
//		properties.put("openjpa.jdbc.ResultSetType", "scroll-insensitive");
//		/* 如果启用本地初始化会导致classLoad的问题 */
//		properties.put("openjpa.DynamicEnhancementAgent", "false");
//		properties.put("openjpa.jdbc.SynchronizeMappings", "buildSchema(ForeignKeys=false)");
//		/* 锁 */
//		properties.put("openjpa.Log", "DefaultLevel=WARN");
//		// properties.put("openjpa.ConnectionFactoryProperties", "PrettyPrint=true,
//		// PrettyPrintLineLength=72");
//		for (String name : Config.externalDataSources().findNamesOfContainerEntity(className)) {
//			properties.put("openjpa.slice." + name + ".ConnectionFactoryName", Config.RESOUCE_JDBC_PREFIX + name);
//		}
//		return properties;
//	}
//
//	private Properties properties_internal(String className) throws Exception {
//		Properties properties = new Properties();
//		properties.put("openjpa.jdbc.DBDictionary", SlicePropertiesBuilder.dictionary_h2);
//		properties.put("openjpa.BrokerFactory", "slice");
//		properties.put("openjpa.slice.Lenient", "false");
//		properties.put("openjpa.slice.DistributionPolicy", FactorDistributionPolicy.class.getName());
//		properties.put("openjpa.slice.Names",
//				StringUtils.join(Config.nodes().dataServers().findNamesOfContainerEntity(className), ","));
//		properties.put("openjpa.QueryCompilationCache", "false");
//		properties.put("openjpa.IgnoreChanges", "true");
//		properties.put("openjpa.QueryCache", "false");
//		properties.put("openjpa.QueryCompilationCache", "false");
//		properties.put("openjpa.LockManager", "none");
//		properties.put("openjpa.jdbc.ResultSetType", "scroll-insensitive");
//		/* 如果启用本地初始化会导致classLoad的问题 */
//		properties.put("openjpa.DynamicEnhancementAgent", "false");
//		properties.put("openjpa.jdbc.SynchronizeMappings", "buildSchema(ForeignKeys=false)");
//		// properties.put("openjpa.ConnectionDriverName",
//		// "com.alibaba.druid.pool.DruidDataSource");
//		/* 锁 */
//		properties.put("openjpa.Log", "DefaultLevel=WARN");
//		for (String name : Config.nodes().dataServers().findNamesOfContainerEntity(className)) {
//			properties.put("openjpa.slice." + name + ".ConnectionFactoryName", Config.RESOUCE_JDBC_PREFIX + name);
//			// properties.put("openjpa.slice." + name + ".ConnectionDriverName",
//			// "com.alibaba.druid.pool.DruidDataSource");
//		}
//		return properties;
//	}

}