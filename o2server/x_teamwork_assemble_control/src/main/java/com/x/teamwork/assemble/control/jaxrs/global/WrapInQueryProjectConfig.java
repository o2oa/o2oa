package com.x.teamwork.assemble.control.jaxrs.global;

import javax.persistence.Column;

import org.apache.commons.lang3.StringUtils;
import org.apache.openjpa.persistence.jdbc.Index;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.teamwork.core.entity.tools.filter.QueryFilter;
import com.x.teamwork.core.entity.tools.filter.term.EqualsTerm;
import com.x.teamwork.core.entity.tools.filter.term.LikeTerm;

public class WrapInQueryProjectConfig {
	@FieldDescribe("用于排列的属性，非必填，默认为createTime.")
	private String orderField = "createTime";

	@FieldDescribe("排序方式：DESC | ASC，非必填， 默认为DESC.")
	private String orderType = "DESC";
	
	@FieldDescribe("项目ID.")
	private String project=null;
	
	@FieldDescribe("新建任务：true|false.")
	private String taskCreate = null;
	
	@FieldDescribe("复制任务：true|false.")
	private String taskCopy = null;
	
	@FieldDescribe("删除任务：true|false.")
	private String taskRemove = null;
	
	@FieldDescribe("新建泳道：true|false.")
	private String laneCreate = null;
	
	@FieldDescribe("编辑泳道：true|false.")
	private String laneEdit = null;
	
	@FieldDescribe("删除泳道：true|false.")
	private String laneRemove = null;
	
	@FieldDescribe("上传附件：true|false.")
	private String attachmentUpload = null;
	
	@FieldDescribe("允许评论：true|false.")
	private String comment = null;
	
	private Long rank = 0L;
	
	public String getProject() {
		return project;
	}

	public void setProject(String project) {
		this.project = project;
	}
	
	public String getTaskCreate() {
		return taskCreate;
	}

	public void setTaskCreate(String taskCreate) {
		this.taskCreate = taskCreate;
	}
	
	public String getTaskCopy() {
		return taskCopy;
	}

	public void setTaskCopy(String taskCopy) {
		this.taskCopy = taskCopy;
	}
	
	public String getTaskRemove() {
		return taskRemove;
	}

	public void setTaskRemove(String taskRemove) {
		this.taskRemove = taskRemove;
	}
	
	public String getLaneCreate() {
		return laneCreate;
	}

	public void setLaneCreate(String laneCreate) {
		this.laneCreate = laneCreate;
	}
	
	public String getLaneEdit() {
		return laneEdit;
	}

	public void setLaneEdit(String laneEdit) {
		this.laneEdit = laneEdit;
	}
	
	public String getLaneRemove() {
		return laneRemove;
	}

	public void setLaneRemove(String laneRemove) {
		this.laneRemove = laneRemove;
	}

	public String getAttachmentUpload() {
		return attachmentUpload;
	}

	public void setAttachmentUpload(String attachmentUpload) {
		this.attachmentUpload = attachmentUpload;
	}
	
	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
	
	public String getOrderField() {
		return orderField;
	}
	public void setOrderField(String orderField) {
		this.orderField = orderField;
	}
	public String getOrderType() {
		return orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}
	
	public Long getRank() {
		return rank;
	}
	public void setRank(Long rank) {
		this.rank = rank;
	}



	/**
	 * 根据传入的查询参数，组织一个完整的QueryFilter对象
	 * @return
	 */
	public QueryFilter getQueryFilter() {
		QueryFilter queryFilter = new QueryFilter();		
		queryFilter.setJoinType( "and" );
		//组织查询条件对象
		if( StringUtils.isNotEmpty( this.getProject())) {
			queryFilter.addEqualsTerm( new EqualsTerm( "project", this.getProject() ) );
		}
		
		if( StringUtils.isNotEmpty( this.getTaskCreate() )) {
			if("true".equalsIgnoreCase( this.getTaskCreate() )) {
				queryFilter.addEqualsTerm( new EqualsTerm( "taskCreate", true ) );
			}else{
				queryFilter.addEqualsTerm( new EqualsTerm( "taskCreate", false ) );
			}
		}
		if( StringUtils.isNotEmpty( this.getTaskCopy() )) {
			if("true".equalsIgnoreCase( this.getTaskCopy() )) {
				queryFilter.addEqualsTerm( new EqualsTerm( "taskCopy", true ) );
			}else{
				queryFilter.addEqualsTerm( new EqualsTerm( "taskCopy", false ) );
			}
		}
		
		if( StringUtils.isNotEmpty( this.getTaskRemove() )) {
			if("true".equalsIgnoreCase( this.getTaskRemove() )) {
				queryFilter.addEqualsTerm( new EqualsTerm( "taskRemove", true ) );
			}else{
				queryFilter.addEqualsTerm( new EqualsTerm( "taskRemove", false ) );
			}
		}
		
		if( StringUtils.isNotEmpty( this.getLaneCreate() )) {
			if("true".equalsIgnoreCase( this.getLaneCreate() )) {
				queryFilter.addEqualsTerm( new EqualsTerm( "laneCreate", true ) );
			}else{
				queryFilter.addEqualsTerm( new EqualsTerm( "laneCreate", false ) );
			}
		}
		
		if( StringUtils.isNotEmpty( this.getLaneEdit() )) {
			if("true".equalsIgnoreCase( this.getLaneEdit() )) {
				queryFilter.addEqualsTerm( new EqualsTerm( "laneEdit", true ) );
			}else{
				queryFilter.addEqualsTerm( new EqualsTerm( "laneEdit", false ) );
			}
		}
		
		if( StringUtils.isNotEmpty( this.getLaneRemove() )) {
			if("true".equalsIgnoreCase( this.getLaneRemove() )) {
				queryFilter.addEqualsTerm( new EqualsTerm( "laneRemove", true ) );
			}else{
				queryFilter.addEqualsTerm( new EqualsTerm( "laneRemove", false ) );
			}
		}
		
		if( StringUtils.isNotEmpty( this.getAttachmentUpload() )) {
			if("true".equalsIgnoreCase( this.getAttachmentUpload() )) {
				queryFilter.addEqualsTerm( new EqualsTerm( "attachmentUpload", true ) );
			}else{
				queryFilter.addEqualsTerm( new EqualsTerm( "attachmentUpload", false ) );
			}
		}
		
		if( StringUtils.isNotEmpty( this.getComment() )) {
			if("true".equalsIgnoreCase( this.getComment() )) {
				queryFilter.addEqualsTerm( new EqualsTerm( "comment", true ) );
			}else{
				queryFilter.addEqualsTerm( new EqualsTerm( "comment", false ) );
			}
		}
		
		return queryFilter;
	}
}
