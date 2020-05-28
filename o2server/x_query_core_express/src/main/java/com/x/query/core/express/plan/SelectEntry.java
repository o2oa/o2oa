package com.x.query.core.express.plan;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.gson.GsonPropertyObject;

public class SelectEntry extends GsonPropertyObject {

	public static final String ORDER_DESC = "desc";
	public static final String ORDER_ASC = "asc";
	public static final String ORDER_ORIGINAL = "original";

	public SelectEntry() {
		this.orderType = ORDER_ORIGINAL;
		this.column = "";
		this.path = "";
		this.id = "";
		this.hideColumn = false;
		this.code = "";
		this.allowOpen = false;
		this.groupEntry = false;
		this.numberOrder = false;
	}

	public Boolean available() {
		return true;
	}

	public String orderType;
	/** 用于前台排序的列值 */
	public Integer orderRank;
	public String column;
	public String displayName;
	public String path;
	public String id;
	public String defaultValue;
	public Boolean hideColumn;
	public String code;
	public Boolean allowOpen;
	public Boolean isName;
	public Boolean groupEntry;
	public Boolean numberOrder;

	public String getColumn() {
		if (StringUtils.isNotEmpty(this.column)) {
			return this.column;
		}
		return this.path;
	}

	public String getDisplayName() {
		if (StringUtils.isNotEmpty(this.displayName)) {
			return this.displayName;
		}
		return this.getColumn();
	}

	public boolean isOrderType(){
		if (StringUtils.equals(SelectEntry.ORDER_ASC, this.orderType)
				|| StringUtils.equals(SelectEntry.ORDER_DESC, this.orderType)) {
			return true;
		}
		return false;
	}

}