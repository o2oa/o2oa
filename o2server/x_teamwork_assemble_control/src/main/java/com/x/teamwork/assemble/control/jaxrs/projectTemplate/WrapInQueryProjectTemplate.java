package com.x.teamwork.assemble.control.jaxrs.projectTemplate;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.teamwork.core.entity.tools.filter.QueryFilter;
import com.x.teamwork.core.entity.tools.filter.term.EqualsTerm;
import com.x.teamwork.core.entity.tools.filter.term.LikeTerm;

public class WrapInQueryProjectTemplate {
	@FieldDescribe("用于排列的属性，非必填，默认为createTime.")
	private String orderField = "createTime";

	@FieldDescribe("排序方式：DESC | ASC，非必填， 默认为DESC.")
	private String orderType = "DESC";
	
	@FieldDescribe("用于搜索的标题，非必填.")
	private String title = null;
	
	@FieldDescribe("用于搜索的项目模板类型，非必填.")
	private String type = null;			
	
	@FieldDescribe("是否已经删除，非必填")
	private String deleted = null;
	
	@FieldDescribe("创建者，非必填")
	private String owner = null;		
	
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
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getDeleted() {
		return deleted;
	}
	public void setDeleted(String deleted) {
		this.deleted = deleted;
	}
	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
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
		if( StringUtils.isNotEmpty( this.getType())) {
			queryFilter.addEqualsTerm( new EqualsTerm( "type", this.getType() ) );
		}
		if( StringUtils.isNotEmpty( this.getOwner())) {
			queryFilter.addEqualsTerm( new EqualsTerm( "owner", this.getOwner() ) );
		}
		if( StringUtils.isNotEmpty( this.getDeleted() )) {
			if( "true".equalsIgnoreCase( this.getDeleted() )) {
				queryFilter.addEqualsTerm( new EqualsTerm( "deleted", true ) );
			}else {
				queryFilter.addEqualsTerm( new EqualsTerm( "deleted", false ) );
			}
		}
		return queryFilter;
	}
}
