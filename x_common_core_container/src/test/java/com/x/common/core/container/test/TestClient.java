package com.x.common.core.container.test;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.openjpa.jdbc.sql.MySQLDictionary;
import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.junit.Test;

public class TestClient {

	public byte[] bs;
	public String s;
	public ArrayList<String> ss;
	public Integer i;
	public List<Integer> is;
	public Date d;
	public List<Date> ds;

	@Test
	public void createEntityManagerFactory() throws Exception {
		File file = new File("e:/persistence.xml");
		SAXReader reader = new SAXReader();
		Document document = reader.read(file);
		List list = document.getRootElement().elements("persistence-unit");
		if (list.isEmpty()) {
			throw new Exception("can not get properties element with persistence-unit name:");
		}
		Element element = (Element) list.get(0);
		element = element.element("properties");
		System.out.println(element.getNamespace());
		System.out.println(element.getUniquePath());
		DocumentFactory documentFactory = new DocumentFactory();
		Element ep = documentFactory.createElement("porperty", "http://java.sun.com/xml/ns/persistence");
		element.add(ep);
		System.out.println(element + "JJJJJJJJJJJJJJJ");
	}

	@Test
	public void test11() {
		System.out.println(Collection.class.isAssignableFrom(List.class));
		System.out.println(List.class.isAssignableFrom(Collection.class));
	}

	@Test
	public void test12() {
		MySQLDictionary m = new MySQLDictionary();
		System.out.println(m.clobTypeName);
		System.out.println(m.blobTypeName);
	}

	@Test
	public void test13() {
		try {
			URI uri = new URI("jdbc:mysql://d02zoneland.mysql.rds.aliyuncs.com:3306/d02s01?xxx=1");
			System.out.println(uri.toString());
			System.out.println(uri.getQuery());
			URL url = new URL(uri.getQuery());
			System.out.println(url);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void test4() {
		try {
			Field field = this.getClass().getField("b");
			System.out.println(field.getType().equals((new byte[] {}).getClass()));
			Class<?> type = field.getType();
			System.out.println(type.isArray());
			System.out.println(field.getGenericType());
			if (type.isArray()) {
				ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
				type = (Class<?>) parameterizedType.getActualTypeArguments()[0];
				System.out.println(type);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void test5() {
		try {
			Field field = this.getClass().getField("ss");
			System.out.println(field.getType().equals((new ArrayList<String>()).getClass()));
			System.out.println(field.getType().isAssignableFrom((new ArrayList<String>()).getClass()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
