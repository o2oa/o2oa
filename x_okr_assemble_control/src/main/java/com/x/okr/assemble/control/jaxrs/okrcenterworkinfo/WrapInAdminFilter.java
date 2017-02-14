package com.x.okr.assemble.control.jaxrs.okrcenterworkinfo;

import com.x.base.core.gson.GsonPropertyObject;

public class WrapInAdminFilter extends GsonPropertyObject {

	private String filterLikeContent = null;
	private String sequenceField = "sequence";
	private String key;
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
	public String getFilterLikeContent() {
		return filterLikeContent;
	}
	public void setFilterLikeContent(String filterLikeContent) {
		this.filterLikeContent = filterLikeContent;
	}
}
