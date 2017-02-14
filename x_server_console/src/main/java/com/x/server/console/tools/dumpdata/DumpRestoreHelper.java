package com.x.server.console.tools.dumpdata;

import java.io.File;
import java.io.FileWriter;
import java.util.HashSet;
import java.util.List;
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

import com.x.base.core.DefaultCharset;
import com.x.base.core.container.FactorDistributionPolicy;
import com.x.base.core.container.factory.SlicePropertiesBuilder;
import com.x.base.core.entity.AbstractPersistenceProperties;
import com.x.base.core.project.server.DataMapping;

public class DumpRestoreHelper {

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
		property.addAttribute("name", "openjpa.jdbc.DBDictionary");
		property.addAttribute("value", SlicePropertiesBuilder.determineDBDictionary(sources.get(0)));
		property = properties.addElement("property");
		property.addAttribute("name", "openjpa.slice.Lenient");
		property.addAttribute("value", "true");
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
			property.addAttribute("value", AbstractPersistenceProperties.schema);
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

}
