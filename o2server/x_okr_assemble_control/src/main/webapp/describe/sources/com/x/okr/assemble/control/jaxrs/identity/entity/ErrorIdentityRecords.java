package com.x.okr.assemble.control.jaxrs.identity.entity;

import java.util.List;

public class ErrorIdentityRecords {
	private String identity = null;
	private String recordType = "未知类别";
	private List<ErrorIdentityRecord> errorRecords = null;
	
	public String getIdentity() {
		return identity;
	}
	public String getRecordType() {
		return recordType;
	}
	public List<ErrorIdentityRecord> getErrorRecords() {
		return errorRecords;
	}
	public void setIdentity(String identity) {
		this.identity = identity;
	}
	public void setRecordType(String recordType) {
		this.recordType = recordType;
	}
	public void setErrorRecords(List<ErrorIdentityRecord> errorRecords) {
		this.errorRecords = errorRecords;
	}
}
