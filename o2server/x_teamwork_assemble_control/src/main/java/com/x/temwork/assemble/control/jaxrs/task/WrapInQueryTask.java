package com.x.temwork.assemble.control.jaxrs.task;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.teamwork.core.entity.tools.filter.QueryFilter;
import com.x.teamwork.core.entity.tools.filter.term.EqualsTerm;
import com.x.teamwork.core.entity.tools.filter.term.LikeTerm;

public class WrapInQueryTask {
	@FieldDescribe("用于排列的属性.")
	private String orderField = "createTime";

	@FieldDescribe("排序方式：DESC | ASC.")
	private String orderType = "DESC";
	
	@FieldDescribe("用于搜索的标题.")
	private String title = null;
	
	@FieldDescribe("用于搜索的项目ID.")
	private String projectId = null;
	
	@FieldDescribe("用于搜索的上级工作任务ID.")
	private String parentId = null;
	
	@FieldDescribe("用于搜索的工作标签：自定义标签.")
	private String tag = null;
	
	@FieldDescribe("工作等级：普通、紧急、特急")
	private String priority = null;		
	
	@FieldDescribe("用于搜索的工作状态：草稿、未开始、执行中、已完成、已挂起、已取消")
	private String workStatus = null;
	
	@FieldDescribe("是否已完成")
	private String completed = null;		

	@FieldDescribe("是否已超时")
	private String overtime = null;		
	
	@FieldDescribe("是否已经删除")
	private String deleted = null;		
	
	@FieldDescribe("执行者和负责人")
	private String executor = null;		

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

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Long getRank() {
		return rank;
	}

	public void setRank(Long rank) {
		this.rank = rank;
	}

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public String getWorkStatus() {
		return workStatus;
	}

	public void setWorkStatus(String workStatus) {
		this.workStatus = workStatus;
	}

	public String getCompleted() {
		return completed;
	}

	public void setCompleted(String completed) {
		this.completed = completed;
	}

	public String getOvertime() {
		return overtime;
	}

	public void setOvertime(String overtime) {
		this.overtime = overtime;
	}

	public String getDeleted() {
		return deleted;
	}

	public void setDeleted(String deleted) {
		this.deleted = deleted;
	}

	public String getExecutor() {
		return executor;
	}

	public void setExecutor(String executor) {
		this.executor = executor;
	}
	
	/**
	 * 根据传入的查询参数，组织一个完整的QueryFilter对象
	 * @return
	 */
	public QueryFilter getQueryFilter() {
		QueryFilter queryFilter = new QueryFilter();
		//组织查询条件对象
		if( StringUtils.isNotEmpty( this.getTitle() )) {
			queryFilter.addLikeTerm( new LikeTerm( "title", this.getTitle() ) );
		}
		if( StringUtils.isNotEmpty( this.getProjectId() )) {
			queryFilter.addEqualsTerm( new EqualsTerm( "projectId", this.getProjectId() ) );
		}
		if( StringUtils.isNotEmpty( this.getParentId() )) {
			queryFilter.addEqualsTerm( new EqualsTerm( "parentId", this.getParentId() ) );
		}
		if( StringUtils.isNotEmpty( this.getTag() )) {
			queryFilter.addEqualsTerm( new EqualsTerm( "tag", this.getTag() ) );
		}
		if( StringUtils.isNotEmpty( this.getPriority())) {
			queryFilter.addEqualsTerm( new EqualsTerm( "priority", this.getPriority() ) );
		}
		if( StringUtils.isNotEmpty( this.getWorkStatus() )) {
			queryFilter.addEqualsTerm( new EqualsTerm( "workStatus", this.getWorkStatus() ) );
		}
		if( StringUtils.isNotEmpty( this.getExecutor())) {
			queryFilter.addEqualsTerm( new EqualsTerm( "executor", this.getExecutor() ) );
		}
		if( StringUtils.isNotEmpty( this.getCompleted() )) {
			if( "true".equalsIgnoreCase( this.getCompleted() )) {
				queryFilter.addEqualsTerm( new EqualsTerm( "completed", true ) );
			}else {
				queryFilter.addEqualsTerm( new EqualsTerm( "completed", false ) );
			}
		}
		if( StringUtils.isNotEmpty( this.getDeleted() )) {
			if( "true".equalsIgnoreCase( this.getDeleted() )) {
				queryFilter.addEqualsTerm( new EqualsTerm( "deleted", true ) );
			}else {
				queryFilter.addEqualsTerm( new EqualsTerm( "deleted", false ) );
			}
		}
		if( StringUtils.isNotEmpty( this.getOvertime() )) {
			if( "true".equalsIgnoreCase( this.getDeleted() )) {
				queryFilter.addEqualsTerm( new EqualsTerm( "overtime", true ) );
			}else {
				queryFilter.addEqualsTerm( new EqualsTerm( "overtime", false ) );
			}
		}
		return queryFilter;
	}
}
