package com.x.base.core.project.tools;

import org.apache.commons.beanutils.PropertyUtils;

public class PropertyTools {
	@SuppressWarnings("unchecked")
	public static <T> T getOrElse(Object bean, String name, Class<T> cls, T defaultObject) throws Exception {
		if (null == bean) {
			return defaultObject;
		}
		if (PropertyUtils.isReadable(bean, name)) {
			return (T) PropertyUtils.getProperty(bean, name);
		}
		return defaultObject;
	}
}
