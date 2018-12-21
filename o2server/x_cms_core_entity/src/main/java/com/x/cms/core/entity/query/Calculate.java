package com.x.cms.core.entity.query;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.tools.ListTools;

public class Calculate extends GsonPropertyObject {

	public Calculate() {
		this.isGroup = false;
		this.isAmount = false;
		this.calculateEntryList = new ArrayList<CalculateEntry>();
	}

	private Boolean isGroup;

	private Boolean isAmount;

	private OrderType orderType;

	private OrderEffectType orderEffectType;

	private String id;

	private List<CalculateEntry> calculateEntryList;

	public Boolean available() {
		for (CalculateEntry o : ListTools.nullToEmpty(this.calculateEntryList)) {
			if (o.available()) {
				return true;
			}
		}
		return false;
	}

	public Boolean getIsGroup() {
		return isGroup;
	}

	public void setIsGroup(Boolean isGroup) {
		this.isGroup = isGroup;
	}

	public Boolean getIsAmount() {
		return isAmount;
	}

	public void setIsAmount(Boolean isAmount) {
		this.isAmount = isAmount;
	}

	public List<CalculateEntry> getCalculateEntryList() {
		return calculateEntryList;
	}

	public void setCalculateEntryList(List<CalculateEntry> calculateEntryList) {
		this.calculateEntryList = calculateEntryList;
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

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
