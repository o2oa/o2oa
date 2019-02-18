package com.x.server.console.action;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.MappedSuperclass;

import org.apache.commons.lang3.StringUtils;
import org.apache.openjpa.persistence.PersistenceProviderImpl;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.QName;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import com.x.base.core.container.FactorDistributionPolicy;
import com.x.base.core.container.factory.SlicePropertiesBuilder;
import com.x.base.core.entity.AbstractPersistenceProperties;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.Storage;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.project.config.DataMapping;
import com.x.base.core.project.config.DataMappings;
import com.x.base.core.project.tools.DefaultCharset;
import com.x.base.core.project.tools.ListTools;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;

public class PersistenceXmlHelper {

	public static void createPersistenceXml(Class<?> cls, List<DataMapping> sources, File file) throws Exception {
		Document document = DocumentHelper.createDocument();
		Element persistence = document.addElement("persistence", "http://java.sun.com/xml/ns/persistence");
		persistence.addAttribute(QName.get("schemaLocation", "xsi", "http://www.w3.org/2001/XMLSchema-instance"),
				"http://java.sun.com/xml/ns/persistence  http://java.sun.com/xml/ns/persistence/persistence_2_0.xsd");
		persistence.addAttribute("version", "2.0");
		Element unit = persistence.addElement("persistence-unit");
		unit.addAttribute("transaction-type", "RESOURCE_LOCAL");
		unit.addAttribute("name", cls.getName());
		Element provider = unit.addElement("provider");
		provider.addText(PersistenceProviderImpl.class.getName());
		Set<Class<?>> classes = new HashSet<>();
		classes.add(cls);
		classes.addAll(scanMappedSuperclass(cls));
		for (Class<?> o : classes) {
			Element element = unit.addElement("class");
			element.addText(o.getCanonicalName());
		}
		Element properties = unit.addElement("properties");
		Element property = properties.addElement("property");
		property.addAttribute("name", "openjpa.BrokerFactory");
		property.addAttribute("value", "slice");
		property = properties.addElement("property");
		property.addAttribute("name", "openjpa.Log");
		property.addAttribute("value", "DefaultLevel=WARN");
		property = properties.addElement("property");
		property.addAttribute("name", "openjpa.jdbc.DBDictionary");
		property.addAttribute("value", SlicePropertiesBuilder.determineDBDictionary(sources.get(0)));
		if (StringUtils.equals(SlicePropertiesBuilder.determineDBDictionary(sources.get(0)),
				SlicePropertiesBuilder.dictionary_db2)) {
			property = properties.addElement("property");
			property.addAttribute("name", "openjpa.jdbc.Schema");
			property.addAttribute("value", JpaObject.default_schema);
		}
		if (StringUtils.equals(SlicePropertiesBuilder.determineDBDictionary(sources.get(0)),
				SlicePropertiesBuilder.dictionary_informix)) {
			property = properties.addElement("property");
			property.addAttribute("name", "openjpa.jdbc.Schema");
			property.addAttribute("value", JpaObject.default_schema);
		}
		if (StringUtils.equals(SlicePropertiesBuilder.determineDBDictionary(sources.get(0)),
				SlicePropertiesBuilder.dictionary_dm)) {
			property = properties.addElement("property");
			property.addAttribute("name", "openjpa.jdbc.Schema");
			property.addAttribute("value", JpaObject.default_schema);
		}
		property = properties.addElement("property");
		property.addAttribute("name", "openjpa.slice.Lenient");
		property.addAttribute("value", "false");
		property = properties.addElement("property");
		property.addAttribute("name", "openjpa.slice.DistributionPolicy");
		property.addAttribute("value", FactorDistributionPolicy.class.getName());
		property = properties.addElement("property");
		property.addAttribute("name", "openjpa.slice.Names");
		property.addAttribute("value", SlicePropertiesBuilder.getSliceNames(sources));
		property = properties.addElement("property");
		property.addAttribute("name", "openjpa.ConnectionDriverName");
		property.addAttribute("value", SlicePropertiesBuilder.getConnectionDriverName(sources.get(0)));
		property = properties.addElement("property");
		property.addAttribute("name", "openjpa.QueryCompilationCache");
		property.addAttribute("value", "false");
		property = properties.addElement("property");
		property.addAttribute("name", "openjpa.IgnoreChanges");
		property.addAttribute("value", "true");
		property = properties.addElement("property");
		property.addAttribute("name", "openjpa.jdbc.ResultSetType");
		property.addAttribute("value", "scroll-insensitive");
		property = properties.addElement("property");
		property.addAttribute("name", "openjpa.jdbc.SynchronizeMappings");
		property.addAttribute("value", "buildSchema(ForeignKeys=false)");
		property = properties.addElement("property");
		property.addAttribute("name", "openjpa.LockManager");
		property.addAttribute("value", "none");
		property = properties.addElement("property");
		property.addAttribute("name", "openjpa.RuntimeUnenhancedClasses");
		property.addAttribute("value", "supported");
		property = properties.addElement("property");
		property.addAttribute("name", "openjpa.ConnectionUserName");
		property.addAttribute("value", sources.get(0).getUsername());
		property = properties.addElement("property");
		property.addAttribute("name", "openjpa.ConnectionPassword");
		property.addAttribute("value", sources.get(0).getPassword());
		String driver = SlicePropertiesBuilder.getConnectionDriverName(sources.get(0));
		if (StringUtils.equals(driver, SlicePropertiesBuilder.driver_db2)
				|| StringUtils.equals(driver, SlicePropertiesBuilder.driver_informix)) {
			property = properties.addElement("property");
			property.addAttribute("name", "openjpa.jdbc.Schema");
			property.addAttribute("value", JpaObject.default_schema);
		}
		for (int i = 0; i < sources.size(); i++) {
			DataMapping dataMapping = sources.get(i);
			String name = SlicePropertiesBuilder.getName(i);
			property = properties.addElement("property");
			property.addAttribute("name", "openjpa.slice." + name + ".ConnectionUserName");
			property.addAttribute("value", dataMapping.getUsername());
			property = properties.addElement("property");
			property.addAttribute("name", "openjpa.slice." + name + ".ConnectionPassword");
			property.addAttribute("value", dataMapping.getPassword());
			property = properties.addElement("property");
			property.addAttribute("name", "openjpa.slice." + name + ".ConnectionDriverName");
			property.addAttribute("value", SlicePropertiesBuilder.getConnectionDriverName(dataMapping));
			property = properties.addElement("property");
			property.addAttribute("name", "openjpa.slice." + name + ".ConnectionURL");
			property.addAttribute("value", dataMapping.getUrl());
			property = properties.addElement("property");
			property.addAttribute("name", "openjpa.slice." + name + ".Log");
			property.addAttribute("value", getLog(dataMapping));
		}
		OutputFormat format = OutputFormat.createPrettyPrint();
		format.setEncoding(DefaultCharset.name);
		XMLWriter writer = new XMLWriter(new FileWriter(file), format);
		writer.write(document);
		writer.close();
	}

