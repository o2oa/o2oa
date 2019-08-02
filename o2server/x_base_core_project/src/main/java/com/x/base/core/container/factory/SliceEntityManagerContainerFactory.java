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

	protected SliceEntityManagerContainerFactory(String webApplicationDirectory, List<String> entities,
			boolean sliceFeatureEnable) throws Exception {
		File path = new File(webApplicationDirectory + "/WEB-INF/classes/" + PERSISTENCE_XML_PATH);
		List<String> classNames = PersistenceXmlHelper.write(path.getAbsolutePath(), entities);
		for (String className : classNames) {
			Class<? extends JpaObject> clz = (Class<? extends JpaObject>) Class.forName(className);
			checkPersistFieldMap.put(clz, this.loadCheckPersistField(clz));
			checkRemoveFieldMap.put(clz, this.loadCheckRemoveField(clz));
			Properties properties = PersistenceXmlHelper.properties(clz.getName(), sliceFeatureEnable);
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

	protected SliceEntityManagerContainerFactory(String source) throws Exception {
		Set<Class<? extends JpaObject>> classes = this.listUnitClass(source);
		for (Class<? extends JpaObject> clz : classes) {
			checkPersistFieldMap.put(clz, this.loadCheckPersistField(clz));
			checkRemoveFieldMap.put(clz, this.loadCheckRemoveField(clz));
			entityManagerFactoryMap.put(clz, OpenJPAPersistence.createEntityManagerFactory(clz.getName(), source));
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

	private Set<Class<? extends JpaObject>> listUnitClass(String source) throws Exception {
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