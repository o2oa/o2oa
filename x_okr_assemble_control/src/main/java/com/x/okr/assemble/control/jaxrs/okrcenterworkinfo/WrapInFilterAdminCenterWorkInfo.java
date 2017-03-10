package com.x.okr.assemble.control.jaxrs.okrcenterworkinfo;

import com.x.base.core.entity.annotation.EntityFieldDescribe;
import com.x.base.core.gson.GsonPropertyObject;
import com.x.base.core.http.annotation.Wrap;

@Wrap(WrapInFilterAdminCenterWorkInfo.class)
public class WrapInFilterAdminCenterWorkInfo extends GsonPropertyObject {

	@EntityFieldDescribe( "用于模糊查询的字符串." )
	private String filterLikeContent = null;
	
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
	public String getFilterLikeContent() {
		return filterLikeContent;
	}
	public void setFilterLikeContent(String filterLikeContent) {
		this.filterLikeContent = filterLikeContent;
	}
}
