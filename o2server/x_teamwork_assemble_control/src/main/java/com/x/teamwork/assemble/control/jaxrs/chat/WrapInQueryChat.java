package com.x.teamwork.assemble.control.jaxrs.chat;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.teamwork.core.entity.tools.filter.QueryFilter;
import com.x.teamwork.core.entity.tools.filter.term.EqualsTerm;
import com.x.teamwork.core.entity.tools.filter.term.LikeTerm;

public class WrapInQueryChat {
	
	@FieldDescribe("用于排列的属性，非必填，默认为createTime.")
	private String orderField = "createTime";

	@FieldDescribe("排序方式：DESC | ASC，非必填，默认为DESC.")
	private String orderType = "DESC";

	@FieldDescribe("所属工作ID，非必填，过滤条件")
	private String taskId = null;

	@FieldDescribe("工作标题，非必填，过滤条件")
	private String taskTitle = null;

	@FieldDescribe("发送者，非必填，过滤条件")
	private String sender = null;

	@FieldDescribe("目标者，非必填，过滤条件")
	private String target = null;

	private Long rank = 0L;

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
	
//	public String getProjectId() {
//		return projectId;
//	}
//
//	public void setProjectId(String projectId) {
//		this.projectId = projectId;
//	}
//
//	public String getProjectTitle() {
//		return projectTitle;
//	}
//
//	public void setProjectTitle(String projectTitle) {
//		this.projectTitle = projectTitle;
//	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getTaskTitle() {
		return taskTitle;
	}

	public void setTaskTitle(String taskTitle) {
		this.taskTitle = taskTitle;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
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
		//组织查询条件对象
//		if( StringUtils.isNotEmpty( this.getProjectTitle() )) {
//			queryFilter.addLikeTerm( new LikeTerm( "projectTitle", this.getProjectTitle() ) );
//		}
		if( StringUtils.isNotEmpty( this.getTaskTitle() )) {
			queryFilter.addLikeTerm( new LikeTerm( "taskTitle", this.getTaskTitle() ) );
		}
//		if( StringUtils.isNotEmpty( this.getProjectId() )) {
//			queryFilter.addEqualsTerm( new EqualsTerm( "projectId", this.getProjectId() ) );
//		}
		if( StringUtils.isNotEmpty( this.getTaskId() )) {
			queryFilter.addEqualsTerm( new EqualsTerm( "taskId", this.getTaskId() ) );
		}
		if( StringUtils.isNotEmpty( this.getSender())) {
			queryFilter.addEqualsTerm( new EqualsTerm( "sender", this.getSender() ) );
		}
		if( StringUtils.isNotEmpty( this.getTarget() )) {
			queryFilter.addEqualsTerm( new EqualsTerm( "target", this.getTarget() ) );
		}
		return queryFilter;
	}
}
