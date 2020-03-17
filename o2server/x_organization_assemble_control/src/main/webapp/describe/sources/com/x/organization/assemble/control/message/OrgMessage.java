package com.x.organization.assemble.control.message;

public class OrgMessage {
	
	public  String operType;
	public  String orgType;
	public  String operUerId;
	public  String operDataId;
	public  String receiveSystem;
	public  boolean consumed;
	public  String consumedModule;
	public  String body;
	
	public String getOperType() {
		return operType;
	}
	public void setOperType(String operType) {
		this.operType = operType;
	}
	public String getOrgType() {
		return orgType;
	}
	public void setOrgType(String orgType) {
		this.orgType = orgType;
	}
	public String getOperUerId() {
		return operUerId;
	}
	public void setOperUerId(String operUerId) {
		this.operUerId = operUerId;
	}
	public String getOperDataId() {
		return operDataId;
	}
	public void setOperDataId(String operDataId) {
		this.operDataId = operDataId;
	}
	public String getReceiveSystem() {
		return receiveSystem;
	}
	public void setReceiveSystem(String receiveSystem) {
		this.receiveSystem = receiveSystem;
	}
	public boolean getConsumed() {
		return consumed;
	}
	public void setConsumed(boolean consumed) {
		this.consumed = consumed;
	}
	public String getConsumedModule() {
		return consumedModule;
	}
	public void setConsumedModule(String consumedModule) {
		this.consumedModule = consumedModule;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	
	
}
