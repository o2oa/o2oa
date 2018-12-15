package com.x.base.core.entity.tools;


//public class SqlWriter {
//	public static void main(String[] arguments) throws Exception {

//		try {
//			Document document = DocumentHelper.createDocument();
//			Element persistence = document.addElement("persistence", "http://java.sun.com/xml/ns/persistence");
//
//			persistence.addAttribute(QName.get("schemaLocation", "xsi", "http://www.w3.org/2001/XMLSchema-instance"),
//					"http://java.sun.com/xml/ns/persistence http://java.sun.com/xml/ns/persistence/persistence_2_0.xsd");
//
//			persistence.addAttribute("version", "2.0");
//			Element persistence_unit_x = persistence.addElement("persistence-unit");
//			persistence_unit_x.addAttribute("name", "x");
//			persistence_unit_x.addAttribute("transaction-type", "RESOURCE_LOCAL");
//			Element jta_data_source = persistence_unit_x.addElement("non-jta-data-source");
//			jta_data_source.addText("jdbc/x");
//
//			Map<String, Set<String>> unitMap = new TreeMap<String, Set<String>>();
//
//			Reflections reflections = new Reflections("com.x.core.entity");
//			Set<Class<?>> classSet = reflections.getTypesAnnotatedWith(PersistenceXml.class);
//			for (Class<?> clazz : classSet) {
//				PersistenceXml persistenceXml = (PersistenceXml) clazz.getAnnotation(PersistenceXml.class);
//				Set<String> set = unitMap.get(persistenceXml.unit());
//				if (null == set) {
//					set = new TreeSet<String>();
//					unitMap.put(persistenceXml.unit(), set);
//				}
//				set.add(clazz.getName());
//				boolean loop = false;
//				Class<?> superClass = clazz.getSuperclass();
//				do {
//					loop = false;
//					for (Class<?> cl : classSet) {
//						if (cl.getName().equalsIgnoreCase(superClass.getName())) {
//							loop = true;
//							set.add(superClass.getName());
//							superClass = superClass.getSuperclass();
//							break;
//						}
//					}
//				} while (loop);
//			}
//
//			Set<String> union = new TreeSet<String>();
//			for (String s : unitMap.keySet()) {
//				Set<String> set = unitMap.get(s);
//				for (String str : set) {
//					union.add(str);
//				}
//			}
//			for (String s : union) {
//				Element el = persistence_unit_x.addElement("class");
//				el.addText(s);
//			}
//
//			Element persistence_unit_x_properties = persistence_unit_x.addElement("properties");
//			Element unit_x_property_1 = persistence_unit_x_properties.addElement("property");
//			unit_x_property_1.addAttribute("name", "openjpa.jdbc.DBDictionary");
//			unit_x_property_1.addAttribute("value", "db2");
//			Element unit_x_property_2 = persistence_unit_x_properties.addElement("property");
//			unit_x_property_2.addAttribute("name", "openjpa.jdbc.SynchronizeMappings");
//			unit_x_property_2.addAttribute("value", "buildSchema(ForeignKeys=false)");
//			Element unit_x_property_3 = persistence_unit_x_properties.addElement("property");
//			unit_x_property_3.addAttribute("name", "openjpa.Log");
//			unit_x_property_3.addAttribute("value", "DefaultLevel=TRACE");
//			Element unit_x_property_4 = persistence_unit_x_properties.addElement("property");
//			unit_x_property_4.addAttribute("name", "openjpa.jdbc.Schema");
//			unit_x_property_4.addAttribute("value", "x");
//			Element unit_x_property_5 = persistence_unit_x_properties.addElement("property");
//			unit_x_property_5.addAttribute("name", "openjpa.ConnectionDriverName");
//			unit_x_property_5.addAttribute("value", "com.ibm.db2.jcc.DB2Driver");
//			Element unit_x_property_6 = persistence_unit_x_properties.addElement("property");
//			unit_x_property_6.addAttribute("name", "openjpa.ConnectionURL");
//			unit_x_property_6.addAttribute("value", arguments[0]);
//			Element unit_x_property_7 = persistence_unit_x_properties.addElement("property");
//			unit_x_property_7.addAttribute("name", "openjpa.ConnectionUserName");
//			unit_x_property_7.addAttribute("value", arguments[1]);
//			Element unit_x_property_8 = persistence_unit_x_properties.addElement("property");
//			unit_x_property_8.addAttribute("name", "openjpa.ConnectionPassword");
//			unit_x_property_8.addAttribute("value", arguments[2]);
//
//			OutputFormat format = OutputFormat.createPrettyPrint();
//			format.setEncoding("UTF-8");
//			XMLWriter writer = new XMLWriter(new FileWriter("src/test/resources/META-INF/persistence.xml"), format);
//			writer.write(document);
//			writer.close();
//			System.out.println("create new persistence.xml");
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		String[] args = new String[] {};
//		Options opts = new Options();
//		opts.put("schemaAction", arguments[3]);
//		opts.put("sqlFile", "sql/" + arguments[3] + ".sql");
//		JDBCConfiguration conf = new JDBCConfigurationImpl();
//		try {
//			MappingTool.run(conf, args, opts);
//		} finally {
//			conf.close();
//		}
//	}
//}
