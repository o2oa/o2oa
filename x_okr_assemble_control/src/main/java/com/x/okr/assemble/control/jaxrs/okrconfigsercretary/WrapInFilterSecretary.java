package com.x.okr.assemble.control.jaxrs.okrconfigsercretary;

import com.x.base.core.entity.annotation.EntityFieldDescribe;
import com.x.base.core.gson.GsonPropertyObject;
import com.x.base.core.http.annotation.Wrap;

@Wrap( WrapInFilterSecretary.class )
public class WrapInFilterSecretary extends GsonPropertyObject {
	
	@EntityFieldDescribe( "用于查询的秘书姓名." )
	private String secretaryName = null;
	
	@EntityFieldDescribe( "用于查询的领导姓名." )
	private String leaderName = null;
	
	@EntityFieldDescribe( "用于列表排序的属性." )
	private String sequenceField = "sequence";
	
	@EntityFieldDescribe( "用于列表排序的方式." )
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
	
	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}
	
	
}
