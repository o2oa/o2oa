package com.x.crm.assemble.control.wrapin;

import org.apache.commons.collections4.map.ListOrderedMap;

import com.x.base.core.application.jaxrs.EqualsTerms;
import com.x.base.core.entity.annotation.EntityFieldDescribe;
import com.x.base.core.gson.GsonPropertyObject;

public class WrapInFilterCustomerBaseInfo extends GsonPropertyObject {
	@EntityFieldDescribe("用于查询的具体工作项ID.")
	private String workId;

	@EntityFieldDescribe("用于列表排序的属性.")
	private String sequenceField = "sequence";

	@EntityFieldDescribe("用于列表排序的方式.")
	private String order = "DESC";

	//--------模糊查询关键字------------
	@EntityFieldDescribe("模糊查询关键字")
	private String fuzzySearchKey;
	//--------------------

	//-----------------------
	@EntityFieldDescribe("相等的字段")
	private EqualsTerms equalFieldName;

	public String getWorkId() {
		return workId;
	}

	public void setWorkId(String workId) {
		this.workId = workId;
	}

	public String getSequenceField() {
		return sequenceField;
	}

	public void setSequenceField(String sequenceField) {
		this.sequenceField = sequenceField;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public String getFuzzySearchKey() {
		return fuzzySearchKey;
	}

	public void setFuzzySearchKey(String fuzzySearchKey) {
		this.fuzzySearchKey = fuzzySearchKey;
	}

	public EqualsTerms getEqualFieldName() {
		return equalFieldName;
	}

	public void setEqualFieldName(EqualsTerms equalFieldName) {
		this.equalFieldName = equalFieldName;
	}

}
