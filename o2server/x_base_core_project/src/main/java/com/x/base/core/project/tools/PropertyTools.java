package com.x.base.core.project.tools;

import java.lang.reflect.Field;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

public class PropertyTools {
	@SuppressWarnings("unchecked")
	public static <T> T getOrElse(Object bean, String name, Class<T> cls, T defaultObject) throws Exception {
		if (null != bean) {
			if (PropertyUtils.isReadable(bean, name)) {
				Object o = PropertyUtils.getProperty(bean, name);
				if (null != o) {
					return (T) o;
				}
			} else {
				Field field = FieldUtils.getField(bean.getClass(), name, true);
				if (null != field) {
					Object o = FieldUtils.readField(field, bean, true);
					if (null != o) {
						return (T) o;
					}
				}
			}
		}
		return defaultObject;
	}
}
