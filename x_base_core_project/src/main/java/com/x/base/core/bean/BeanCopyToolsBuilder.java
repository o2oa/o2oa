package com.x.base.core.bean;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

public class BeanCopyToolsBuilder {

	private static String GET_PREFIX = "get";
	private static String SET_PREFIX = "set";
	private static String IS_PREFIX = "is";

	public static <T, W> BeanCopyTools<T, W> create(Class<T> origClass, Class<W> destClass) {
		List<String> includeList = new ArrayList<String>();
		List<String> excludeList = new ArrayList<String>();
		return create(origClass, destClass, includeList, excludeList, true);
	}

	public static <T, W> BeanCopyTools<T, W> create(Class<T> origClass, Class<W> destClass, String... excludes) {
		return create(origClass, destClass, null, excludes, true);
	}

	public static <T, W> BeanCopyTools<T, W> create(Class<T> origClass, Class<W> destClass, List<String> excludes) {
		return create(origClass, destClass, null, excludes, true);
	}

	public static <T, W> BeanCopyTools<T, W> create(Class<T> origClass, Class<W> destClass, String[] includes,
			String[] excludes) {
		List<String> includeList = ((includes == null) ? new ArrayList<String>() : Arrays.asList(includes));
		List<String> excludeList = ((excludes == null) ? new ArrayList<String>() : Arrays.asList(excludes));
		return create(origClass, destClass, includeList, excludeList, true);
	}

	public static <T, W> BeanCopyTools<T, W> create(Class<T> origClass, Class<W> destClass, String[] includes,
			String[] excludes, boolean ignoreNull) {
		List<String> includeList = ((includes == null) ? new ArrayList<String>() : Arrays.asList(includes));
		List<String> excludeList = ((excludes == null) ? new ArrayList<String>() : Arrays.asList(excludes));
		return create(origClass, destClass, includeList, excludeList, ignoreNull);
	}

	public static <T, W> BeanCopyTools<T, W> create(Class<T> origClass, Class<W> destClass, List<String> includes,
			List<String> excludes) {
		return create(origClass, destClass, includes, excludes, true);
	}

	public static <T, W> BeanCopyTools<T, W> create(Class<T> origClass, Class<W> destClass, List<String> includes,
			List<String> excludes, boolean ingoreNull) {
		try {
			List<String> origFiledNames = getOrigFieldNames(origClass, includes);
			List<String> destFiledNames = getDestFieldNames(destClass, excludes);
			List<String> commons = ListUtils.intersection(origFiledNames, destFiledNames);
			List<String> names = new ArrayList<>();
			for (String name : commons) {
				Field origField = FieldUtils.getField(origClass, name, true);
				Field destField = FieldUtils.getField(destClass, name, true);
				if (origField.getType() == destField.getType()) {
					if (null != MethodUtils.getAccessibleMethod(origClass, getGetterName(origField),
							new Class<?>[] {})) {
						if (null != MethodUtils.getAccessibleMethod(destClass, getSetterName(destField),
								new Class<?>[] { destField.getType() })) {
							names.add(name);
						}
					}
				}
			}
			BeanCopyTools<T, W> copier = new BeanCopyTools<T, W>(new PropertyUtilsBean(), origClass, destClass, names,
					ingoreNull);
			return copier;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private static String getGetterName(Field field) throws Exception {
		if (field.getType() == boolean.class) {
			return IS_PREFIX + StringUtils.capitalize(field.getName());
		} else {
			return GET_PREFIX + StringUtils.capitalize(field.getName());
		}
	}

	private static String getSetterName(Field field) throws Exception {
		return SET_PREFIX + StringUtils.capitalize(field.getName());
	}

	private static List<String> getOrigFieldNames(Class<?> cls, List<String> includes) throws Exception {
		List<String> names = getAllFieldNames(cls);
		if ((null != includes) && (!includes.isEmpty())) {
			names = ListUtils.intersection(includes, names);
		}
		return names;

	}

	private static List<String> getDestFieldNames(Class<?> cls, List<String> excludes) throws Exception {
		List<String> names = getAllFieldNames(cls);
		if ((null != excludes) && (!excludes.isEmpty())) {
			names = ListUtils.subtract(names, excludes);
		}
		return names;
	}

	private static List<String> getAllFieldNames(Class<?> cls) throws Exception {
		List<String> names = new ArrayList<>();
		for (Field field : FieldUtils.getAllFields(cls)) {
			names.add(field.getName());
		}
		return names;
	}
}