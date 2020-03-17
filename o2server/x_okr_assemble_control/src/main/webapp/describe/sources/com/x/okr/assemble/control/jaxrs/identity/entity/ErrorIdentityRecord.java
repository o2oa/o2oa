package com.x.okr.assemble.control.jaxrs.identity.entity;

import java.util.Date;

public class ErrorIdentityRecord {
	private String id = null;
	private String identity = null;
	private String tableName = null;
	private String title = null;
	private Date createTime = null;	
	
	public ErrorIdentityRecord() {
		super();
	}

	public ErrorIdentityRecord(String id, String identity, String tableName, String title, Date createTime) {
		super();
		this.id = id;
		this.identity = identity;
		this.tableName = tableName;
		this.title = title;
		this.createTime = createTime;
	}
	
	public String getId() {
		return id;
	}
	public String getIdentity() {
		return identity;
	}
	public String getTitle() {
		return title;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setId(String id) {
		this.id = id;
	}
	public void setIdentity(String identity) {
		this.identity = identity;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	
}
