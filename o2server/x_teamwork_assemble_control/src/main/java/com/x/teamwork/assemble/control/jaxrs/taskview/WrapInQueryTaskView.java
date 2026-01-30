package com.x.teamwork.assemble.control.jaxrs.taskview;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.tools.ListTools;
import com.x.teamwork.core.entity.tools.filter.QueryFilter;
import com.x.teamwork.core.entity.tools.filter.term.EqualsTerm;
import com.x.teamwork.core.entity.tools.filter.term.InTerm;
import com.x.teamwork.core.entity.tools.filter.term.LikeTerm;

public class WrapInQueryTaskView {
	
	@FieldDescribe("筛选ID列表，非必填")
	private List<String> ids;
	
	@FieldDescribe("用于排列的属性，非必填，默认为createTime.")
	private String orderField = "createTime";

	@FieldDescribe("排序方式：DESC | ASC，非必填，默认为DESC.")
	private String orderType = "DESC";

	@FieldDescribe("所属项目ID，非必填。")
	private String project;

	@FieldDescribe("任务视图名称，非必填。")
	private String name;

	@FieldDescribe("拥有者@P，非必填。")
	private String owner;

	private Long rank = 0L;
	
	public List<String> getIds() {
		return ids;
	}
	public void setIds(List<String> ids) {
		this.ids = ids;
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
	public String getProject() {
		return project;
	}
	public void setProject(String project) {
		this.project = project;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
		//组织查询条件对象
		if( ListTools.isNotEmpty( this.getIds() )) {			
			if( this.getIds().size() == 1 ) {
				queryFilter.addEqualsTerm( new EqualsTerm( "choosideWorkTag", this.getIds().get(0) ) );
			}else {
				queryFilter.addInTerm( new InTerm( "id",  new ArrayList<>(this.getIds())  ) );
			}
		}
		if( StringUtils.isNotEmpty( this.getProject() )) {
			queryFilter.addEqualsTerm( new EqualsTerm( "project", this.getProject() ) );
		}
		if( StringUtils.isNotEmpty( this.getName() )) {
			queryFilter.addLikeTerm( new LikeTerm( "name", "%" + this.getName() + "%" ) );
		}
		if( StringUtils.isNotEmpty( this.getOwner() )) {
			queryFilter.addEqualsTerm( new EqualsTerm( "owner", this.getOwner() ) );
		}
		return queryFilter;
	}
}
