package com.x.processplatform.core.express.query;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.processplatform.core.entity.query.OrderType;

public class GroupEntry extends GsonPropertyObject {

	public GroupEntry() {
		this.column = "";
		this.orderType = OrderType.asc;
	}

	public Boolean available() {
		if (StringUtils.isEmpty(this.column)) {
			return false;
		}
		if (null == this.orderType) {
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