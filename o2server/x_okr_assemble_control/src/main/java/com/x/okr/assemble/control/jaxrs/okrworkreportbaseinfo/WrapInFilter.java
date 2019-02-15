package com.x.okr.assemble.control.jaxrs.okrworkreportbaseinfo;
import java.util.ArrayList;
import java.util.List;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.gson.GsonPropertyObject;

public class WrapInFilter extends GsonPropertyObject {
	private String query_deployerName;
	
	private String query_deployerUnitName;
	
	private String query_deployerTopUnitName;
	
	private String title ;
	
	private String processIdentity;
	
	private List<String> workIds;
	
	private List<String> processStatusList;
	
	private List<String> deployerNames;
	
	private List<String> deployerUnitNames;
	
	private List<String> deployerTopUnitNames;
	
	private String query_creatorName;
	
	private String query_creatorUnitName;
	
	private String query_creatorTopUnitName;
	
	private List<String> creatorNames;
	
	private List<String> creatorUnitNames;
	
	private List<String> creatorTopUnitNames;
	
	private String sequenceField =  JpaObject.sequence_FIELDNAME;
	
	private String key;
	
	private String order = "DESC";
	
	private List<String> q_statuses;

	public String getQuery_deployerName() {
		return query_deployerName;
	}

	public void setQuery_deployerName(String query_deployerName) {
		this.query_deployerName = query_deployerName;
	}

	public String getQuery_deployerUnitName() {
		return query_deployerUnitName;
	}

	public void setQuery_deployerUnitName(String query_deployerUnitName) {
		this.query_deployerUnitName = query_deployerUnitName;
	}

	public String getQuery_deployerTopUnitName() {
		return query_deployerTopUnitName;
	}

	public void setQuery_deployerTopUnitName(String query_deployerTopUnitName) {
		this.query_deployerTopUnitName = query_deployerTopUnitName;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getProcessIdentity() {
		return processIdentity;
	}

	public void setProcessIdentity(String processIdentity) {
		this.processIdentity = processIdentity;
	}

	public List<String> getWorkIds() {
		return workIds;
	}

	public void setWorkIds(List<String> workIds) {
		this.workIds = workIds;
	}
		
	public List<String> getProcessStatusList() {
		return processStatusList;
	}

	public void setProcessStatusList(List<String> processStatusList) {
		this.processStatusList = processStatusList;
	}

	public List<String> getDeployerNames() {
		return deployerNames;
	}

	public void setDeployerNames(List<String> deployerNames) {
		this.deployerNames = deployerNames;
	}

	public List<String> getDeployerUnitNames() {
		return deployerUnitNames;
	}

	public void setDeployerUnitNames(List<String> deployerUnitNames) {
		this.deployerUnitNames = deployerUnitNames;
	}

	public List<String> getDeployerTopUnitNames() {
		return deployerTopUnitNames;
	}

	public void setDeployerTopUnitNames(List<String> deployerTopUnitNames) {
		this.deployerTopUnitNames = deployerTopUnitNames;
	}

	public String getQuery_creatorName() {
		return query_creatorName;
	}

	public void setQuery_creatorName(String query_creatorName) {
		this.query_creatorName = query_creatorName;
	}

	public String getQuery_creatorUnitName() {
		return query_creatorUnitName;
	}

	public void setQuery_creatorUnitName(String query_creatorUnitName) {
		this.query_creatorUnitName = query_creatorUnitName;
	}

	public String getQuery_creatorTopUnitName() {
		return query_creatorTopUnitName;
	}

	public void setQuery_creatorTopUnitName(String query_creatorTopUnitName) {
		this.query_creatorTopUnitName = query_creatorTopUnitName;
	}

	public List<String> getCreatorNames() {
		return creatorNames;
	}

	public void setCreatorNames(List<String> creatorNames) {
		this.creatorNames = creatorNames;
	}

	public List<String> getCreatorUnitNames() {
		return creatorUnitNames;
	}

	public void setCreatorUnitNames(List<String> creatorUnitNames) {
		this.creatorUnitNames = creatorUnitNames;
	}

	public List<String> getCreatorTopUnitNames() {
		return creatorTopUnitNames;
	}

	public void setCreatorTopUnitNames(List<String> creatorTopUnitNames) {
		this.creatorTopUnitNames = creatorTopUnitNames;
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

	public List<String> getQ_statuses() {
		return q_statuses;
	}

	public void setQ_statuses(List<String> q_statuses) {
		this.q_statuses = q_statuses;
	}
	
	/**
	 * 添加查询的信息状态
	 * @param status
	 */
	public void addQueryInfoStatus( String status ) {
		if( q_statuses == null ){
			q_statuses = new ArrayList<String>();
		}
		if( !q_statuses.contains( status )){
			q_statuses.add( status );
		}
	}
	
	/**
	 * 添加查询的信息状态
	 * @param status
	 */
	public void addQueryProcessStatus( String status ) {
		if( processStatusList == null ){
			processStatusList = new ArrayList<String>();
		}
		if( !processStatusList.contains( status )){
			processStatusList.add( status );
		}
	}
}
