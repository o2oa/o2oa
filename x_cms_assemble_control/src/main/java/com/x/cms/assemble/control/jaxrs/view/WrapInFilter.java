package com.x.cms.assemble.control.jaxrs.view;

import com.x.base.core.gson.GsonPropertyObject;
import com.x.base.core.http.annotation.Wrap;
import com.x.cms.core.entity.element.View;

@Wrap(View.class)
public class WrapInFilter extends GsonPropertyObject {

	private String orderField;

	private String orderType;

	private String catagoryId;

	private String viewId;
	
	private String searchDocStatus;

	public String getOrderField() {
		return orderField;
	}

	public void setOrderField(String orderField) {
		this.orderField = orderField;
	}

	public String getOrderType() {
		return orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

	public String getCatagoryId() {
		return catagoryId;
	}

	public void setCatagoryId(String catagoryId) {
		this.catagoryId = catagoryId;
	}

	public String getViewId() {
		return viewId;
	}

	public void setViewId(String viewId) {
		this.viewId = viewId;
	}

	public String getSearchDocStatus() {
		return searchDocStatus;
	}

	public void setSearchDocStatus(String searchDocStatus) {
		this.searchDocStatus = searchDocStatus;
	}

}
