package com.x.okr.assemble.control.jaxrs.okrworkdynamics;
import java.util.List;

import com.x.base.core.gson.GsonPropertyObject;
import com.x.base.core.http.annotation.Wrap;
import com.x.okr.entity.OkrWorkDynamics;

@Wrap(OkrWorkDynamics.class)
public class WrapInFilter extends GsonPropertyObject {
	
	private String workId;
	
	private List<String> centerIds = null;
	
	private List<String> workIds = null;
	
	private String sequenceField = "sequence";
	
	private String userIdentity = null;
	
	private String key;
	
	private String order = "DESC";

	private boolean isOkrSystemAdmin = false;
	
	public String getWorkId() {
		return workId;
	}

	public void setWorkId(String workId) {
		this.workId = workId;
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

	public String getUserIdentity() {
		return userIdentity;
	}

	public void setUserIdentity(String userIdentity) {
		this.userIdentity = userIdentity;
	}

	public List<String> getCenterIds() {
		return centerIds;
	}

	public void setCenterIds(List<String> centerIds) {
		this.centerIds = centerIds;
	}

	public List<String> getWorkIds() {
		return workIds;
	}

	public void setWorkIds(List<String> workIds) {
		this.workIds = workIds;
	}

	public boolean isOkrSystemAdmin() {
		return isOkrSystemAdmin;
	}

	public void setOkrSystemAdmin(boolean isOkrSystemAdmin) {
		this.isOkrSystemAdmin = isOkrSystemAdmin;
	}
	
}
