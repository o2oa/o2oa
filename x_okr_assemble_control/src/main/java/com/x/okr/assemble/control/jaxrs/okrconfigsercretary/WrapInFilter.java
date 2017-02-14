package com.x.okr.assemble.control.jaxrs.okrconfigsercretary;

import com.x.base.core.gson.GsonPropertyObject;
import com.x.base.core.http.annotation.Wrap;
import com.x.okr.entity.OkrConfigSecretary;

@Wrap(OkrConfigSecretary.class)
public class WrapInFilter extends GsonPropertyObject {
	
	private String secretaryName = null;
	
	private String leaderName = null;
	
	private String sequenceField = "sequence";
	
	private String key;
	
	private String order = "DESC";
	
	private Integer count;
	
	private boolean andJoin;
	
	public Integer getCount() {
		return count;
	}
	public void setCount(Integer count) {
		this.count = count;
	}
	public boolean isAndJoin() {
		return andJoin;
	}
	public void setAndJoin(boolean andJoin) {
		this.andJoin = andJoin;
	}
	public String getSecretaryName() {
		return secretaryName;
	}

	public void setSecretaryName(String secretaryName) {
		this.secretaryName = secretaryName;
	}

	public String getLeaderName() {
		return leaderName;
	}

	public void setLeaderName(String leaderName) {
		this.leaderName = leaderName;
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
	
	
}
