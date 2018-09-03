package com.x.cms.core.entity.query;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.gson.GsonPropertyObject;

public class OrderEntry extends GsonPropertyObject {

	public OrderEntry() {
		this.column = "";
		this.orderType = OrderType.original;
	}

	public Boolean available() {
		if (StringUtils.isEmpty(this.column)) {
			return false;
		}
		if (null == this.orderType || Objects.equals(this.orderType, OrderType.original)) {
			return false;
		}
		return true;
	}

	private String column;

	private OrderType orderType;

	public String getColumn() {
		return column;
	}

	public void setColumn(String column) {
		this.column = column;
	}

	public OrderType getOrderType() {
		return orderType;
	}

	public void setOrderType(OrderType orderType) {
		this.orderType = orderType;
	}

}