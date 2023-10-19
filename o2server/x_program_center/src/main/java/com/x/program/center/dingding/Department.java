package com.x.program.center.dingding;

import com.x.base.core.project.gson.GsonPropertyObject;

public class Department extends GsonPropertyObject {

	private static final long serialVersionUID = 2443231508595734358L;
	private Long dept_id;
	private String name;
	private Long parent_id;
	private Long order;
	private Boolean from_union_org; // 部门是否来自关联组织
	public Long getDept_id() {
		return dept_id;
	}
	public void setDept_id(Long dept_id) {
		this.dept_id = dept_id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Long getParent_id() {
		return parent_id;
	}
	public void setParent_id(Long parent_id) {
		this.parent_id = parent_id;
	}
	public Long getOrder() {
		return order;
	}
	public void setOrder(Long order) {
		this.order = order;
	}
	public Boolean getFrom_union_org() {
		return from_union_org;
	}
	public void setFrom_union_org(Boolean from_union_org) {
		this.from_union_org = from_union_org;
	}
 
	
	
}
