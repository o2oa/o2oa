package com.x.base.core.project.tools;

import org.apache.commons.beanutils.PropertyUtils;

public class PropertyTools {
	@SuppressWarnings("unchecked")
	public static <T> T getOrElse(Object bean, String name, Class<T> cls, T defaultObject) throws Exception {
		if ((null != bean) && PropertyUtils.isReadable(bean, name)) {
			Object o = PropertyUtils.getProperty(bean, name);
			if (null != o) {
				return (T) o;
			}
		}
		return defaultObject;
	}
}
