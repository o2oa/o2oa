package com.x.base.core.container.factory;

import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Id;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.openjpa.persistence.OpenJPAPersistence;
import org.apache.openjpa.persistence.PersistenceProductDerivation;
import org.apache.openjpa.persistence.PersistenceProviderImpl;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.QName;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.CheckRemove;
import com.x.base.core.entity.annotation.Flag;
import com.x.base.core.entity.annotation.RestrictFlag;
import com.x.base.core.entity.tools.JpaObjectTools;
import com.x.base.core.project.config.DataMappings;
import com.x.base.core.project.tools.ListTools;

public abstract class SliceEntityManagerContainerFactory {

	// protected static String persistence_xml_path = "META-INF/x_persistence.xml";
	protected static String PERSISTENCE_XML_PATH = "META-INF/persistence.xml";
	protected static String META_INF = "META-INF";

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

//	protected SliceEntityManagerContainerFactory(String webApplicationDirectory, DataMappings dataMappings)
//			throws Exception {
////		Set<Class<? extends JpaObject>> classes = mergePersistenceXml(webApplicationDirectory, dataMappings);
//		Set<Class<? extends JpaObject>> classes = persistenceXml(webApplicationDirectory, dataMappings,);
//		for (Class<? extends JpaObject> clz : classes) {
//			checkPersistFieldMap.put(clz, this.loadCheckPersistField(clz));
//			checkRemoveFieldMap.put(clz, this.loadCheckRemoveField(clz));
//			entityManagerFactoryMap.put(clz,
//					OpenJPAPersistence.createEntityManagerFactory(clz.getCanonicalName(), PERSISTENCE_XML_PATH));
//			flagMap.put(clz, new ArrayList<Field>());
//			restrictFlagMap.put(clz, new ArrayList<Field>());
//			for (Field o : FieldUtils.getFieldsListWithAnnotation(clz, Id.class)) {
//				flagMap.get(clz).add(o);
//				restrictFlagMap.get(clz).add(o);
//			}
//			for (Field o : FieldUtils.getFieldsListWithAnnotation(clz, Flag.class)) {
//				flagMap.get(clz).add(o);
//				restrictFlagMap.get(clz).add(o);
//			}
//			for (Field o : FieldUtils.getFieldsListWithAnnotation(clz, RestrictFlag.class)) {
//				restrictFlagMap.get(clz).add(o);
//			}
//		}
//	}

	protected SliceEntityManagerContainerFactory(String webApplicationDirectory, DataMappings dataMappings,
			List<String> entities) throws Exception {
		Set<Class<? extends JpaObject>> classes = persistenceXml(webApplicationDirectory, dataMappings, entities);
		for (Class<? extends JpaObject> clz : classes) {
			checkPersistFieldMap.put(clz, this.loadCheckPersistField(clz));
			checkRemoveFieldMap.put(clz, this.loadCheckRemoveField(clz));
			entityManagerFactoryMap.put(clz,
					OpenJPAPersistence.createEntityManagerFactory(clz.getName(), PERSISTENCE_XML_PATH));
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

	protected SliceEntityManagerContainerFactory(String source) throws Exception {
		Set<Class<? extends JpaObject>> classes = this.listUitClass(source);
		for (Class<? extends JpaObject> clz : classes) {
			checkPersistFieldMap.put(clz, this.loadCheckPersistField(clz));
			checkRemoveFieldMap.put(clz, this.loadCheckRemoveField(clz));
			entityManagerFactoryMap.put(clz, OpenJPAPersistence.createEntityManagerFactory(clz.getName(), source));
		}
	}

	/** 扫描受管实体,生成 x_perisitence.xml */
	@SuppressWarnings("unchecked")
	private Set<Class<? extends JpaObject>> persistenceXml(String webApplicationDirectory, DataMappings dataMappings,
			List<String> entities) throws Exception {
		String name = "";
		Set<Class<? extends JpaObject>> classes = new HashSet<>();
		try {
			List<String> names = new ArrayList<>();
			names.addAll(dataMappings.keySet());
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
				Element slice_unit_properties = unit.addElement("properties");
				for (Entry<String, String> entry : SlicePropertiesBuilder.getPropertiesDBCP(dataMappings.get(className))
						.entrySet()) {
					Element property = slice_unit_properties.addElement("property");
					property.addAttribute("name", entry.getKey());
					property.addAttribute("value", entry.getValue());
				}
				classes.add(clazz);
			}
			OutputFormat format = OutputFormat.createPrettyPrint();
			format.setEncoding("UTF-8");
			File dir = new File(webApplicationDirectory + "/WEB-INF/classes/" + META_INF);
			FileUtils.forceMkdir(dir);
			File file = new File(webApplicationDirectory + "/WEB-INF/classes/" + PERSISTENCE_XML_PATH);
			XMLWriter writer = new XMLWriter(new FileWriter(file), format);
			writer.write(document);
			writer.close();
			return classes;
		} catch (Exception e) {
			throw new Exception("registContainerEntity error.className:" + name, e);
		}
	}

//	/** 扫描受管实体,生成 x_perisitence.xml */
//	@SuppressWarnings("unchecked")
//	private Set<Class<? extends JpaObject>> mergePersistenceXml(String webApplicationDirectory,
//			DataMappings dataMappings) throws Exception {
//		String name = null;
//		try {
//			Set<Class<? extends JpaObject>> classes = new HashSet<>();
//			File file = new File(webApplicationDirectory + "/WEB-INF/classes/" + persistence_xml_path);
//			SAXReader reader = new SAXReader();
//			Document document = reader.read(file);
//			for (Object o : document.getRootElement().elements("persistence-unit")) {
//				Element unit = (Element) o;
//				name = unit.attribute("name").getValue();
//				// System.out.println("try to load entity class:" + name);
//				Element properties = unit.element("properties");
//				if (null != properties) {
//					properties.clearContent();
//				} else {
//					properties = unit.addElement("properties");
//				}
//				for (Entry<String, String> entry : SlicePropertiesBuilder.getPropertiesDBCP(dataMappings.get(name))
//						.entrySet()) {
//					Element property = properties.addElement("property");
//					property.addAttribute("name", entry.getKey());
//					property.addAttribute("value", entry.getValue());
//				}
//				classes.add((Class<JpaObject>) Class.forName(name));
//			}
//			OutputFormat format = OutputFormat.createPrettyPrint();
//			format.setEncoding("UTF-8");
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

}