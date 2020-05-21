package com.x.teamwork.assemble.control.jaxrs.project;

import com.x.base.core.project.annotation.FieldDescribe;

public class WrapOutControl {
	
	@FieldDescribe("是否可删除")
	private Boolean delete = false;
	
	@FieldDescribe("是否可编辑")
	private Boolean edit = false;
	
	@FieldDescribe("是否可排序")
	private Boolean sortable = true;
	
	@FieldDescribe("是否创始人")
	private Boolean founder = false;
	
	
	@FieldDescribe("是否可新建任务")
	private Boolean taskCreate = true;
	
	@FieldDescribe("是否可复制任务")
	private Boolean taskCopy = true;
	
	@FieldDescribe("是否可删除任务")
	private Boolean taskRemove = true;
	
	@FieldDescribe("是否可新建泳道")
	private Boolean laneCreate = true;
	
	@FieldDescribe("是否可编辑泳道")
	private Boolean laneEdit = true;
	
	@FieldDescribe("是否可删除泳道")
	private Boolean laneRemove = true;
	
	@FieldDescribe("是否上传附件")
	private Boolean attachmentUpload = true;
	
	@FieldDescribe("是否允许评论")
	private Boolean comment = true;
	
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
	
	public Boolean getFounder() {
		return founder;
	}

	public void setFounder(Boolean founder) {
		this.founder = founder;
	}
	
	public Boolean getTaskCreate() {
		return taskCreate;
	}

	public void setTaskCreate(Boolean taskCreate) {
		this.taskCreate = taskCreate;
	}
	
	public Boolean getTaskCopy() {
		return taskCopy;
	}

	public void setTaskCopy(Boolean taskCopy) {
		this.taskCopy = taskCopy;
	}
	
	public Boolean getTaskRemove() {
		return taskRemove;
	}

	public void setTaskRemove(Boolean taskRemove) {
		this.taskRemove = taskRemove;
	}
	
	public Boolean getLaneCreate() {
		return laneCreate;
	}

	public void setLaneCreate(Boolean laneCreate) {
		this.laneCreate = laneCreate;
	}
	
	public Boolean getLaneEdit() {
		return laneEdit;
	}

	public void setLaneEdit(Boolean laneEdit) {
		this.laneEdit = laneEdit;
	}
	
	public Boolean getLaneRemove() {
		return laneRemove;
	}

	public void setLaneRemove(Boolean laneRemove) {
		this.laneRemove = laneRemove;
	}
	
	public Boolean getAttachmentUpload() {
		return attachmentUpload;
	}

	public void setAttachmentUpload(Boolean attachmentUpload) {
		this.attachmentUpload = attachmentUpload;
	}
	
	public Boolean getComment() {
		return comment;
	}

	public void setComment(Boolean comment) {
		this.comment = comment;
	}
}
