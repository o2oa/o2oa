package com.x.base.core.project.build;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.config.AndFx;
import com.x.base.core.project.config.AppStyle;
import com.x.base.core.project.config.Cache;
import com.x.base.core.project.config.Cms;
import com.x.base.core.project.config.Collect;
import com.x.base.core.project.config.Components;
import com.x.base.core.project.config.ConfigObject;
import com.x.base.core.project.config.Dingding;
import com.x.base.core.project.config.DumpRestoreData;
import com.x.base.core.project.config.Exmail;
import com.x.base.core.project.config.ExternalDataSource;
import com.x.base.core.project.config.ExternalDataSources;
import com.x.base.core.project.config.ExternalStorageSources;
import com.x.base.core.project.config.General;
import com.x.base.core.project.config.JpushConfig;
import com.x.base.core.project.config.Messages;
import com.x.base.core.project.config.Mpweixin;
import com.x.base.core.project.config.Node;
import com.x.base.core.project.config.Person;
import com.x.base.core.project.config.Portal;
import com.x.base.core.project.config.ProcessPlatform;
import com.x.base.core.project.config.Qiyeweixin;
import com.x.base.core.project.config.Query;
import com.x.base.core.project.config.TernaryManagement;
import com.x.base.core.project.config.Token;
import com.x.base.core.project.config.WeLink;
import com.x.base.core.project.config.WorkTime;
import com.x.base.core.project.config.ZhengwuDingding;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.tools.DefaultCharset;

/**
 *
 * @author ray
 *
 */
public class CreateConfigSample {

	private static final String DEFAULTINSTANCE = "defaultInstance";

	public static void main(String... args) throws Exception {
		File base = new File(args[0]);
		File dir = new File(base, "configSample");
		List<Class<?>> classes = new ArrayList<>();
		classes.add(AppStyle.class);
		classes.add(Cache.class);
		classes.add(Collect.class);
		classes.add(Components.class);
		classes.add(Dingding.class);
		classes.add(DumpRestoreData.class);
		classes.add(Exmail.class);
		classes.add(ExternalStorageSources.class);
		classes.add(JpushConfig.class);
		classes.add(Messages.class);
		classes.add(Mpweixin.class);
		classes.add(Node.class);
		classes.add(Person.class);
		classes.add(Portal.class);
		classes.add(ProcessPlatform.class);
		classes.add(Qiyeweixin.class);
		classes.add(AndFx.class);
		classes.add(Query.class);
		classes.add(Token.class);
		classes.add(TernaryManagement.class);
		classes.add(WeLink.class);
		classes.add(WorkTime.class);
		classes.add(ZhengwuDingding.class);
		classes.add(General.class);
		classes.add(Cms.class);

		Collections.sort(classes, (c1, c2) -> c1.getCanonicalName().compareTo(c2.getCanonicalName()));
		for (Class<?> cls : classes) {
			create(dir.toPath(), cls);
		}
		renameNode(dir);
		createExternalDataSources(dir.toPath());
	}

	/**
	 * @author ray
	 * @param dir 创建到的目录
	 * @param cls 配置文件类
	 * @throws NoSuchMethodException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws Exception
	 * @throws IOException
	 */
	public static void create(Path dir, Class<?> cls) throws Exception {
		Object o = MethodUtils.invokeStaticMethod(cls, DEFAULTINSTANCE);
		Map<String, Object> map = new LinkedHashMap<>();
		map = XGsonBuilder.convert(o, map.getClass());
		describe(cls, map);
		String name = StringUtils.lowerCase(cls.getSimpleName().substring(0, 1)) + cls.getSimpleName().substring(1)
				+ ".json";
		File file = new File(dir.toFile(), name);
		FileUtils.write(file, XGsonBuilder.toJson(map), DefaultCharset.charset);
	}

	private static void createExternalDataSources(Path dir) throws Exception {
		Object o = MethodUtils.invokeStaticMethod(ExternalDataSource.class, DEFAULTINSTANCE);
		Map<String, Object> map = new LinkedHashMap<>();
		map = XGsonBuilder.convert(o, map.getClass());
		describe(ExternalDataSource.class, map);
		List<Map<String, Object>> externalDataSources = new ArrayList<>();
		externalDataSources.add(map);
		String name = StringUtils.lowerCase(ExternalDataSources.class.getSimpleName().substring(0, 1))
				+ ExternalDataSources.class.getSimpleName().substring(1) + ".json";
		File file = new File(dir.toFile(), name);
		FileUtils.write(file, XGsonBuilder.toJson(externalDataSources), DefaultCharset.charset);
	}

	private static void renameNode(File dir) throws IOException {
		File fileNode = new File(dir, "node.json");
		File fileNodeLocal = new File(dir, "node_127.0.0.1.json");
		Files.deleteIfExists(fileNodeLocal.toPath());
		FileUtils.moveFile(fileNode, fileNodeLocal);
	}

	private static Map<String, Object> describe(Class<?> cls, Map<String, Object> map) throws Exception {
		for (Field field : FieldUtils.getFieldsListWithAnnotation(cls, FieldDescribe.class)) {
			map.put("###" + field.getName(), field.getAnnotation(FieldDescribe.class).value() + "###");
			if (ConfigObject.class.isAssignableFrom(field.getType())) {
				Object o = MethodUtils.invokeStaticMethod(field.getType(), DEFAULTINSTANCE);
				Map<String, Object> m = new LinkedHashMap<>();
				m = XGsonBuilder.convert(o, m.getClass());
				map.put(field.getName(), describe(field.getType(), m));
			} else if (List.class.isAssignableFrom(field.getType())) {
				ParameterizedType parameterized = (ParameterizedType) field.getGenericType();
				Class<Object> actualClass = (Class<Object>) parameterized.getActualTypeArguments()[0];
				if (ConfigObject.class.isAssignableFrom(actualClass)) {
					Object o = MethodUtils.invokeStaticMethod(actualClass, DEFAULTINSTANCE);
					Map<String, Object> m = new LinkedHashMap<>();
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
