package com.x.cms.core.entity.query;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.gson.GsonPropertyObject;

public class CalculateEntry extends GsonPropertyObject {

	private String column;

	private CalculateType calculateType;

	private String displayName;

	private OrderType orderType;

	private OrderEffectType orderEffectType;
	
	private String id;

	public Boolean available() {
		if (StringUtils.isEmpty(this.column)) {
			return false;
		}
		if (null == this.calculateType) {
			return false;
		}
		if (null == this.orderType) {
			return false;
		}
		if (null == this.orderEffectType) {
			return false;
		}
		return true;
	}

	public String getColumn() {
		return column;
	}

	public void setColumn(String column) {
		this.column = column;
	}

	public CalculateType getCalculateType() {
		return calculateType;
	}

	public void setCalculateType(CalculateType calculateType) {
		this.calculateType = calculateType;
	}

	public OrderType getOrderType() {
		return orderType;
	}

	public void setOrderType(OrderType orderType) {
		this.orderType = orderType;
	}

	public OrderEffectType getOrderEffectType() {
		return orderEffectType;
	}

	public void setOrderEffectType(OrderEffectType orderEffectType) {
		this.orderEffectType = orderEffectType;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