	public static void createPersistenceXml(List<Class<?>> clsList, DataMappings mappings, File file) throws Exception {
		Document document = DocumentHelper.createDocument();
		Element persistence = document.addElement("persistence", "http://java.sun.com/xml/ns/persistence");
		persistence.addAttribute(QName.get("schemaLocation", "xsi", "http://www.w3.org/2001/XMLSchema-instance"),
				"http://java.sun.com/xml/ns/persistence  http://java.sun.com/xml/ns/persistence/persistence_2_0.xsd");
		persistence.addAttribute("version", "2.0");
		for (Class<?> cls : clsList) {
			List<DataMapping> sources = mappings.get(cls.getName());
			Element unit = persistence.addElement("persistence-unit");
			unit.addAttribute("transaction-type", "RESOURCE_LOCAL");
			unit.addAttribute("name", cls.getName());
			Element provider = unit.addElement("provider");
			provider.addText(PersistenceProviderImpl.class.getName());
			Set<Class<?>> classes = new HashSet<>();
			classes.add(cls);
			classes.addAll(scanMappedSuperclass(cls));
			for (Class<?> o : classes) {
				Element element = unit.addElement("class");
				element.addText(o.getCanonicalName());
			}
			Element properties = unit.addElement("properties");
			Element property = properties.addElement("property");
			property.addAttribute("name", "openjpa.BrokerFactory");
			property.addAttribute("value", "slice");
			property = properties.addElement("property");
			property.addAttribute("name", "openjpa.Log");
			property.addAttribute("value", "DefaultLevel=WARN");
			property = properties.addElement("property");
			property.addAttribute("name", "openjpa.jdbc.DBDictionary");
			property.addAttribute("value", SlicePropertiesBuilder.determineDBDictionary(sources.get(0)));
			property = properties.addElement("property");
			property.addAttribute("name", "openjpa.slice.Lenient");
			property.addAttribute("value", "false");
			property = properties.addElement("property");
			property.addAttribute("name", "openjpa.slice.DistributionPolicy");
			property.addAttribute("value", FactorDistributionPolicy.class.getName());
			property = properties.addElement("property");
			property.addAttribute("name", "openjpa.slice.Names");
			property.addAttribute("value", SlicePropertiesBuilder.getSliceNames(sources));
			property = properties.addElement("property");
			property.addAttribute("name", "openjpa.ConnectionDriverName");
			property.addAttribute("value", SlicePropertiesBuilder.getConnectionDriverName(sources.get(0)));
			property = properties.addElement("property");
			property.addAttribute("name", "openjpa.QueryCompilationCache");
			property.addAttribute("value", "false");
			property = properties.addElement("property");
			property.addAttribute("name", "openjpa.IgnoreChanges");
			property.addAttribute("value", "true");
			property = properties.addElement("property");
			property.addAttribute("name", "openjpa.jdbc.ResultSetType");
			property.addAttribute("value", "scroll-insensitive");
			property = properties.addElement("property");
			property.addAttribute("name", "openjpa.jdbc.SynchronizeMappings");
			property.addAttribute("value", "buildSchema(ForeignKeys=false)");
			property = properties.addElement("property");
			property.addAttribute("name", "openjpa.LockManager");
			property.addAttribute("value", "none");
			property = properties.addElement("property");
			property.addAttribute("name", "openjpa.RuntimeUnenhancedClasses");
			property.addAttribute("value", "supported");
			property = properties.addElement("property");
			property.addAttribute("name", "openjpa.ConnectionUserName");
			property.addAttribute("value", sources.get(0).getUsername());
			property = properties.addElement("property");
			property.addAttribute("name", "openjpa.ConnectionPassword");
			property.addAttribute("value", sources.get(0).getPassword());
			String driver = SlicePropertiesBuilder.getConnectionDriverName(sources.get(0));
			if (StringUtils.equals(driver, SlicePropertiesBuilder.driver_db2)
					|| StringUtils.equals(driver, SlicePropertiesBuilder.driver_informix)) {
				property = properties.addElement("property");
				property.addAttribute("name", "openjpa.jdbc.Schema");
				property.addAttribute("value", JpaObject.default_schema);
			}
			for (int i = 0; i < sources.size(); i++) {
				DataMapping dataMapping = sources.get(i);
				String name = SlicePropertiesBuilder.getName(i);
				property = properties.addElement("property");
				property.addAttribute("name", "openjpa.slice." + name + ".ConnectionUserName");
				property.addAttribute("value", dataMapping.getUsername());
				property = properties.addElement("property");
				property.addAttribute("name", "openjpa.slice." + name + ".ConnectionPassword");
				property.addAttribute("value", dataMapping.getPassword());
				property = properties.addElement("property");
				property.addAttribute("name", "openjpa.slice." + name + ".ConnectionDriverName");
				property.addAttribute("value", SlicePropertiesBuilder.getConnectionDriverName(dataMapping));
				property = properties.addElement("property");
				property.addAttribute("name", "openjpa.slice." + name + ".ConnectionURL");
				property.addAttribute("value", dataMapping.getUrl());
				property = properties.addElement("property");
				property.addAttribute("name", "openjpa.slice." + name + ".Log");
				property.addAttribute("value", getLog(dataMapping));
			}
		}
		OutputFormat format = OutputFormat.createPrettyPrint();
		format.setEncoding(DefaultCharset.name);
		XMLWriter writer = new XMLWriter(new FileWriter(file), format);
		writer.write(document);
		writer.close();
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

	/** 获取日志属性 */
	protected static String getLog(DataMapping dataMapping) throws Exception {
		try {
			return "Tool=" + dataMapping.getToolLevel() + ", Enhance=" + dataMapping.getEnhanceLevel() + ", METADATA="
					+ dataMapping.getMetaDataLevel() + ", RUNTIME=" + dataMapping.getRuntimeLevel() + ", Query="
					+ dataMapping.getQueryLevel() + ", DataCache=" + dataMapping.getDataCacheLevel() + ", JDBC="
					+ dataMapping.getJdbcLevel() + ", SQL=" + dataMapping.getSqlLevel();
		} catch (Exception e) {
			throw new Exception("can not get log property.", e);
		}
	}

	public static List<String> listDataClassName() throws Exception {
		try (ScanResult scanResult = new ClassGraph().enableAllInfo().scan()) {
			List<String> list = new ArrayList<>();
			for (ClassInfo info : scanResult.getClassesWithAnnotation(ContainerEntity.class.getName())) {
				list.add(info.getName());
			}
			return list;
		}
	}

	public static List<String> listStorageClassName() throws Exception {
		try (ScanResult scanResult = new ClassGraph().enableAllInfo().scan()) {
			List<String> list = new ArrayList<>();
			for (ClassInfo info : scanResult.getClassesWithAnnotation(Storage.class.getName())) {
				list.add(info.getName());
			}
			return list;
		}
	}

	public static List<Class<?>> listClassWithIncludesExcludes(List<String> list, List<String> includes,
			List<String> excludes) throws Exception {
		list = ListTools.includesExcludesWildcard(list, includes, excludes);
		list = ListTools.trim(list, true, true);
		list = list.stream().sorted().collect(Collectors.toList());
		List<Class<?>> os = new ArrayList<>();
		for (String str : list) {
			Class<?> clz = null;
			try {
				clz = Class.forName(str);
				os.add(clz);
			} catch (Exception e) {
				System.out.println("无法获取类:" + str + ".");
			}
		}
		return os;
	}

}