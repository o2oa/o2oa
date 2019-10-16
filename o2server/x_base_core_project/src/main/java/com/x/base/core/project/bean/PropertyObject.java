package com.x.base.core.project.bean;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

public abstract class PropertyObject {
	public void copyTo(Object o) throws Exception {
		copyTo(o, false, new String[] {});
	}

	public void copyTo(Object o, String... excludes) throws Exception {
		List<String> list = new ArrayList<String>();
		for (String str : excludes) {
			list.add(str);
		}
		copyTo(o, false, list);
	}

	public void copyTo(Object o, Collection<String> excludes) throws Exception {
		this.copyTo(o, false, excludes);
	}

	public void copyTo(Object o, boolean ignoreNull) throws Exception {
		copyTo(o, ignoreNull, new String[] {});
	}

	public void copyTo(Object o, boolean ignoreNull, String... excludes) throws Exception {
		List<String> list = new ArrayList<String>();
		for (String str : excludes) {
			list.add(str);
		}
		copyTo(o, ignoreNull, list);
	}

	public void copyTo(Object o, boolean ignoreNull, Collection<String> excludes) throws Exception {
		for (Field fld : FieldUtils.getAllFields(this.getClass())) {
			if (!excludes.contains(fld.getName())) {
				if (PropertyUtils.isReadable(this, fld.getName()) && PropertyUtils.isWriteable(o, fld.getName())) {
					Object value = PropertyUtils.getProperty(this, fld.getName());
					if (ignoreNull && (null == value)) {
						continue;
					}
					PropertyUtils.setProperty(o, fld.getName(), value);
				}
			}
		}
	}

	public final Object get(String name) throws Exception {
		return PropertyUtils.isReadable(this, name) ? PropertyUtils.getProperty(this, name) : null;
	}

	@SuppressWarnings("unchecked")
	public final <T> T get(String name, Class<T> clazz) throws Exception {
		Object o = get(name);
		if (null == o) {
			return null;
		}
		return (T) o;
	}

}
