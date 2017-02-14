package com.x.cms.core.entity.query;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.gson.GsonPropertyObject;

public class CalculateEntry extends GsonPropertyObject {

	private String column;

	private CalculateType calculateType;

	private OrderType orderType;

	private OrderEffectType orderEffectType;

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

}
