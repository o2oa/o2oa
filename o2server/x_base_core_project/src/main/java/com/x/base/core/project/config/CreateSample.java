package com.x.base.core.project.config;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.Test;

import com.google.gson.JsonElement;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DefaultCharset;
import com.x.base.core.project.tools.FileTools;

public class CreateSample {

	private static Logger logger = LoggerFactory.getLogger(CreateSample.class);

	@Test
	public void test() throws Exception {
		List<Class<?>> classes = new ArrayList<Class<?>>();
		classes.add(AppStyle.class);
		classes.add(CenterServer.class);
		classes.add(Collect.class);
		classes.add(Dingding.class);
		classes.add(DumpRestoreData.class);
		classes.add(DumpRestoreStorage.class);
		classes.add(LogLevel.class);
		classes.add(Meeting.class);
		classes.add(Messages.class);
		classes.add(Node.class);
		classes.add(Person.class);
		classes.add(ProcessPlatform.class);
		classes.add(Qiyeweixin.class);
		classes.add(Query.class);
		classes.add(Token.class);
		classes.add(Vfs.class);
		classes.add(WorkTime.class);
		classes.add(ZhengwuDingding.class);
		classes.add(ExternalDataSource.class);

		Collections.sort(classes, new Comparator<Class<?>>() {
			public int compare(Class<?> c1, Class<?> c2) {
				return c1.getCanonicalName().compareTo(c2.getCanonicalName());
			}
		});
		for (Class<?> cls : classes) {
			Object o = MethodUtils.invokeStaticMethod(cls, "defaultInstance", null);
			Map<String, Object> map = new LinkedHashMap<String, Object>();
			map = XGsonBuilder.convert(o, map.getClass());
			map = this.describe(cls, map);
			String name = StringUtils.lowerCase(cls.getSimpleName().substring(0, 1)) + cls.getSimpleName().substring(1)
					+ ".json";
			File file = new File(FileTools.parent(FileTools.parent(new File("./"))), "configSample/" + name);
			logger.print("create file:{}.", file.getAbsoluteFile());
			FileUtils.write(file, XGsonBuilder.toJson(map), DefaultCharset.charset);
		}
		this.convertExternalDataSource2ExternalDataSources();
		this.renameNode();

	}

	private void convertExternalDataSource2ExternalDataSources() throws Exception, IOException {
		File file_externalDataSource = new File(FileTools.parent(FileTools.parent(new File("./"))),
				"configSample/externalDataSource.json");
		File file_externalDataSources = new File(FileTools.parent(FileTools.parent(new File("./"))),
				"configSample/externalDataSources.json");
		JsonElement jsonElement = XGsonBuilder.instance().fromJson(FileUtils.readFileToString(file_externalDataSource),
				JsonElement.class);
		List<JsonElement> list = new ArrayList<>();
		list.add(jsonElement);
		FileUtils.writeStringToFile(file_externalDataSources, XGsonBuilder.toJson(list), DefaultCharset.charset);
		file_externalDataSource.delete();
	}

	private void renameNode() throws Exception, IOException {
		File file_node = new File(FileTools.parent(FileTools.parent(new File("./"))), "configSample/node.json");
		File file_node_local = new File(FileTools.parent(FileTools.parent(new File("./"))),
				"configSample/node_127.0.0.1.json");
		if (file_node_local.exists()) {
			file_node_local.delete();
		}
		FileUtils.moveFile(file_node, file_node_local);
	}

	private <T extends ConfigObject> Map<String, Object> describe(Class<?> cls, Map<String, Object> map)
			throws Exception {
		for (Field field : FieldUtils.getFieldsListWithAnnotation(cls, FieldDescribe.class)) {
			map.put("###" + field.getName(), field.getAnnotation(FieldDescribe.class).value() + "###");
			if (ConfigObject.class.isAssignableFrom(field.getType())) {
				Object o = MethodUtils.invokeStaticMethod(field.getType(), "defaultInstance", null);
				Map<String, Object> m = new LinkedHashMap<String, Object>();
				m = XGsonBuilder.convert(o, m.getClass());
				map.put(field.getName(), this.describe(field.getType(), m));
			} else if (List.class.isAssignableFrom(field.getType())) {
				ParameterizedType parameterized = (ParameterizedType) field.getGenericType();
				Class<Object> actualClass = (Class<Object>) parameterized.getActualTypeArguments()[0];
				if (ConfigObject.class.isAssignableFrom(actualClass)) {
					Object o = MethodUtils.invokeStaticMethod(actualClass, "defaultInstance", null);
					Map<String, Object> m = new LinkedHashMap<String, Object>();
					m = XGsonBuilder.convert(o, m.getClass());
					List<Object> list = new ArrayList<>();
					list.add(this.describe(actualClass, m));
					map.put(field.getName(), list);
				}
			}
		}
		// EntryComparator entryComparator = new EntryComparator();
		// map = map.entrySet().stream().sorted(entryComparator)
		// .collect(Collectors.toMap(Entry::getKey, Entry::getValue, (v1, v2) -> v1,
		// LinkedHashMap::new));
		return map;
	}

	public static class EntryComparator implements Comparator<Entry<String, Object>> {
		public int compare(Entry<String, Object> en1, Entry<String, Object> en2) {
			String k1 = en1.getKey();
			String k2 = en2.getKey();
			return 1;
			// if ((!k1.startsWith("###")) && (!k2.startsWith("###"))) {
			// return 1;
			// } else {
			// if (k1.startsWith("###")) {
			// k1 = StringUtils.substringAfter(k1, "###");
			// }
			// if (k2.startsWith("###")) {
			// k2 = StringUtils.substringAfter(k2, "###");
			// }
			// if (StringUtils.equals(k1, k2)) {
			// if (en1.getKey().startsWith("###")) {
			// return -1;
			// }
			// if (en2.getKey().startsWith("###")) {
			// return 1;
			// }
			// }
			// return k1.compareTo(k2);
			// }

		}
	}

}
