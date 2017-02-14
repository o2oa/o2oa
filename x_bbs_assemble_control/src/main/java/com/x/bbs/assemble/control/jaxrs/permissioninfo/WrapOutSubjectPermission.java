package com.x.bbs.assemble.control.jaxrs.permissioninfo;

import com.x.base.core.entity.annotation.EntityFieldDescribe;
import com.x.base.core.http.annotation.Wrap;

@Wrap
public class WrapOutSubjectPermission{
	
	@EntityFieldDescribe( "用户是否可以审核该主题." )
	private Boolean auditAble = false;
	
	@EntityFieldDescribe( "用户是否可以删除或者管理该主题." )
	private Boolean manageAble = false;	
	
	@EntityFieldDescribe( "用户是否可以推荐该主题." )
	private Boolean recommendAble = false;
	
	@EntityFieldDescribe( "用户是否可以置顶该主题." )
	private Boolean stickAble = false;
	
	@EntityFieldDescribe( "用户是否可以对该主题进行精华主题设置操作." )
	private Boolean creamAble = false;
	
	@EntityFieldDescribe( "用户是否可以在该主题中进行回复操作." )
	private Boolean replyPublishAble = false;
	
	@EntityFieldDescribe( "用户是否可以在版块中对回复进行审核." )
	private Boolean replyAuditAble = false;	
	
	@EntityFieldDescribe( "用户是否可以在主题中对回复进行查询或者删除." )
	private Boolean replyManageAble = false;

	public Boolean getAuditAble() {
		return auditAble;
	}

	public void setAuditAble(Boolean auditAble) {
		this.auditAble = auditAble;
	}

	public Boolean getManageAble() {
		return manageAble;
	}

	public void setManageAble(Boolean manageAble) {
		this.manageAble = manageAble;
	}

	public Boolean getRecommendAble() {
		return recommendAble;
	}

	public void setRecommendAble(Boolean recommendAble) {
		this.recommendAble = recommendAble;
	}

	public Boolean getStickAble() {
		return stickAble;
	}

	public void setStickAble(Boolean stickAble) {
		this.stickAble = stickAble;
	}

	public Boolean getCreamAble() {
		return creamAble;
	}

	public void setCreamAble(Boolean creamAble) {
		this.creamAble = creamAble;
	}

	public Boolean getReplyPublishAble() {
		return replyPublishAble;
	}

	public void setReplyPublishAble(Boolean replyPublishAble) {
		this.replyPublishAble = replyPublishAble;
	}

	public Boolean getReplyManageAble() {
		return replyManageAble;
	}

	public void setReplyManageAble(Boolean replyManageAble) {
		this.replyManageAble = replyManageAble;
	}

	public Boolean getReplyAuditAble() {
		return replyAuditAble;
	}

	public void setReplyAuditAble(Boolean replyAuditAble) {
		this.replyAuditAble = replyAuditAble;
	}
	
}
