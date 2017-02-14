package com.x.bbs.assemble.control.jaxrs.permissioninfo;

import com.x.base.core.entity.annotation.EntityFieldDescribe;
import com.x.base.core.http.annotation.Wrap;

@Wrap
public class WrapOutSectionPermission{
	
	@EntityFieldDescribe( "对应权限SECTION_SUBJECT_PUBLISH:用户是否可以在版块中发布主题." )
	private Boolean subjectPublishAble = false;
	
	@EntityFieldDescribe( "对应权限SECTION_SUBJECT_AUDIT:用户是否可以审核在版块中发布的主题." )
	private Boolean subjectAuditAble = false;
	
	@EntityFieldDescribe( "对应权限SECTION_SUBJECT_MANAGEMENT:用户是否可以在版块中对已发布主题进行删除等管理操作." )
	private Boolean subjectManageAble = false;	
	
	@EntityFieldDescribe( "对应权限SECTION_SUBJECT_RECOMMEND:用户是否可以在版块中对主题进行推荐操作." )
	private Boolean subjectRecommendAble = false;
	
	@EntityFieldDescribe( "对应权限SECTION_SUBJECT_STICK:用户是否可以在版块中对主题进行置顶操作." )
	private Boolean subjectStickAble = false;
	
	@EntityFieldDescribe( "对应权限SECTION_SUBJECT_CREAM:用户是否可以在版块中对指定主题进行精华主题设置操作." )
	private Boolean subjectCreamAble = false;
	
	@EntityFieldDescribe( "对应权限SECTION_REPLY_PUBLISH:用户是否可以审核在版块中的所有回复内容." )
	private Boolean replyPublishAble = false;
	
	@EntityFieldDescribe( "对应权限SECTION_REPLY_AUDIT:用户是否可以在版块中对回复进行审核." )
	private Boolean replyAuditAble = false;	
	
	@EntityFieldDescribe( "对应权限SECTION_REPLY_MANAGEMENT:用户是否可以在版块中对回复进行查询或者删除." )
	private Boolean replyManageAble = false;
	
	@EntityFieldDescribe( "对应权限SECTION_SECTION_MANAGER:用户是否可以在版块中对子版块进行创建和删除等操作." )
	private Boolean sectionManageAble = false;
	
	@EntityFieldDescribe( "对应权限SECTION_PERMISSION_MANAGEMENT:用户是否可以对论坛用户进行版块的权限管理." )
	private Boolean sectionPermissoinManageAble = false;
	
	@EntityFieldDescribe( "对应权限SECTION_CONFIG_MANAGEMENT:用户是否可以对版块进行系统参数配置修改." )
	private Boolean sectionConfigManageAble = false;

	public Boolean getSubjectPublishAble() {
		return subjectPublishAble;
	}

	public void setSubjectPublishAble(Boolean subjectPublishAble) {
		this.subjectPublishAble = subjectPublishAble;
	}

	public Boolean getSubjectAuditAble() {
		return subjectAuditAble;
	}

	public void setSubjectAuditAble(Boolean subjectAuditAble) {
		this.subjectAuditAble = subjectAuditAble;
	}

	public Boolean getSubjectManageAble() {
		return subjectManageAble;
	}

	public void setSubjectManageAble(Boolean subjectManageAble) {
		this.subjectManageAble = subjectManageAble;
	}

	public Boolean getSubjectRecommendAble() {
		return subjectRecommendAble;
	}

	public void setSubjectRecommendAble(Boolean subjectRecommendAble) {
		this.subjectRecommendAble = subjectRecommendAble;
	}

	public Boolean getSubjectStickAble() {
		return subjectStickAble;
	}

	public void setSubjectStickAble(Boolean subjectStickAble) {
		this.subjectStickAble = subjectStickAble;
	}

	public Boolean getSubjectCreamAble() {
		return subjectCreamAble;
	}

	public void setSubjectCreamAble(Boolean subjectCreamAble) {
		this.subjectCreamAble = subjectCreamAble;
	}

	public Boolean getReplyPublishAble() {
		return replyPublishAble;
	}

	public void setReplyPublishAble(Boolean replyPublishAble) {
		this.replyPublishAble = replyPublishAble;
	}

	public Boolean getReplyAuditAble() {
		return replyAuditAble;
	}

	public void setReplyAuditAble(Boolean replyAuditAble) {
		this.replyAuditAble = replyAuditAble;
	}

	public Boolean getReplyManageAble() {
		return replyManageAble;
	}

	public void setReplyManageAble(Boolean replyManageAble) {
		this.replyManageAble = replyManageAble;
	}

	public Boolean getSectionManageAble() {
		return sectionManageAble;
	}

	public void setSectionManageAble(Boolean sectionManageAble) {
		this.sectionManageAble = sectionManageAble;
	}

	public Boolean getSectionPermissoinManageAble() {
		return sectionPermissoinManageAble;
	}

	public void setSectionPermissoinManageAble(Boolean sectionPermissoinManageAble) {
		this.sectionPermissoinManageAble = sectionPermissoinManageAble;
	}

	public Boolean getSectionConfigManageAble() {
		return sectionConfigManageAble;
	}

	public void setSectionConfigManageAble(Boolean sectionConfigManageAble) {
		this.sectionConfigManageAble = sectionConfigManageAble;
	}
	
}
