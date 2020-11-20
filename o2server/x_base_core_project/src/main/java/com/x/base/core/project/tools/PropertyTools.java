package com.x.base.core.project.tools;

import java.lang.reflect.Field;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

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

}