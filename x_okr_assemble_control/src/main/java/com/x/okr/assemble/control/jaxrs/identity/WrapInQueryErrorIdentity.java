package com.x.okr.assemble.control.jaxrs.identity;

import com.x.base.core.entity.annotation.EntityFieldDescribe;
import com.x.base.core.http.annotation.Wrap;

@Wrap( WrapInQueryErrorIdentity.class)
public class WrapInQueryErrorIdentity {
	
	@EntityFieldDescribe( "用于查询身份名称." )
	private String identity  = null;
	
	@EntityFieldDescribe( "用于列表排序的属性." )
	private String sequenceField = "sequence";
	
	@EntityFieldDescribe( "用于列表排序的方式." )
	private String order = "DESC";
	
	private Long rank = 0L;

	public Long getRank() {
		return rank;
	}
	public void setRank(Long rank) {
		this.rank = rank;
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
	public String getIdentity() {
		return identity;
	}
	public void setIdentity(String identity) {
		this.identity = identity;
	}
	
}