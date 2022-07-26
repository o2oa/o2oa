package com.x.base.core.project.tools;

import java.lang.reflect.Field;
import java.util.*;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

import com.x.base.core.project.gson.XGsonBuilder;

public class PropertyTools {
	@SuppressWarnings("unchecked")
	public static <T> T getOrElse(Object bean, String name, Class<T> cls, T defaultObject) throws Exception {
		if (null != bean) {
			try {
				// 如果使用PropertyUtils.isReadable那么Nested就会失效,无法获取map类型中的值.
				// Object o = PropertyUtils.getNestedProperty(bean, name);
				Object o = PropertyUtils.getProperty(bean, name);
				if (null != o) {
					return (T) o;
				}
			} catch (NoSuchMethodException e) {
				// java.lang.NoSuchMethodException: Unknown property 'abc' on class 'class
				// java.lang.Object'
				// nothing pass
			}
			Field field = FieldUtils.getField(bean.getClass(), name, true);
			if (null != field) {
				Object o = FieldUtils.readField(field, bean, true);
				if (null != o) {
					return (T) o;
				}
			}
		}
		return defaultObject;
	}

	public static <T> Map<String, String> fieldMatchKeyword(final List<String> properties, T t, String keyword, Boolean caseSensitive, Boolean matchWholeWord, Boolean matchRegExp) throws Exception {
		Map<String, String> map = new HashMap<>();
		if(ListTools.isNotEmpty(properties) && StringUtils.isNotBlank(keyword)) {
			for (String name : properties) {
				Object o = PropertyUtils.getProperty(t, name);
				if (o!=null) {
					String content = "";
					if (o instanceof Collection<?> || o instanceof Map<?, ?>){
						content = XGsonBuilder.toJson(o);
					} else {
						content = String.valueOf(o);
					}
					if (StringTools.matchKeyword(keyword, content, caseSensitive, matchWholeWord, matchRegExp)) {
						map.put(name, content);
					}
				}
			}
		}
		return map;
	}

	public static boolean hasField(Class<?> cls, String fieldName) {
		if(StringUtils.isBlank(fieldName)){
			return false;
		}
		for (Field field : FieldUtils.getAllFields(cls)) {
			if(field.getName().equals(fieldName)){
				return true;
			}
		}
		return false;
	}

}
