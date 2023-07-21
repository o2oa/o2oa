package com.x.base.core.project.bean;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.apache.commons.beanutils.PropertyUtils;

public abstract class PropertyObject implements Serializable {

	private static final long serialVersionUID = 4777469768430179083L;

	public void copyTo(Object o) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		copyTo(o, false, new ArrayList<>());
	}

	public void copyTo(Object o, String... excludes)
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		copyTo(o, false, Arrays.asList(excludes));
	}

	public void copyTo(Object o, Collection<String> excludes)
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		this.copyTo(o, false, new ArrayList<>(excludes));
	}

	public void copyTo(Object o, boolean ignoreNull)
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		copyTo(o, ignoreNull, new ArrayList<>());
	}

	public void copyTo(Object o, boolean ignoreNull, String... excludes)
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		copyTo(o, ignoreNull, Arrays.asList(excludes));
	}

//	public void copyTo(Object o, boolean ignoreNull, Collection<String> excludes)
//			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
//		for (Field fld : FieldUtils.getAllFields(this.getClass())) {
//			if (!excludes.contains(fld.getName())) {
//				if (PropertyUtils.isReadable(this, fld.getName()) && PropertyUtils.isWriteable(o, fld.getName())) {
//					Object value = PropertyUtils.getProperty(this, fld.getName());
//					if (ignoreNull && (null == value)) {
//						continue;
//					}
//					PropertyUtils.setProperty(o, fld.getName(), value);
//				}
//			}
//		}
//	}

	public void copyTo(Object o, boolean ignoreNull, Collection<String> excludes)
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		PropertyObjectDescriptor descriptor = PropertyObjectFactory.descriptor(this, o, excludes);
		for (String name : descriptor.getEffectiveNames()) {
			Object value = descriptor.getPropertyUtilsBean().getProperty(this, name);
			if (ignoreNull && (null == value)) {
				continue;
			}
			descriptor.getPropertyUtilsBean().setProperty(o, name, value);
		}
	}

	public final Object get(String name)
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		return PropertyUtils.isReadable(this, name) ? PropertyUtils.getProperty(this, name) : null;
	}

	@SuppressWarnings("unchecked")
	public final <T> T get(String name, Class<T> clazz)
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Object o = get(name);
		if (null == o) {
			return null;
		}
		return (T) o;
	}

}
