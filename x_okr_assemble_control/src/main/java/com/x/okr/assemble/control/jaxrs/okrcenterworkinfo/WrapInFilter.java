package com.x.okr.assemble.control.jaxrs.okrcenterworkinfo;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.gson.GsonPropertyObject;
import com.x.base.core.http.annotation.Wrap;
import com.x.okr.entity.OkrCenterWorkInfo;

@Wrap(OkrCenterWorkInfo.class)
public class WrapInFilter extends GsonPropertyObject {

	private String title;
	private String identity;
	private List<String> centerIds; //部署时使用

	private List<String> q_statuses;
	private List<String> processStatusList;
	private List<String> defaultWorkTypes;

	private String sequenceField = "sequence";
	private String key;
	private String order = "DESC";
	// 是否查询下级组织
	private boolean querySubOrganizatin = true;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<String> getCenterIds() {
		return centerIds;
	}

	public void setCenterIds(List<String> centerIds) {
		this.centerIds = centerIds;
	}

	public List<String> getQ_statuses() {
		return q_statuses;
	}

	public void setQ_statuses(List<String> q_statuses) {
		this.q_statuses = q_statuses;
	}

	public List<String> getProcessStatusList() {
		return processStatusList;
	}

	public void setProcessStatusList(List<String> processStatusList) {
		this.processStatusList = processStatusList;
	}

	public List<String> getDefaultWorkTypes() {
		return defaultWorkTypes;
	}

	public void setDefaultWorkTypes(List<String> defaultWorkTypes) {
		this.defaultWorkTypes = defaultWorkTypes;
	}

	public String getSequenceField() {
		return sequenceField;
	}

	public void setSequenceField(String sequenceField) {
		this.sequenceField = sequenceField;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public boolean isQuerySubOrganizatin() {
		return querySubOrganizatin;
	}

	public void setQuerySubOrganizatin(boolean querySubOrganizatin) {
		this.querySubOrganizatin = querySubOrganizatin;
	}

	public String getIdentity() {
		return identity;
	}

	public void setIdentity(String identity) {
		this.identity = identity;
	}

	/**
	 * 添加查询的信息状态
	 * 
	 * @param status
	 */
	public void addQueryInfoStatus(String status) {
		if (q_statuses == null) {
			q_statuses = new ArrayList<String>();
		}
		if (!q_statuses.contains(status)) {
			q_statuses.add(status);
		}
	}
}
