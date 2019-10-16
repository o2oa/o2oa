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

import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.Test;

import com.google.gson.JsonElement;
import com.x.base.core.entity.StorageProtocol;
import com.x.base.core.entity.StorageType;
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
		classes.add(ExternalStorageSource.class);

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
		this.convertExternalStorageSource2ExternalStorageSources();
		this.renameNode();

	}

	private void convertExternalDataSource2ExternalDataSources() throws Exception, IOException {
		File file_externalDataSource = new File(FileTools.parent(FileTools.parent(new File("./"))),
				"configSample/externalDataSource.json");
		File file_externalDataSources = new File(FileTools.parent(FileTools.parent(new File("./"))),
				"configSample/externalDataSources.json");
		JsonElement jsonElement = XGsonBuilder.instance()
				.fromJson(FileUtils.readFileToString(file_externalDataSource, "utf-8"), JsonElement.class);
		List<JsonElement> list = new ArrayList<>();
		list.add(jsonElement);
		FileUtils.writeStringToFile(file_externalDataSources, XGsonBuilder.toJson(list), DefaultCharset.charset);
		file_externalDataSource.delete();
	}

	private void convertExternalStorageSource2ExternalStorageSources() throws Exception, IOException {
		File file_externalStorageSource = new File(FileTools.parent(FileTools.parent(new File("./"))),
				"configSample/externalStorageSource.json");
		File file_externalStorageSources = new File(FileTools.parent(FileTools.parent(new File("./"))),
				"configSample/externalStorageSources.json");
		JsonElement jsonElement = XGsonBuilder.instance()
				.fromJson(FileUtils.readFileToString(file_externalStorageSource, "utf-8"), JsonElement.class);
		List<JsonElement> list = new ArrayList<>();
		list.add(jsonElement);
		LinkedHashMap<String, List<JsonElement>> map = new LinkedHashMap<>();
		for (StorageType o : StorageType.values()) {
			map.put(o.toString(), list);
		}
		FileUtils.writeStringToFile(file_externalStorageSources, XGsonBuilder.toJson(map), DefaultCharset.charset);
		file_externalStorageSource.delete();
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
		return map;
	}

}
