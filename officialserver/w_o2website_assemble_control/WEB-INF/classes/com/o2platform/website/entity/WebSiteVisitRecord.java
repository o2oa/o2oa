package com.o2platform.website.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

import com.o2platform.common.date.DateOperation;


public class WebSiteVisitRecord implements Serializable {

	private static final long serialVersionUID = 3856138316794473794L;
	
	public WebSiteVisitRecord() {
		Date date = new Date();
		if ( null == this.createTime ) {
			this.createTime = date;
		}
		if ( null == this.operationYear ) {
			this.operationYear = DateOperation.getYear( date );
		}
		if ( null == this.operationMonth ) {
			this.operationMonth = DateOperation.getMonth( date );
		}
		if ( null == this.operationDay ) {
			this.operationDay = DateOperation.getDay( date );
		}
	}
	
	public static String createId() {
		return UUID.randomUUID().toString();
	}
	private String id = createId();
	private Date createTime;
	private String hostIp = null;
	private String operationYear = null;
	private String operationMonth = null;
	private String operationDay = null;
	private String xoperator = "";
	private String pageName = null;
	private String province = null;
	private String city = null;
	private String description = null;

	public String getHostIp() {
		return hostIp;
	}
	public void setHostIp(String hostIp) {
		this.hostIp = hostIp;
	}
	public String getOperationDay() {
		return operationDay;
	}
	public void setOperationDay(String operationDay) {
		this.operationDay = operationDay;
	}
	public String getXoperator() {
		return xoperator;
	}
	public void setXoperator(String xoperator) {
		this.xoperator = xoperator;
	}

	public String getPageName() {
		return pageName;
	}
	public void setPageName(String pageName) {
		this.pageName = pageName;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getOperationYear() {
		return operationYear;
	}
	public void setOperationYear(String operationYear) {
		this.operationYear = operationYear;
	}
	public String getOperationMonth() {
		return operationMonth;
	}
	public void setOperationMonth(String operationMonth) {
		this.operationMonth = operationMonth;
	}
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
}