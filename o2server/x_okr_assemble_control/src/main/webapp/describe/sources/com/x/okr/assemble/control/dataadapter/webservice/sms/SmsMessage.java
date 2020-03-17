package com.x.okr.assemble.control.dataadapter.webservice.sms;

public class SmsMessage {	
	String UNID = null;
	String CONTENT = null;
	
	public SmsMessage(String uNID, String cONTENT) {
		super();
		UNID = uNID;
		CONTENT = cONTENT;
	}
	public String getUNID() {
		return UNID;
	}
	public String getCONTENT() {
		return CONTENT;
	}
	public void setUNID(String uNID) {
		UNID = uNID;
	}
	public void setCONTENT(String cONTENT) {
		CONTENT = cONTENT;
	}
	
}
