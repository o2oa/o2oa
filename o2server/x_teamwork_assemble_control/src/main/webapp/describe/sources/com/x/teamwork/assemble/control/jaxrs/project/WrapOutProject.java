package com.x.teamwork.assemble.control.jaxrs.project;

import java.util.List;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.teamwork.core.entity.Project;
import com.x.teamwork.core.entity.ProjectGroup;

public class WrapOutProject extends Project{
	
	private static final long serialVersionUID = 1L;

	@FieldDescribe("是否标星")
	private Boolean star = false;
	
	@FieldDescribe("项目控件权限")
	private WrapOutControl control = null;	
	
	@FieldDescribe("说明信息(1M)")
	private String description;
	
	@FieldDescribe("项目组列表")
	private List<ProjectGroup> groups = null;
	
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<ProjectGroup> getGroups() {
		return groups;
	}

	public void setGroups(List<ProjectGroup> groups) {
		this.groups = groups;
	}

	public WrapOutControl getControl() {
		return control;
	}

	public void setControl(WrapOutControl control) {
		this.control = control;
	}
}
