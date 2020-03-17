package com.x.teamwork.assemble.control.jaxrs.project;

import com.x.base.core.project.annotation.FieldDescribe;

public class WrapOutControl {

	@FieldDescribe("是可以管理")
	private Boolean manageAble = false;
	
	@FieldDescribe("是可以编辑")
	private Boolean editAble = false;

	public Boolean getManageAble() {
		return manageAble;
	}

	public void setManageAble(Boolean manageAble) {
		this.manageAble = manageAble;
	}

	public Boolean getEditAble() {
		return editAble;
	}

	public void setEditAble(Boolean editAble) {
		this.editAble = editAble;
	}
	
	
}
