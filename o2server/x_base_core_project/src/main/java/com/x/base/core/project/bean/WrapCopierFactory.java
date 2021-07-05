package com.x.base.core.project.bean;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

import com.x.base.core.project.tools.ListTools;

public class WrapCopierFactory {

	private static final String GET_PREFIX = "get";
	private static final String SET_PREFIX = "set";
	private static final String IS_PREFIX = "is";

	private WrapCopierFactory() {
		// nothing
	}

	@SuppressWarnings("rawtypes")
	private static final Map<Key, WrapCopier> MAPPEDWO = new ConcurrentHashMap<>();
	@SuppressWarnings("rawtypes")
	private static final Map<Key, WrapCopier> MAPPEDWI = new ConcurrentHashMap<>();

	public static <T, W> WrapCopier<T, W> wo(Class<T> origClass, Class<W> destClass, List<String> includes,
			List<String> excludes) {
		return wo(origClass, destClass, includes, excludes, true);
	}

	@SuppressWarnings("unchecked")
	public static <T, W> WrapCopier<T, W> wo(Class<T> origClass, Class<W> destClass, List<String> includes,
			List<String> excludes, boolean ignoreNull) {
		return MAPPEDWO.compute(new Key(origClass, destClass, includes, excludes, ignoreNull),
				(k, v) -> (null == v) ? createWo(origClass, destClass, includes, excludes, ignoreNull) : v);
	}

	@SuppressWarnings("unchecked")
	public static <T, W> WrapCopier<T, W> wi(Class<T> origClass, Class<W> destClass, List<String> includes,
			List<String> excludes, boolean ignoreNull) {
		return MAPPEDWI.compute(new Key(origClass, destClass, includes, excludes, ignoreNull),
				(k, v) -> (null == v) ? createWi(origClass, destClass, includes, excludes, ignoreNull) : v);
	}

	private static <T, W> WrapCopier<T, W> createWo(Class<T> origClass, Class<W> destClass, List<String> includes,
			List<String> excludes, boolean ignoreNull) {
		try {
			List<String> origFieldNames = getAllFieldNames(origClass);
			List<String> destFieldNames = getAllFieldNames(destClass);
			List<String> copyFieldNames = new ArrayList<>();
			ListTools.includesExcludes(ListUtils.intersection(origFieldNames, destFieldNames), includes, excludes)
					.stream().forEach(name -> {
						if (accessible(origClass, destClass, name)) {
							copyFieldNames.add(name);
						}
					});
			List<String> eraseFieldNames = new ArrayList<>();
			ListUtils.subtract(destFieldNames, copyFieldNames).stream().forEach(name -> {
				if (accessible(origClass, destClass, name)) {
					eraseFieldNames.add(name);
				}
			});
			return new WrapCopier<>(new PropertyUtilsBean(), origClass, destClass, copyFieldNames, eraseFieldNames,
					ignoreNull);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// 需要erase的属性也必须在orig中有,否则新增加的属性将直接被擦除
	private static <T, W> boolean accessible(Class<T> origClass, Class<W> destClass, String name) {
		try {
			Field origField = FieldUtils.getField(origClass, name, true);
			Field destField = FieldUtils.getField(destClass, name, true);
			if (((null != origField) && (null != destField)) && (origField.getType() == destField.getType())
					&& (null != MethodUtils.getAccessibleMethod(origClass, getGetterName(origField)))
					&& (null != MethodUtils.getAccessibleMethod(destClass, getSetterName(destField),
							destField.getType()))) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static <T, W> WrapCopier<T, W> wi(Class<T> origClass, Class<W> destClass, List<String> includes,
			List<String> excludes) {
		return wi(origClass, destClass, includes, excludes, true);
	}

	public static <T, W> WrapCopier<T, W> createWi(Class<T> origClass, Class<W> destClass, List<String> includes,
			List<String> excludes, boolean ignoreNull) {
		try {
			List<String> origFieldNames = getAllFieldNames(origClass);
			List<String> destFieldNames = getAllFieldNames(destClass);
			List<String> copyFieldNames = new ArrayList<>();
			ListTools.includesExcludes(ListUtils.intersection(origFieldNames, destFieldNames), includes, excludes)
					.stream().forEach(name -> {
						if (accessible(origClass, destClass, name)) {
							copyFieldNames.add(name);
						}
					});
			return new WrapCopier<>(new PropertyUtilsBean(), origClass, destClass, copyFieldNames, new ArrayList<>(),
					ignoreNull);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private static String getGetterName(Field field) {
		if (field.getType() == boolean.class) {
			return IS_PREFIX + StringUtils.capitalize(field.getName());
		} else {
			return GET_PREFIX + StringUtils.capitalize(field.getName());
		}
	}

	private static String getSetterName(Field field) {
		return SET_PREFIX + StringUtils.capitalize(field.getName());
	}

	private static List<String> getAllFieldNames(Class<?> cls) {
		List<String> names = new ArrayList<>();
		for (Field field : FieldUtils.getAllFields(cls)) {
			names.add(field.getName());
		}
		return names;
	}

	private static class Key {

		private Class<?> origClass;
		private Class<?> destClass;
		private String joinInclude;
		private String joinExclude;
		private boolean ignoreNull;

		private Key(Class<?> origClass, Class<?> destClass, List<String> includes, List<String> excludes,
				boolean ignoreNull) {
			this.origClass = origClass;
			this.destClass = destClass;
			if (includes == null || includes.isEmpty()) {
				this.joinInclude = "";
			} else {
				this.joinInclude = includes.stream().sorted().collect(Collectors.joining(","));
			}
			if (excludes == null || excludes.isEmpty()) {
				this.joinExclude = "";
			} else {
				this.joinExclude = excludes.stream().sorted().collect(Collectors.joining(","));
			}
			this.ignoreNull = ignoreNull;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((destClass == null) ? 0 : destClass.hashCode());
			result = prime * result + ((origClass == null) ? 0 : origClass.hashCode());
			result = prime * result + ((joinExclude == null) ? 0 : joinExclude.hashCode());
			result = prime * result + ((joinInclude == null) ? 0 : joinInclude.hashCode());
			result = prime * result + (ignoreNull ? 1231 : 1237);
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (this.getClass() != obj.getClass())
				return false;
			Key other = (Key) obj;
			if (!Objects.equals(this.origClass, other.origClass)) {
				return false;
			}
			if (!Objects.equals(this.destClass, other.destClass)) {
				return false;
			}
			if (!StringUtils.equals(this.joinInclude, other.joinInclude)) {
				return false;
			}
			if (!StringUtils.equals(this.joinExclude, other.joinExclude)) {
				return false;
			}
			return this.ignoreNull == other.ignoreNull;
		}

	}
}