package com.x.teamwork.assemble.control.jaxrs.task;

import com.x.base.core.project.annotation.FieldDescribe;

public class WrapOutControl {
	
	@FieldDescribe("是否可删除")
	private Boolean delete = false;
	
	@FieldDescribe("是否可编辑")
	private Boolean edit = false;
	
	@FieldDescribe("是否可排序")
	private Boolean sortable = true;
	
	@FieldDescribe("是否可变更负责人")
	private Boolean changeExecutor = false;
	
	@FieldDescribe("是否创始人")
	private Boolean founder = false;
	
	public Boolean getDelete() {
		return delete;
	}

	public void setDelete(Boolean delete) {
		this.delete = delete;
	}
	
	public Boolean getEdit() {
		return edit;
	}

	public void setEdit(Boolean edit) {
		this.edit = edit;
	}
	
	public Boolean getSortable() {
		return sortable;
	}

	public void setSortable(Boolean sortable) {
		this.sortable = sortable;
	}
	
	
	public Boolean getChangeExecutor() {
		return changeExecutor;
	}

	public void setChangeExecutor(Boolean changeExecutor) {
		this.changeExecutor = changeExecutor;
	}
	
	public Boolean getFounder() {
		return founder;
	}

	public void setFounder(Boolean founder) {
		this.founder = founder;
	}
	
}
