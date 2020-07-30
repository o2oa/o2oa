package com.x.query.core.express.plan;

import java.util.List;

import org.apache.commons.collections4.list.TreeList;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.tools.ListTools;

public class Calculate extends GsonPropertyObject {

	public Calculate() {
		this.isGroup = false;
		this.calculateList = new TreeList<CalculateEntry>();
	}

	public static final String GROUPMERGETYPE_SPECIFIED = "specified";
	public static final String GROUPMERGETYPE_ITEM = "item";
	public static final String GROUPMERGETYPE_INTERSECTION = "intersection";
	public static final String GROUPMERGETYPE_SUM = "sum";

	@FieldDescribe("是否是分类视图")
	public Boolean isGroup;

	@FieldDescribe("是否总计")
	public Boolean isAmount;

	@FieldDescribe("排序类型")
	public String orderType;

	@FieldDescribe("排序列")
	public String orderColumn;

	@FieldDescribe("分类自定义标记")
	public List<String> groupSpecifiedList;

	@FieldDescribe("分类视图标题")
	public String title;

	@FieldDescribe("specified,item,intersection,sum")
	public String groupMergeType;

	public List<CalculateEntry> calculateList;
	
	@FieldDescribe("图表")
	public List<String> chart;
	
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

	public String getOrderType() {
		return orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

	public String getOrderColumn() {
		return orderColumn;
	}

	public void setOrderColumn(String orderColumn) {
		this.orderColumn = orderColumn;
	}

	public List<String> getGroupSpecifiedList() {
		return groupSpecifiedList;
	}

	public void setGroupSpecifiedList(List<String> groupSpecifiedList) {
		this.groupSpecifiedList = groupSpecifiedList;
	}

	public String getGroupMergeType() {
		return groupMergeType;
	}

	public void setGroupMergeType(String groupMergeType) {
		this.groupMergeType = groupMergeType;
	}

	public List<CalculateEntry> getCalculateList() {
		return calculateList;
	}

	public void setCalculateList(List<CalculateEntry> calculateList) {
		this.calculateList = calculateList;
	}

	public List<String> getChart() {
		return chart;
	}

	public void setChart(List<String> chart) {
		this.chart = chart;
	}

	public Boolean available() {
		for (CalculateEntry o : ListTools.nullToEmpty(this.calculateList)) {
			if (o.available()) {
				return true;
			}
		}
		return false;
	}

	public CalculateEntry find(String id) {
		for (CalculateEntry o : this.calculateList) {
			if (StringUtils.equals(o.id, id)) {
				return o;
			}
		}
		return null;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	};

}