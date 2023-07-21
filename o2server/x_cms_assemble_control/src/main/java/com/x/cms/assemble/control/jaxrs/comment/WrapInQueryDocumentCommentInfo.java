package com.x.cms.assemble.control.jaxrs.comment;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.cms.core.express.tools.filter.QueryFilter;
import com.x.cms.core.express.tools.filter.term.EqualsTerm;
import com.x.cms.core.express.tools.filter.term.IsFalseTerm;
import com.x.cms.core.express.tools.filter.term.IsTrueTerm;
import com.x.cms.core.express.tools.filter.term.LikeTerm;

public class WrapInQueryDocumentCommentInfo {
	
	@FieldDescribe("用于排列的属性.")
	private String orderField = "orderNumber";

	@FieldDescribe("排序方式：DESC | ASC.")
	private String orderType = "ASC";
	
	@FieldDescribe("搜索条件：栏目ID")
	private String appId;

	@FieldDescribe("搜索条件：分类ID")
	private String categoryId;

	@FieldDescribe("搜索条件：文档ID")
	private String documentId;

	@FieldDescribe("搜索条件：评论标题")
	private String title;

	@FieldDescribe("搜索条件：上级评论ID")
	private String parentId;

	@FieldDescribe("搜索条件：创建人姓名")
	private String creatorName;

	@FieldDescribe("搜索条件：评论审核状态：无审核|待审核|审核通过")
	private String commentAuditStatus;

	@FieldDescribe("搜索条件：审核人")
	private String auditorName;
	
	@FieldDescribe("评论的权限：私信评论 | 公开评论 | 全部评论.")
	private String permission = "private | public | all";
	
	private Long rank = 0L;	
	
	public String getPermission() {
		return permission;
	}

	public void setPermission(String permission) {
		this.permission = permission;
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

	public Long getRank() {
		return rank;
	}

	public void setRank(Long rank) {
		this.rank = rank;
	}
	
	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

	public String getDocumentId() {
		return documentId;
	}

	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getCreatorName() {
		return creatorName;
	}

	public void setCreatorName(String creatorName) {
		this.creatorName = creatorName;
	}

	public String getCommentAuditStatus() {
		return commentAuditStatus;
	}

	public void setCommentAuditStatus(String commentAuditStatus) {
		this.commentAuditStatus = commentAuditStatus;
	}

	public String getAuditorName() {
		return auditorName;
	}

	public void setAuditorName(String auditorName) {
		this.auditorName = auditorName;
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
		if( StringUtils.isNotEmpty( this.getAppId() )) {
			queryFilter.addEqualsTerm( new EqualsTerm( "appId", this.getAppId() ) );
		}
		if( StringUtils.isNotEmpty( this.getCategoryId() )) {
			queryFilter.addEqualsTerm( new EqualsTerm( "categoryId", this.getCategoryId() ) );
		}
		if( StringUtils.isNotEmpty( this.getDocumentId() )) {
			queryFilter.addEqualsTerm( new EqualsTerm( "documentId", this.getDocumentId() ) );
		}
		if( StringUtils.isNotEmpty( this.getParentId() )) {
			queryFilter.addEqualsTerm( new EqualsTerm( "parentId", this.getParentId() ) );
		}
		if( StringUtils.isNotEmpty( this.getAuditorName() )) {
			queryFilter.addEqualsTerm( new EqualsTerm( "auditorName", this.getAuditorName() ) );
		}
		if( StringUtils.isNotEmpty( this.getCommentAuditStatus() )) {
			queryFilter.addEqualsTerm( new EqualsTerm( "commentAuditStatus", this.getCommentAuditStatus() ) );
		}
		if( StringUtils.isNotEmpty( this.getCreatorName() )) {
			queryFilter.addEqualsTerm( new EqualsTerm( "creatorName", this.getCreatorName() ) );
		}
		if( StringUtils.isNotEmpty( this.getPermission() )) {
			if( "public".equalsIgnoreCase( this.getPermission() )) {
				queryFilter.addIsFalseTerm( new IsFalseTerm( "isPrivate" ));
			}else if( "private".equalsIgnoreCase( this.getPermission() )) {
				queryFilter.addIsTrueTerm( new IsTrueTerm( "isPrivate" ));
			}
		}
		return queryFilter;
	}
}
