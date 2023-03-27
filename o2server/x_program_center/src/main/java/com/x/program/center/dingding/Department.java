package com.x.program.center.dingding;

import com.x.base.core.project.gson.GsonPropertyObject;

public class Department extends GsonPropertyObject {
	//
	// "id": 2,
	// "name": "广州研发中心",
	// "parentid": 1,
	// "order": 10
	// "isFromUnionOrg": false

	private Long id;
	private String name;
	private Long parentid;
	private Long order;
	private Boolean isFromUnionOrg;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getParentid() {
		return parentid;
	}

	public void setParentid(Long parentid) {
		this.parentid = parentid;
	}

	public Long getOrder() {
		return order;
	}

	public void setOrder(Long order) {
		this.order = order;
	}

	public Boolean getIsFromUnionOrg() {
		return isFromUnionOrg;
	}

	public void setIsFromUnionOrg(Boolean isFromUnionOrg) {
		this.isFromUnionOrg = isFromUnionOrg;
	}
}
