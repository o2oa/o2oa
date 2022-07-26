package com.x.base.core.project.bean;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.tools.ListTools;

public class WrapCopier<T, W> {

	private static final Logger LOGGER = LoggerFactory.getLogger(WrapCopier.class);

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

	public W copy(T orig, W dest) {
		if (null == orig) {
			return null;
		}
		copyFields(orig, dest);
		eraseFields(dest);
		return dest;
	}

	private void copyFields(T orig, W dest) {
		// properties 的值会再次覆盖
		if (copyFields.contains(JpaObject.PROPERTIES_FIELDNAME)) {
			try {
				Object o = propertyUtilsBean.getProperty(orig, JpaObject.PROPERTIES_FIELDNAME);
				setDestProperty(dest, JpaObject.PROPERTIES_FIELDNAME, o);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		copyFields.stream().forEach(f -> {
			try {
				// openjpa在访问主键(getId()会执行pcGetId())会发起一个锁定所以在这里对id(xid column)进行单独的处理
				if (StringUtils.equals(f, JpaObject.id_FIELDNAME)) {
					Field field = FieldUtils.getField(orig.getClass(), f, true);
					if (null != field) {
						Object o = FieldUtils.readField(field, orig, true);
						setDestProperty(dest, f, o);
					}
				} else if (!StringUtils.equals(f, JpaObject.PROPERTIES_FIELDNAME)) {
					Object o = propertyUtilsBean.getProperty(orig, f);
					setDestProperty(dest, f, o);
				}
			} catch (Exception e) {
				LOGGER.warn("copyFields:{} to {} error: {}.", f, dest.getClass().getSimpleName(), e.getMessage());
				e.printStackTrace();
			}
		});
	}

	private void setDestProperty(W dest, String fieldName, Object o)
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		if (null != o || (!ignoreNull)) {
			propertyUtilsBean.setProperty(dest, fieldName, o);
		}
	}

	private void eraseFields(W dest) {
		eraseFields.stream().forEach(f -> {
			try {
				propertyUtilsBean.setProperty(dest, f, null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	public W copy(T orig) {
		if (null == orig) {
			return null;
		}
		W w = null;
		try {
			w = this.destClass.getConstructor().newInstance();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		return copy(orig, w);
	}

	public List<W> copy(List<T> origs, List<W> dests) {
		if (null != origs) {
			origs.stream().forEach(t -> {
				dests.add(this.copy(t));
			});
		}
		return dests;
	}

	public List<W> copy(List<T> origs) {
		List<W> dests = new ArrayList<>();
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
