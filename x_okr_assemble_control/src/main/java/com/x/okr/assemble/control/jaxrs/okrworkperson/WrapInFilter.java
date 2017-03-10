package com.x.okr.assemble.control.jaxrs.okrworkperson;

import java.util.ArrayList;
import java.util.List;

import com.x.base.core.gson.GsonPropertyObject;
import com.x.base.core.http.annotation.Wrap;
import com.x.okr.entity.OkrWorkPerson;

@Wrap( OkrWorkPerson.class )
public class WrapInFilter extends GsonPropertyObject {
	
	private String sequenceField = "sequence";
	
	private String order = "DESC";
	
	private List<String> employeeNames = null;
	
	private List<String> employeeIdentities = null;
	
	private List<String> processIdentities = null;
	
	private List<String> organizationNames = null;
	
	private List<String> companyNames = null;
	
	private List<String> workTypes = null;
	
	private String workTitle = null;
	
	private String deployYear = null;
	
	private String deployMonth = null;
	
	private String workDateTimeType = null;
	
	private List<String> workProcessStatuses = null;
	
	private String infoType = "CenterWork"; // CenterWork | Work
	
	private List<String> infoStatuses = null;
	
	private Integer maxCharacterNumber = 30;

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

	public List<String> getEmployeeNames() {
		return employeeNames;
	}

	public void setEmployeeNames(List<String> employeeNames) {
		this.employeeNames = employeeNames;
	}

	public List<String> getProcessIdentities() {
		return processIdentities;
	}

	public void setProcessIdentities(List<String> processIdentities) {
		this.processIdentities = processIdentities;
	}

	public List<String> getOrganizationNames() {
		return organizationNames;
	}

	public void setOrganizationNames(List<String> organizationNames) {
		this.organizationNames = organizationNames;
	}

	public List<String> getCompanyNames() {
		return companyNames;
	}

	public void setCompanyNames(List<String> companyNames) {
		this.companyNames = companyNames;
	}

	public List<String> getWorkTypes() {
		return workTypes;
	}

	public void setWorkTypes(List<String> workTypes) {
		this.workTypes = workTypes;
	}

	public String getWorkTitle() {
		return workTitle;
	}

	public void setWorkTitle(String workTitle) {
		this.workTitle = workTitle;
	}

	public String getDeployYear() {
		return deployYear;
	}

	public void setDeployYear(String deployYear) {
		this.deployYear = deployYear;
	}

	public String getDeployMonth() {
		return deployMonth;
	}

	public void setDeployMonth(String deployMonth) {
		this.deployMonth = deployMonth;
	}

	public String getWorkDateTimeType() {
		return workDateTimeType;
	}

	public void setWorkDateTimeType(String workDateTimeType) {
		this.workDateTimeType = workDateTimeType;
	}

	public List<String> getWorkProcessStatuses() {
		return workProcessStatuses;
	}

	public void setWorkProcessStatuses(List<String> workProcessStatuses) {
		this.workProcessStatuses = workProcessStatuses;
	}

	public String getInfoType() {
		return infoType;
	}

	public void setInfoType(String infoType) {
		this.infoType = infoType;
	}
	
	
	public List<String> getInfoStatuses() {
		return infoStatuses;
	}

	public void setInfoStatuses(List<String> infoStatuses) {
		this.infoStatuses = infoStatuses;
	}

	public List<String> getEmployeeIdentities() {
		return employeeIdentities;
	}

	public void setEmployeeIdentities(List<String> employeeIdentities) {
		this.employeeIdentities = employeeIdentities;
	}

	/**
	 * 添加查询的信息状态
	 * @param status
	 */
	public void addQueryInfoStatus( String status ) {
		if( infoStatuses == null ){
			infoStatuses = new ArrayList<String>();
		}
		if( !infoStatuses.contains( status )){
			infoStatuses.add( status );
		}
	}
	
	/**
	 * 添加查询的员工姓名
	 * @param status
	 */
	public void addQueryEmployeeName( String employeeName ) {
		if( employeeNames == null ){
			employeeNames = new ArrayList<String>();
		}
		if( !employeeNames.contains( employeeName )){
			employeeNames.add( employeeName );
		}
	}
	
	/**
	 * 添加查询的员工姓名
	 * @param status
	 */
	public void addQueryEmployeeIdentities( String employeeIdentity ) {
		if( employeeIdentities == null ){
			employeeIdentities = new ArrayList<String>();
		}
		if( !employeeIdentities.contains( employeeIdentity )){
			employeeIdentities.add( employeeIdentity );
		}
	}
	
	/**
	 * 添加查询的部门名称
	 * @param status
	 */
	public void addQueryOrganizationName( String organizationName ) {
		if( organizationNames == null ){
			organizationNames = new ArrayList<String>();
		}
		if( !organizationNames.contains( organizationName )){
			organizationNames.add( organizationName );
		}
	}
	
	/**
	 * 添加查询的部门名称
	 * @param status
	 */
	public void addQueryCompanyName( String companyName ) {
		if( companyNames == null ){
			companyNames = new ArrayList<String>();
		}
		if( !companyNames.contains( companyName )){
			companyNames.add( companyName );
		}
	}
	
	/**
	 * 添加查询的部署处理状态
	 * @param status
	 */
	public void addQueryWorkProcessStatus( String status ) {
		if( workProcessStatuses == null ){
			workProcessStatuses = new ArrayList<String>();
		}
		if( !workProcessStatuses.contains( status )){
			workProcessStatuses.add( status );
		}
	}
	
	/**
	 * 添加查询的部署处理身份
	 * @param status
	 */
	public void addQueryProcessIdentity( String identity ) {
		if( processIdentities == null ){
			processIdentities = new ArrayList<String>();
		}
		if( !processIdentities.contains( identity )){
			processIdentities.add(identity);
		}
	}

	public Integer getMaxCharacterNumber() {
		return maxCharacterNumber;
	}

	public void setMaxCharacterNumber(Integer maxCharacterNumber) {
		this.maxCharacterNumber = maxCharacterNumber;
	}	
}
