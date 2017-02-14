package com.x.okr.assemble.control.jaxrs.okrworkreportpersonlink;
import java.util.ArrayList;
import java.util.List;

import com.x.base.core.gson.GsonPropertyObject;
import com.x.base.core.http.annotation.Wrap;
import com.x.okr.entity.OkrWorkReportPersonLink;
@Wrap(OkrWorkReportPersonLink.class)
public class WrapInFilter extends GsonPropertyObject {
private String query_deployerOrganizationName;
	
	private String query_deployerName;

	private String query_deployerCompanyName;
	
	private String title ;
	
	private String processIdentity;
	
	private List<String> workIds;
	
	private List<String> processStatusList;
	
	private List<String> deployerNames;
	
	private List<String> deployerOrganizationNames;
	
	private List<String> deployerCompanyNames;
	
	private String query_creatorName;
	
	private String query_creatorOrganizationName;
	
	private String query_creatorCompanyName;
	
	private List<String> creatorNames;
	
	private List<String> creatorOrganizationNames;
	
	private List<String> creatorCompanyNames;
	
	private String sequenceField = "sequence";
	
	private String key;
	
	private String order = "DESC";
	
	private List<String> q_statuses;

	public String getQuery_deployerName() {
		return query_deployerName;
	}

	public void setQuery_deployerName(String query_deployerName) {
		this.query_deployerName = query_deployerName;
	}

	public String getQuery_deployerOrganizationName() {
		return query_deployerOrganizationName;
	}

	public void setQuery_deployerOrganizationName(String query_deployerOrganizationName) {
		this.query_deployerOrganizationName = query_deployerOrganizationName;
	}

	public String getQuery_deployerCompanyName() {
		return query_deployerCompanyName;
	}

	public void setQuery_deployerCompanyName(String query_deployerCompanyName) {
		this.query_deployerCompanyName = query_deployerCompanyName;
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

	public List<String> getDeployerOrganizationNames() {
		return deployerOrganizationNames;
	}

	public void setDeployerOrganizationNames(List<String> deployerOrganizationNames) {
		this.deployerOrganizationNames = deployerOrganizationNames;
	}

	public List<String> getDeployerCompanyNames() {
		return deployerCompanyNames;
	}

	public void setDeployerCompanyNames(List<String> deployerCompanyNames) {
		this.deployerCompanyNames = deployerCompanyNames;
	}

	public String getQuery_creatorName() {
		return query_creatorName;
	}

	public void setQuery_creatorName(String query_creatorName) {
		this.query_creatorName = query_creatorName;
	}

	public String getQuery_creatorOrganizationName() {
		return query_creatorOrganizationName;
	}

	public void setQuery_creatorOrganizationName(String query_creatorOrganizationName) {
		this.query_creatorOrganizationName = query_creatorOrganizationName;
	}

	public String getQuery_creatorCompanyName() {
		return query_creatorCompanyName;
	}

	public void setQuery_creatorCompanyName(String query_creatorCompanyName) {
		this.query_creatorCompanyName = query_creatorCompanyName;
	}

	public List<String> getCreatorNames() {
		return creatorNames;
	}

	public void setCreatorNames(List<String> creatorNames) {
		this.creatorNames = creatorNames;
	}

	public List<String> getCreatorOrganizationNames() {
		return creatorOrganizationNames;
	}

	public void setCreatorOrganizationNames(List<String> creatorOrganizationNames) {
		this.creatorOrganizationNames = creatorOrganizationNames;
	}

	public List<String> getCreatorCompanyNames() {
		return creatorCompanyNames;
	}

	public void setCreatorCompanyNames(List<String> creatorCompanyNames) {
		this.creatorCompanyNames = creatorCompanyNames;
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
