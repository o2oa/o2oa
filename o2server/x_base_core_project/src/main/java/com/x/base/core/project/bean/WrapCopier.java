package com.x.base.core.project.bean;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.commons.lang3.reflect.FieldUtils;

import com.x.base.core.project.tools.ListTools;

public class WrapCopier<T, W> {

	private List<String> copyFields = new ArrayList<>();

	private List<String> eraseFields = new ArrayList<>();

	private Class<T> origClass;

	private Class<W> destClass;

	private PropertyUtilsBean propertyUtilsBean;

	private boolean ignoreNull;

	@SuppressWarnings("unused")
	private WrapCopier() {
	}

	protected WrapCopier(PropertyUtilsBean propertyUtilsBean, Class<T> origClass, Class<W> destClass,
			List<String> copyFields, List<String> eraseFields, boolean ignoreNull) {
		this.propertyUtilsBean = propertyUtilsBean;
		this.origClass = origClass;
		this.destClass = destClass;
		if (ListTools.isNotEmpty(copyFields)) {
			this.copyFields = copyFields;
		}
		if (ListTools.isNotEmpty(eraseFields)) {
			this.eraseFields = eraseFields;
		}
		this.ignoreNull = ignoreNull;
	}

	public W copy(T orig, W dest) throws Exception {
		if (null == orig) {
			return null;
		}
		copyFields.stream().forEach(f -> {
			try {
				Object o = propertyUtilsBean.getProperty(orig, f);
				if (null != o || (!ignoreNull)) {
					propertyUtilsBean.setProperty(dest, f, o);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		eraseFields.stream().forEach(f -> {
			try {
				propertyUtilsBean.setProperty(dest, f, null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
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
			origs.stream().forEach(t -> {
				try {
					dests.add(this.copy(t));
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
		}
		return dests;
	}

	public List<W> copy(List<T> origs) throws Exception {
		List<W> dests = new ArrayList<W>();
		if (null != origs) {
			origs.stream().forEach(t -> {
				try {
					dests.add(this.copy(t));
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
		}
		return dests;
	}

	public Class<T> getOrigClass() {
		return origClass;
	}

	public Class<W> getDestClass() {
		return destClass;
	}

	public List<String> getCopyFields() {
		return copyFields;
	}

	public List<String> getEraseFields() {
		return eraseFields;
	}

	public Boolean fieldInOrigNotInDest(String fieldName) {
		return (null != FieldUtils.getField(origClass, fieldName, true)
				&& (null == FieldUtils.getField(destClass, fieldName, true)));
	}

	public Boolean fieldNotInOrigInDest(String fieldName) {
		return (null == FieldUtils.getField(origClass, fieldName, true)
				&& (null != FieldUtils.getField(destClass, fieldName, true)));
	}

}
