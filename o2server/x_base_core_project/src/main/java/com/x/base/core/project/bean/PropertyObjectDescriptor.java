package com.x.base.core.project.bean;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtilsBean;

public class PropertyObjectDescriptor {

	private List<String> effectiveNames = new ArrayList<>();

	private PropertyUtilsBean propertyUtilsBean = new PropertyUtilsBean();

	PropertyObjectDescriptor(List<String> effectiveNames, PropertyUtilsBean propertyUtilsBean) {
		this.effectiveNames = effectiveNames;
		this.propertyUtilsBean = propertyUtilsBean;
	}

	protected List<String> getEffectiveNames() {
		return effectiveNames;
	}

	protected PropertyUtilsBean getPropertyUtilsBean() {
		return propertyUtilsBean;
	}

}
