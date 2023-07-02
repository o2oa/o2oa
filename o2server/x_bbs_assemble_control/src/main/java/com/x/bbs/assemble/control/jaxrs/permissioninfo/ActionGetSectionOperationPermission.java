package com.x.bbs.assemble.control.jaxrs.permissioninfo;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.bbs.assemble.control.Business;
import com.x.bbs.assemble.control.jaxrs.permissioninfo.exception.ExceptionSectionNotExists;
import com.x.bbs.assemble.control.service.bean.RoleAndPermission;
import com.x.bbs.entity.BBSSectionInfo;

public class ActionGetSectionOperationPermission extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionGetSectionOperationPermission.class);

	protected ActionResult<Wo> execute(HttpServletRequest request,
			EffectivePerson effectivePerson, String sectionId) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wrap = new Wo();
		RoleAndPermission roleAndPermission = null;
		BBSSectionInfo sectionInfo = sectionInfoService.get(sectionId);
		Boolean hasPermission = false;
		String checkUserPermission;
		if (sectionInfo == null) {
			throw  new ExceptionSectionNotExists(sectionId);
		}
		if (!effectivePerson.isAnonymous()) {
			roleAndPermission = UserPermissionService.getUserRoleAndPermission(effectivePerson.getDistinguishedName());
		}
		if (roleAndPermission == null) {
			roleAndPermission = new RoleAndPermission();
		}
		// 判断主题发布权限
		wrap.setSubjectPublishAble(sectionInfoService.hasPublishPermission(sectionInfo, effectivePerson, roleAndPermission.getPermissionInfoList()));

		// 判断版块主题回复权限
		if ("所有人".equals(sectionInfo.getReplyPublishAble())) {
			wrap.setReplyPublishAble(true);
		} else {// 判断权限
			checkUserPermission = "SECTION_REPLY_PUBLISH_" + sectionInfo.getId();
			hasPermission = checkUserPermission(checkUserPermission, roleAndPermission.getPermissionInfoList());
			if (hasPermission) {
				wrap.setReplyPublishAble(true);
			}
		}

		checkUserPermission = "SECTION_SUBJECT_AUDIT_" + sectionInfo.getId();
		hasPermission = checkUserPermission(checkUserPermission, roleAndPermission.getPermissionInfoList());
		if (hasPermission) {
			wrap.setSubjectAuditAble(true);
		}

		checkUserPermission = "SECTION_SUBJECT_MANAGEMENT_" + sectionInfo.getId();
		hasPermission = checkUserPermission(checkUserPermission, roleAndPermission.getPermissionInfoList());
		if (hasPermission) {
			wrap.setSubjectManageAble(true);
		}

		checkUserPermission = "SECTION_SUBJECT_RECOMMEND_" + sectionInfo.getId();
		hasPermission = checkUserPermission(checkUserPermission, roleAndPermission.getPermissionInfoList());
		if (hasPermission) {
			wrap.setSubjectRecommendAble(true);
		}

		checkUserPermission = "SECTION_SUBJECT_STICK_" + sectionInfo.getId();
		hasPermission = checkUserPermission(checkUserPermission, roleAndPermission.getPermissionInfoList());
		if (hasPermission) {
			wrap.setSubjectStickAble(true);
		}

		checkUserPermission = "SECTION_SUBJECT_CREAM_" + sectionInfo.getId();
		hasPermission = checkUserPermission(checkUserPermission, roleAndPermission.getPermissionInfoList());
		if (hasPermission) {
			wrap.setSubjectCreamAble(true);
		}

		checkUserPermission = "SECTION_REPLY_AUDIT_" + sectionInfo.getId();
		hasPermission = checkUserPermission(checkUserPermission, roleAndPermission.getPermissionInfoList());
		if (hasPermission) {
			wrap.setReplyAuditAble(true);
		}

		checkUserPermission = "SECTION_REPLY_MANAGEMENT_" + sectionInfo.getId();
		hasPermission = checkUserPermission(checkUserPermission, roleAndPermission.getPermissionInfoList());
		if (hasPermission) {
			wrap.setReplyManageAble(true);
		}

		checkUserPermission = "SECTION_SECTION_MANAGER_" + sectionInfo.getId();
		hasPermission = checkUserPermission(checkUserPermission, roleAndPermission.getPermissionInfoList());
		if (hasPermission) {
			wrap.setSectionManageAble(true);
		}

		checkUserPermission = "SECTION_PERMISSION_MANAGEMENT_" + sectionInfo.getId();
		hasPermission = checkUserPermission(checkUserPermission, roleAndPermission.getPermissionInfoList());
		if (hasPermission) {
			wrap.setSectionPermissoinManageAble(true);
		}

		checkUserPermission = "SECTION_CONFIG_MANAGEMENT_" + sectionInfo.getId();
		hasPermission = checkUserPermission(checkUserPermission, roleAndPermission.getPermissionInfoList());
		if (hasPermission) {
			wrap.setSectionConfigManageAble(true);
		}

		if (effectivePerson.getDistinguishedName() != null
				&& sectionInfo.getCreatorName().equalsIgnoreCase(effectivePerson.getDistinguishedName())) {
			wrap.setSectionManageAble(true);
			wrap.setSectionPermissoinManageAble(true);
			wrap.setSectionConfigManageAble(true);
		}
		//是否是管理员
		Business business = new Business(null);
		Boolean userBBSManager = business.isManager(effectivePerson);
		if(userBBSManager) {
			wrap.setSubjectPublishAble(true);
			wrap.setReplyPublishAble(true);
			wrap.setReplyAuditAble(true);
			wrap.setSubjectAuditAble(true);
			wrap.setSubjectManageAble(true);
			wrap.setSubjectRecommendAble(true);
			wrap.setSubjectStickAble(true);
			wrap.setSubjectCreamAble(true);
			wrap.setReplyManageAble(true);

			wrap.setSectionManageAble(true);
			wrap.setSectionPermissoinManageAble(true);
			wrap.setSectionConfigManageAble(true);
		}

		result.setData(wrap);
		return result;
	}

	public static class Wo{

		@FieldDescribe( "对应权限SECTION_SUBJECT_PUBLISH:用户是否可以在版块中发布主题." )
		private Boolean subjectPublishAble = false;

		@FieldDescribe( "对应权限SECTION_SUBJECT_AUDIT:用户是否可以审核在版块中发布的主题." )
		private Boolean subjectAuditAble = false;

		@FieldDescribe( "对应权限SECTION_SUBJECT_MANAGEMENT:用户是否可以在版块中对已发布主题进行删除等管理操作." )
		private Boolean subjectManageAble = false;

		@FieldDescribe( "对应权限SECTION_SUBJECT_RECOMMEND:用户是否可以在版块中对主题进行推荐操作." )
		private Boolean subjectRecommendAble = false;

		@FieldDescribe( "对应权限SECTION_SUBJECT_STICK:用户是否可以在版块中对主题进行置顶操作." )
		private Boolean subjectStickAble = false;

		@FieldDescribe( "对应权限SECTION_SUBJECT_CREAM:用户是否可以在版块中对指定主题进行精华主题设置操作." )
		private Boolean subjectCreamAble = false;

		@FieldDescribe( "对应权限SECTION_REPLY_PUBLISH:用户是否可以审核在版块中的所有回复内容." )
		private Boolean replyPublishAble = false;

		@FieldDescribe( "对应权限SECTION_REPLY_AUDIT:用户是否可以在版块中对回复进行审核." )
		private Boolean replyAuditAble = false;

		@FieldDescribe( "对应权限SECTION_REPLY_MANAGEMENT:用户是否可以在版块中对回复进行查询或者删除." )
		private Boolean replyManageAble = false;

		@FieldDescribe( "对应权限SECTION_SECTION_MANAGER:用户是否可以在版块中对子版块进行创建和删除等操作." )
		private Boolean sectionManageAble = false;

		@FieldDescribe( "对应权限SECTION_PERMISSION_MANAGEMENT:用户是否可以对论坛用户进行版块的权限管理." )
		private Boolean sectionPermissoinManageAble = false;

		@FieldDescribe( "对应权限SECTION_CONFIG_MANAGEMENT:用户是否可以对版块进行系统参数配置修改." )
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
}
