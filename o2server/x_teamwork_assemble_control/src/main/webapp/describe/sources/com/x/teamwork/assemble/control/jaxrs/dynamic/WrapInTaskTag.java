package com.x.teamwork.assemble.control.jaxrs.dynamic;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.teamwork.core.entity.tools.filter.QueryFilter;
import com.x.teamwork.core.entity.tools.filter.term.EqualsTerm;

public class WrapInTaskTag {
	
	@FieldDescribe("用于排列的属性，非必填，默认为createTime.")
	private String orderField = "createTime";

	@FieldDescribe("排序方式：DESC | ASC，非必填，默认为DESC.")
	private String orderType = "DESC";
	
	@FieldDescribe("用于搜索的项目ID，<font style='color:red'>必填</font>")
	private String projectId = null;
	
	@FieldDescribe("用于搜索的工作任务ID，非必填.")
	private String taskId = null;
	
	@FieldDescribe("用于搜索的对象类型：PROJECT、TASK、TASKGROUP、TASKLIST、TASKVIEW、CHAT等，非必填.")
	private String objectType = null;
	
	@FieldDescribe("用于搜索的项目、工作或者工作组、列表、视图等对象的ID，非必填.")
	private String bundle = null;
	
	@FieldDescribe("用于搜索的操作类别，非必填.")
	private String optType = null;
	
	private Long rank = 0L;

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getObjectType() {
		return objectType;
	}

	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}

	public String getBundle() {
		return bundle;
	}

	public void setBundle(String bundle) {
		this.bundle = bundle;
	}

	public String getOptType() {
		return optType;
	}

	public void setOptType(String optType) {
		this.optType = optType;
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
		//组织查询条件对象
		if( StringUtils.isNotEmpty( this.getProjectId() )) {
			queryFilter.addEqualsTerm( new EqualsTerm( "projectId", this.getProjectId() ) );
		}
		if( StringUtils.isNotEmpty( this.getTaskId() )) {
			queryFilter.addEqualsTerm( new EqualsTerm( "taskId", this.getTaskId() ) );
		}
		if( StringUtils.isNotEmpty( this.getObjectType())) {
			queryFilter.addEqualsTerm( new EqualsTerm( "objectType", this.getObjectType() ) );
		}
		if( StringUtils.isNotEmpty( this.getOptType() )) {
			queryFilter.addEqualsTerm( new EqualsTerm( "optType", this.getOptType() ) );
		}
		if( StringUtils.isNotEmpty( this.getBundle() )) {
			queryFilter.addEqualsTerm( new EqualsTerm( "bundle", this.getBundle() ) );
		}
		return queryFilter;
	}
}
