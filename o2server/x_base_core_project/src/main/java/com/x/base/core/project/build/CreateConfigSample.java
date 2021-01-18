package com.x.base.core.project.build;

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

import com.x.base.core.project.config.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DefaultCharset;

public class CreateConfigSample {

	private static Logger logger = LoggerFactory.getLogger(CreateConfigSample.class);

	public static void main(String... args) throws Exception {
		File base = new File(args[0]);
		File dir = new File(base, "configSample");
		List<Class<?>> classes = new ArrayList<Class<?>>();
		classes.add(AppStyle.class);
		classes.add(CenterServer.class);
		classes.add(Collect.class);
		classes.add(Communicate.class);
		classes.add(Components.class);
		classes.add(Dingding.class);
		classes.add(DumpRestoreData.class);
		// classes.add(DumpRestoreStorage.class);
		classes.add(Exmail.class);
		classes.add(LogLevel.class);
		classes.add(Meeting.class);
		classes.add(Messages.class);
		classes.add(Node.class);
		classes.add(Person.class);
		classes.add(Portal.class);
		classes.add(ProcessPlatform.class);
		classes.add(JpushConfig.class);
		classes.add(Qiyeweixin.class);
		classes.add(Query.class);
		classes.add(Token.class);
		classes.add(Vfs.class);
		classes.add(WeLink.class);
		classes.add(WorkTime.class);
		classes.add(ZhengwuDingding.class);
		classes.add(Cache.class);
		//classes.add(Web.class);

		Collections.sort(classes, new Comparator<Class<?>>() {
			public int compare(Class<?> c1, Class<?> c2) {
				return c1.getCanonicalName().compareTo(c2.getCanonicalName());
			}
		});
		for (Class<?> cls : classes) {
			Object o = MethodUtils.invokeStaticMethod(cls, "defaultInstance", null);
			Map<String, Object> map = new LinkedHashMap<String, Object>();
			map = XGsonBuilder.convert(o, map.getClass());
			map = describe(cls, map);
			String name = StringUtils.lowerCase(cls.getSimpleName().substring(0, 1)) + cls.getSimpleName().substring(1)
					+ ".json";
			File file = new File(dir, name);
			logger.print("create file:{}.", file.getAbsoluteFile());
			FileUtils.write(file, XGsonBuilder.toJson(map), DefaultCharset.charset);
		}
		renameNode(dir);
	}

	private static void renameNode(File dir) throws Exception, IOException {
		File file_node = new File(dir, "node.json");
		File file_node_local = new File(dir, "node_127.0.0.1.json");
		if (file_node_local.exists()) {
			file_node_local.delete();
		}
		FileUtils.moveFile(file_node, file_node_local);
	}

	private static <T extends ConfigObject> Map<String, Object> describe(Class<?> cls, Map<String, Object> map)
			throws Exception {
		for (Field field : FieldUtils.getFieldsListWithAnnotation(cls, FieldDescribe.class)) {
			map.put("###" + field.getName(), field.getAnnotation(FieldDescribe.class).value() + "###");
			if (ConfigObject.class.isAssignableFrom(field.getType())) {
				Object o = MethodUtils.invokeStaticMethod(field.getType(), "defaultInstance", null);
				Map<String, Object> m = new LinkedHashMap<String, Object>();
				m = XGsonBuilder.convert(o, m.getClass());
				map.put(field.getName(), describe(field.getType(), m));
			} else if (List.class.isAssignableFrom(field.getType())) {
				ParameterizedType parameterized = (ParameterizedType) field.getGenericType();
				Class<Object> actualClass = (Class<Object>) parameterized.getActualTypeArguments()[0];
				if (ConfigObject.class.isAssignableFrom(actualClass)) {
					Object o = MethodUtils.invokeStaticMethod(actualClass, "defaultInstance", null);
					Map<String, Object> m = new LinkedHashMap<String, Object>();
					m = XGsonBuilder.convert(o, m.getClass());
					List<Object> list = new ArrayList<>();
					list.add(describe(actualClass, m));
					map.put(field.getName(), list);
				}
			}
		}
		return map;
	}
}
