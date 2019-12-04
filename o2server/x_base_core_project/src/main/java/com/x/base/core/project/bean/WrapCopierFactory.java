package com.x.base.core.project.bean;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

import com.x.base.core.project.tools.ListTools;

public class WrapCopierFactory {

	private static String GET_PREFIX = "get";
	private static String SET_PREFIX = "set";
	private static String IS_PREFIX = "is";

	public static <T, W> WrapCopier<T, W> wo(Class<T> origClass, Class<W> destClass, List<String> includes,
			List<String> excludes) {
		return wo(origClass, destClass, includes, excludes, true);
	}

	public static <T, W> WrapCopier<T, W> wo(Class<T> origClass, Class<W> destClass, List<String> includes,
			List<String> excludes, boolean ingoreNull) {
		try {
			List<String> origFieldNames = getAllFieldNames(origClass);
			List<String> destFieldNames = getAllFieldNames(destClass);
			List<String> copyFieldNames = new ArrayList<>();
			ListTools.includesExcludes(ListUtils.intersection(origFieldNames, destFieldNames), includes, excludes)
					.stream().forEach(s -> {
						try {
							Field origField = FieldUtils.getField(origClass, s, true);
							Field destField = FieldUtils.getField(destClass, s, true);
							if ((null != origField) && (null != destField)) {
								if (origField.getType() == destField.getType()) {
									if (null != MethodUtils.getAccessibleMethod(origClass, getGetterName(origField),
											new Class<?>[] {})) {
										if (null != MethodUtils.getAccessibleMethod(destClass, getSetterName(destField),
												new Class<?>[] { destField.getType() })) {
											copyFieldNames.add(s);
										}
									}
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					});
			List<String> eraseFieldNames = new ArrayList<>();
			ListUtils.subtract(destFieldNames, copyFieldNames).stream().forEach(s -> {
				try {
					Field origField = FieldUtils.getField(origClass, s, true);
					Field destField = FieldUtils.getField(destClass, s, true);
					if ((null != origField) && (null != destField)) {
						if (origField.getType() == destField.getType()) {
							if (null != MethodUtils.getAccessibleMethod(origClass, getGetterName(origField),
									new Class<?>[] {})) {
								if (null != MethodUtils.getAccessibleMethod(destClass, getSetterName(destField),
										new Class<?>[] { destField.getType() })) {
									eraseFieldNames.add(s);
								}
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
			WrapCopier<T, W> copier = new WrapCopier<T, W>(new PropertyUtilsBean(), origClass, destClass,
					copyFieldNames, eraseFieldNames, ingoreNull);
			return copier;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static <T, W> WrapCopier<T, W> wi(Class<T> origClass, Class<W> destClass, List<String> includes,
			List<String> excludes) {
		return wi(origClass, destClass, includes, excludes, true);
	}

	public static <T, W> WrapCopier<T, W> wi(Class<T> origClass, Class<W> destClass, List<String> includes,
			List<String> excludes, boolean ingoreNull) {
		try {
			List<String> origFieldNames = getAllFieldNames(origClass);
			List<String> destFieldNames = getAllFieldNames(destClass);
			List<String> copyFieldNames = new ArrayList<>();
			ListTools.includesExcludes(ListUtils.intersection(origFieldNames, destFieldNames), includes, excludes)
					.stream().forEach(s -> {
						try {
							Field origField = FieldUtils.getField(origClass, s, true);
							Field destField = FieldUtils.getField(destClass, s, true);
							if ((null != origField) && (null != destField)) {
								if (origField.getType() == destField.getType()) {
									if (null != MethodUtils.getAccessibleMethod(origClass, getGetterName(origField),
											new Class<?>[] {})) {
										if (null != MethodUtils.getAccessibleMethod(destClass, getSetterName(destField),
												new Class<?>[] { destField.getType() })) {
											copyFieldNames.add(s);
										}
									}
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					});
			WrapCopier<T, W> copier = new WrapCopier<T, W>(new PropertyUtilsBean(), origClass, destClass,
					copyFieldNames, new ArrayList<String>(), ingoreNull);
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

	private static List<String> getAllFieldNames(Class<?> cls) throws Exception {
		List<String> names = new ArrayList<>();
		for (Field field : FieldUtils.getAllFields(cls)) {
			names.add(field.getName());
		}
		return names;
	}
}