package com.x.processplatform.core.express.query;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.processplatform.core.entity.query.OrderType;
import com.x.processplatform.core.entity.query.SelectType;

public class SelectEntry extends GsonPropertyObject {

	public SelectEntry() {
		this.orderType = OrderType.original;
		this.selectType = SelectType.attribute;
		this.column = "";
		this.attribute = "";
		this.path = "";
		this.id = "";
		this.hideColumn = false;
		this.code = "";
		this.allowOpen = false;

	}

	public Boolean available() {
		if (null == this.orderType) {
			return false;
		}
		if (null == this.selectType) {
			return false;
		}
		switch (this.selectType) {
		case attribute:
			if (StringUtils.isEmpty(attribute)) {
				return false;
			}
			break;
		case path:
			if (StringUtils.isEmpty(path)) {
				return false;
			}
			break;
		case padding:
			if (StringUtils.isEmpty(column)) {
				return false;
			}
			break;
		}
		return true;
	}

	private OrderType orderType;
	private Integer orderRank;
	private SelectType selectType;
	private String attribute;
	private String column;
	private String displayName;
	private String path;
	private String id;
	private String defaultValue;
	private Boolean hideColumn;
	private String code;
	private Boolean allowOpen;

	public String getColumn() {
		if (StringUtils.isNotEmpty(this.column)) {
			return this.column;
		}
		if (Objects.equals(this.selectType, SelectType.attribute)) {
			return this.attribute;
		}
		if (Objects.equals(this.selectType, SelectType.path)) {
			if (StringUtils.contains(path, ".")) {
				return StringUtils.substringAfterLast(this.path, ".");
			} else {
				return this.path;
			}
		}
		return "";
	}

	public String getDisplayName() {
		if (StringUtils.isNotEmpty(this.displayName)) {
			return this.displayName;
		}
		return this.getColumn();
	}

	public OrderType getOrderType() {
		return orderType;
	}

	public void setOrderType(OrderType orderType) {
		this.orderType = orderType;
	}

	public Integer getOrderRank() {
		return orderRank;
	}

	public void setOrderRank(Integer orderRank) {
		this.orderRank = orderRank;
	}

	public SelectType getSelectType() {
		return selectType;
	}

	public void setSelectType(SelectType selectType) {
		this.selectType = selectType;
	}

	public String getAttribute() {
		return attribute;
	}

	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public void setColumn(String column) {
		this.column = column;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public Boolean getHideColumn() {
		return hideColumn;
	}

	public void setHideColumn(Boolean hideColumn) {
		this.hideColumn = hideColumn;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Boolean getAllowOpen() {
		return allowOpen;
	}

	public void setAllowOpen(Boolean allowOpen) {
		this.allowOpen = allowOpen;
	}

}
