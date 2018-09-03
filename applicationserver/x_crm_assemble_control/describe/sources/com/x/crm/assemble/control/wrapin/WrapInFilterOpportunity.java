package com.x.crm.assemble.control.wrapin;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.crm.core.entity.Opportunity;

public class WrapInFilterOpportunity extends Opportunity {
	private static final long serialVersionUID = -8051251287263083564L;
	public static List<String> Excludes = new ArrayList<>(JpaObject.FieldsUnmodify);

	@FieldDescribe("用于查询的具体工作项ID.")
	private String workId;

	@FieldDescribe("用于列表排序的属性.")
	private String sequenceField = "sequence";

	@FieldDescribe("用于列表排序的方式.")
	private String order = "DESC";

	@FieldDescribe("用于模糊查询.")
	public String fuzzySearchKey;

	public String getFuzzySearchKey() {
		return fuzzySearchKey;
	}

	public void setFuzzySearchKey(String fuzzySearchKey) {
		this.fuzzySearchKey = fuzzySearchKey;
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

}
