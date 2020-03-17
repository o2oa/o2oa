package com.x.teamwork.assemble.control.jaxrs.project;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.teamwork.core.entity.tools.filter.QueryFilter;
import com.x.teamwork.core.entity.tools.filter.term.EqualsTerm;
import com.x.teamwork.core.entity.tools.filter.term.LikeTerm;

public class WrapInQueryProject {
	@FieldDescribe("用于排列的属性，非必填，默认为createTime.")
	private String orderField = "createTime";

	@FieldDescribe("排序方式：DESC | ASC，非必填， 默认为DESC.")
	private String orderType = "DESC";
	
	@FieldDescribe("用于搜索的标题，非必填.")
	private String title = null;
	
	@FieldDescribe("用于搜索的项目类型，非必填.")
	private String type = null;
	
	@FieldDescribe("是否已完成，非必填")
	private String completed = null;		
	
	@FieldDescribe("是否已经删除，非必填")
	private String deleted = null;
	
	@FieldDescribe("是否已经归档，非必填")
	private String archive = null;
	
	@FieldDescribe("执行者和负责人：单值，非必填")
	private String executor = null;		

	@FieldDescribe("用于搜索的项目分组标识：单值，非必填.")
	private String group = null;
	
	private Long rank = 0L;
	
	public String getGroup() {
		return group;
	}
	public void setGroup(String group) {
		this.group = group;
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
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getCompleted() {
		return completed;
	}
	public void setCompleted(String completed) {
		this.completed = completed;
	}
	public String getDeleted() {
		return deleted;
	}
	public void setDeleted(String deleted) {
		this.deleted = deleted;
	}
	public String getArchive() {
		return archive;
	}
	public void setArchive(String archive) {
		this.archive = archive;
	}
	public String getExecutor() {
		return executor;
	}
	public void setExecutor(String executor) {
		this.executor = executor;
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
		if( StringUtils.isNotEmpty( this.getTitle() )) {
			queryFilter.addLikeTerm( new LikeTerm( "title", this.getTitle() ) );
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
		if( StringUtils.isNotEmpty( this.getArchive() )) {
			if( "true".equalsIgnoreCase( this.getDeleted() )) {
				queryFilter.addEqualsTerm( new EqualsTerm( "archive", true ) );
			}else {
				queryFilter.addEqualsTerm( new EqualsTerm( "archive", false ) );
			}
		}
		return queryFilter;
	}
}
