package com.x.base.core.bean;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtilsBean;

public class BeanCopyTools<T, W> {

	private List<String> fields = new ArrayList<>();

	private Class<T> origClass;

	private Class<W> destClass;

	private PropertyUtilsBean propertyUtilsBean;

	private boolean ignoreNull;

	@SuppressWarnings("unused")
	private BeanCopyTools() {
	}

	protected BeanCopyTools(PropertyUtilsBean propertyUtilsBean, Class<T> origClass, Class<W> destClass,
			List<String> fields, boolean ignoreNull) {
		this.propertyUtilsBean = propertyUtilsBean;
		this.origClass = origClass;
		this.destClass = destClass;
		this.fields = fields;
		this.ignoreNull = ignoreNull;
	}

	public W copy(T orig, W dest) throws Exception {
		if (null == orig) {
			return null;
		}
		for (String field : fields) {
			Object o = propertyUtilsBean.getProperty(orig, field);
			if (ignoreNull && null == o) {
				continue;
			}
			propertyUtilsBean.setProperty(dest, field, o);
		}
		return dest;
	}

	public W copy(T orig) throws Exception {
		if (null == orig) {
			return null;
		}
		W w = this.destClass.newInstance();
		return copy(orig, w);
	}

	public List<W> copy(List<T> origs, List<W> dests) throws Exception {
		if (null != origs) {
			for (T t : origs) {
				dests.add(this.copy(t));
			}
		}
		return dests;
	}

	public List<W> copy(List<T> origs) throws Exception {
		List<W> list = new ArrayList<W>();
		if (null != origs) {
			for (T t : origs) {
				list.add(this.copy(t));
			}
		}
		return list;
	}

	public List<String> getFields() {
		return fields;
	}

	public Class<T> getOrigClass() {
		return origClass;
	}

	public Class<W> getDestClass() {
		return destClass;
	}

}
