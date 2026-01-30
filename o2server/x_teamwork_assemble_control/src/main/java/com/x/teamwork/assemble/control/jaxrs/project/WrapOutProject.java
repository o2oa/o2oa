package com.x.teamwork.assemble.control.jaxrs.project;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.teamwork.core.entity.Project;

public class WrapOutProject extends Project{

	private static final long serialVersionUID = 1L;

	@FieldDescribe("是否标星")
	private Boolean star = false;

	@FieldDescribe("项目控件权限")
	private WrapOutControl control = null;

	private Long rank;

	public Boolean getStar() {
		return star;
	}

	public void setStar(Boolean star) {
		this.star = star;
	}

	public Long getRank() {
		return rank;
	}

	public void setRank(Long rank) {
		this.rank = rank;
	}

	public WrapOutControl getControl() {
		return control;
	}

	public void setControl(WrapOutControl control) {
		this.control = control;
	}
}
