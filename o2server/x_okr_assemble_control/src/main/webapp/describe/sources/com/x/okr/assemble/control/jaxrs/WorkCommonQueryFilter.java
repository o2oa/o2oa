package com.x.okr.assemble.control.jaxrs;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;

public class WorkCommonQueryFilter extends GsonPropertyObject {

	@FieldDescribe( "用于模糊查询的中心工作标题." )
	private String title;
	
	@FieldDescribe( "用于查询的用户身份." )
	private String identity;
	
	@FieldDescribe( "用于查询的中心工作ID列表." )
	private List<String> centerIds; //部署时使用
	
	@FieldDescribe( "用于查询的中心工作信息状态列表：正常、已归档、已删除." )
	private List<String> q_statuses;
	
	@FieldDescribe( "用于查询的中心工作处理状态列表：草稿、执行中、已完成." )
	private List<String> processStatusList;
	
	@FieldDescribe( "用于查询的中心工作类别列表." )
	private List<String> defaultWorkTypes;

	@FieldDescribe( "用于列表排序的属性." )
	private String sequenceField =  JpaObject.sequence_FIELDNAME;
	
	@FieldDescribe( "用于列表排序的方式." )
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
	
	/**
	 * 添加查询的部署处理状态
	 * @param status
	 */
	public void addQueryWorkProcessStatus( String status ) {
		if( processStatusList == null ){
			processStatusList = new ArrayList<String>();
		}
		if( !processStatusList.contains( status )){
			processStatusList.add( status );
		}
	}
}
