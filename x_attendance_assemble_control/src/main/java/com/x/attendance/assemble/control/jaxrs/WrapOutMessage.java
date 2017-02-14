package com.x.attendance.assemble.control.jaxrs;

import java.io.Serializable;
import java.util.List;

import com.x.base.core.gson.GsonPropertyObject;

public class WrapOutMessage extends GsonPropertyObject implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private String message;
	private String status;
	private String exceptionMessage;
	private List<DateRecord> dateRecordList;
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getExceptionMessage() {
		return exceptionMessage;
	}
	public void setExceptionMessage(String exceptionMessage) {
		this.exceptionMessage = exceptionMessage;
	}
	public List<DateRecord> getDateRecordList() {
		return dateRecordList;
	}
	public void setDateRecordList(List<DateRecord> dateRecordList) {
		this.dateRecordList = dateRecordList;
	}	
}
