package com.x.base.core.project.config;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.Test;

import com.x.base.core.project.Packages;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.tools.DefaultCharset;

import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;
import io.github.lukehutch.fastclasspathscanner.scanner.ScanResult;

public class CreateSample {

	@Test
	public void test() throws Exception {
		FastClasspathScanner scanner = new FastClasspathScanner(Packages.PREFIX);
		ScanResult scanResult = scanner.scan();
		List<Class<?>> classes = new ArrayList<Class<?>>();
		for (String str : scanResult.getNamesOfSubclassesOf(ConfigObject.class)) {
			Class<? extends ConfigObject> cls = (Class<? extends ConfigObject>) Class.forName(str);
			if (!cls.isMemberClass()) {
				classes.add(cls);
			}
		}
		Collections.sort(classes, new Comparator<Class<?>>() {
			public int compare(Class<?> c1, Class<?> c2) {
				return c1.getCanonicalName().compareTo(c2.getCanonicalName());
			}
		});
		for (Class<?> cls : classes) {
			Object o = MethodUtils.invokeStaticMethod(cls, "defaultInstance", null);
			Map<String, Object> map = new HashMap<String, Object>();
			map = XGsonBuilder.convert(o, map.getClass());
			map = this.describe(cls, map);
			String name = StringUtils.lowerCase(cls.getSimpleName().substring(0, 1)) + cls.getSimpleName().substring(1)
					+ ".json";
			File file = new File("D:/o2server/config/sample", name);
			FileUtils.write(file, XGsonBuilder.toJson(map), DefaultCharset.charset);
		}

	}

	private <T extends ConfigObject> Map<String, Object> describe(Class<?> cls, Map<String, Object> map)
			throws Exception {
		for (Field field : FieldUtils.getFieldsListWithAnnotation(cls, FieldDescribe.class)) {
			map.put("###" + field.getName(), field.getAnnotation(FieldDescribe.class).value() + "###");
			if (ConfigObject.class.isAssignableFrom(field.getType())) {
				Map<String, Object> m = (Map<String, Object>) map.get(field.getName());
				map.put(field.getName(), this.describe(field.getType(), m));
			}
		}
		EntryComparator entryComparator = new EntryComparator();
		map = map.entrySet().stream().sorted(entryComparator)
				.collect(Collectors.toMap(Entry::getKey, Entry::getValue, (v1, v2) -> v1, LinkedHashMap::new));
		return map;
	}

	public static class EntryComparator implements Comparator<Entry<String, Object>> {
		public int compare(Entry<String, Object> en1, Entry<String, Object> en2) {
			String k1 = en1.getKey();
			String k2 = en2.getKey();
			if (k1.startsWith("###")) {
				k1 = StringUtils.substringAfter(k1, "###");
			}
			if (k2.startsWith("###")) {
				k2 = StringUtils.substringAfter(k2, "###");
			}
			if (StringUtils.equals(k1, k2)) {
				if (en1.getKey().startsWith("###")) {
					return -1;
				}
				if (en2.getKey().startsWith("###")) {
					return 1;
				}
			}
			return k1.compareTo(k2);
		}
	}

}
