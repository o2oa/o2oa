package com.x.teamwork.assemble.control.jaxrs.task;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.teamwork.core.entity.tools.filter.QueryFilter;
import com.x.teamwork.core.entity.tools.filter.term.EqualsTerm;
import com.x.teamwork.core.entity.tools.filter.term.IsFalseTerm;
import com.x.teamwork.core.entity.tools.filter.term.LikeTerm;

public class WrapInQueryTask {
	
	@FieldDescribe("用于排列的属性，非必填，默认为createTime.")
	private String orderField = "createTime";

	@FieldDescribe("排序方式：DESC | ASC，非必填，默认为DESC.")
	private String orderType = "DESC";
	
	@FieldDescribe("用于搜索的标题，非必填.")
	private String title = null;
	
	@FieldDescribe("用于搜索的项目ID，非必填.")
	private String project = null;
	
	@FieldDescribe("用于搜索的上级工作任务ID，非必填.")
	private String parentId = null;
	
	@FieldDescribe("用于搜索的工作标签：自定义标签，非必填.")
	private String tag = null;
	
	@FieldDescribe("工作等级：普通-normal | 紧急-urgent | 特急-extraurgent，非必填")
	private String priority = null;		
	
	@FieldDescribe("用于搜索的工作状态：草稿- draft  | 执行中- processing | 已完成- completed | 已归档- archived，非必填")
	private String workStatus = null;
	
	@FieldDescribe("是否已完成，非必填")
	private String completed = null;		

	@FieldDescribe("是否已超时，非必填")
	private String overtime = null;		
	
	@FieldDescribe("是否已经删除，非必填")
	private String deleted = null;		
	
	@FieldDescribe("执行者和负责人，非必填")
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

	public String getProject() {
		return project;
	}

	public void setProject(String project) {
		this.project = project;
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
		if( StringUtils.isNotEmpty( this.getProject() )) {
			queryFilter.addEqualsTerm( new EqualsTerm( "project", this.getProject() ) );
		}
		if( StringUtils.isNotEmpty( this.getParentId() )) {
			queryFilter.addEqualsTerm( new EqualsTerm( "parentId", this.getParentId() ) );
		}
		if( StringUtils.isNotEmpty( this.getTag() )) {
			queryFilter.addLikeTerm(  new LikeTerm( "tagContent", this.getTag() ) );
		}
		if( StringUtils.isNotEmpty( this.getPriority())) {
			queryFilter.addEqualsTerm( new EqualsTerm( "priority", this.getPriority() ) );
		}
		if( StringUtils.isNotEmpty( this.getWorkStatus() )) {
			queryFilter.addEqualsTerm( new EqualsTerm( "workStatus", this.getWorkStatus() ) );
		}else {
			//默认查询所有的未归档的工作任务
			queryFilter.addIsFalseTerm( new IsFalseTerm("archive"));
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
