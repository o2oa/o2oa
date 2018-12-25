package com.x.bbs.assemble.control.jaxrs.permissioninfo;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.bbs.assemble.control.jaxrs.permissioninfo.exception.ExceptionPermissionInfoProcess;
import com.x.bbs.assemble.control.jaxrs.permissioninfo.exception.ExceptionSectionIdEmpty;
import com.x.bbs.assemble.control.jaxrs.permissioninfo.exception.ExceptionSectionNotExists;
import com.x.bbs.assemble.control.service.bean.RoleAndPermission;
import com.x.bbs.entity.BBSSectionInfo;

public class ActionGetSectionOperationPermissoin extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionGetSectionOperationPermissoin.class);

	protected ActionResult<Wo> execute(HttpServletRequest request,
			EffectivePerson effectivePerson, String sectionId) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wrap = new Wo();
		RoleAndPermission roleAndPermission = null;
		BBSSectionInfo sectionInfo = null;
		Boolean hasPermission = false;
		Boolean check = true;
		String checkUserPermission = null;

		if (check) {
			if (sectionId == null || sectionId.isEmpty()) {
				check = false;
				Exception exception = new ExceptionSectionIdEmpty();
				result.error(exception);
			}
		}
		if (check) {
			try {
				sectionInfo = sectionInfoService.get(sectionId);
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionPermissionInfoProcess(e, "根据指定ID查询版块信息时发生异常.ID:" + sectionId);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		if (check) {
			if (sectionInfo == null) {
				check = false;
				Exception exception = new ExceptionSectionNotExists(sectionId);
				result.error(exception);
			}
		}
		if (check) {
			if (!"anonymous".equalsIgnoreCase(effectivePerson.getTokenType().name())) {
				try {
					roleAndPermission = UserPermissionService.getUserRoleAndPermission(effectivePerson.getDistinguishedName());
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionPermissionInfoProcess(e,
							"获取用户的论坛访问权限列表时发生异常.Person:" + effectivePerson.getDistinguishedName());
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}
			if (roleAndPermission == null) {
				roleAndPermission = new RoleAndPermission();
			}
		}
		// 判断主题发布权限
		if (check) {
			if ("所有人".equals(sectionInfo.getSubjectPublishAble())) {
				wrap.setSubjectPublishAble(true);
			} else {// 判断权限
				checkUserPermission = "SECTION_SUBJECT_PUBLISH_" + sectionInfo.getId();
				hasPermission = checkUserPermission(checkUserPermission, roleAndPermission.getPermissionInfoList());
				if (hasPermission) {
					wrap.setSubjectPublishAble(true);
				}
			}
		}
		// 判断版块主题回复权限
		if (check) {
			if ("所有人".equals(sectionInfo.getReplyPublishAble())) {
				wrap.setReplyPublishAble(true);
			} else {// 判断权限
				checkUserPermission = "SECTION_REPLY_PUBLISH_" + sectionInfo.getId();
				hasPermission = checkUserPermission(checkUserPermission, roleAndPermission.getPermissionInfoList());
				if (hasPermission) {
					wrap.setReplyPublishAble(true);
				}
			}
		}
		if (check) {
			checkUserPermission = "SECTION_SUBJECT_AUDIT_" + sectionInfo.getId();
			hasPermission = checkUserPermission(checkUserPermission, roleAndPermission.getPermissionInfoList());
			if (hasPermission) {
				wrap.setSubjectAuditAble(true);
			}
		}
		if (check) {
			checkUserPermission = "SECTION_SUBJECT_MANAGEMENT_" + sectionInfo.getId();
			hasPermission = checkUserPermission(checkUserPermission, roleAndPermission.getPermissionInfoList());
			if (hasPermission) {
				wrap.setSubjectManageAble(true);
			}
		}
		if (check) {
			checkUserPermission = "SECTION_SUBJECT_RECOMMEND_" + sectionInfo.getId();
			hasPermission = checkUserPermission(checkUserPermission, roleAndPermission.getPermissionInfoList());
			if (hasPermission) {
				wrap.setSubjectRecommendAble(true);
			}
		}
		if (check) {
			checkUserPermission = "SECTION_SUBJECT_STICK_" + sectionInfo.getId();
			hasPermission = checkUserPermission(checkUserPermission, roleAndPermission.getPermissionInfoList());
			if (hasPermission) {
				wrap.setSubjectStickAble(true);
			}
		}
		if (check) {
			checkUserPermission = "SECTION_SUBJECT_CREAM_" + sectionInfo.getId();
			hasPermission = checkUserPermission(checkUserPermission, roleAndPermission.getPermissionInfoList());
			if (hasPermission) {
				wrap.setSubjectCreamAble(true);
			}
		}
		if (check) {
			checkUserPermission = "SECTION_REPLY_AUDIT_" + sectionInfo.getId();
			hasPermission = checkUserPermission(checkUserPermission, roleAndPermission.getPermissionInfoList());
			if (hasPermission) {
				wrap.setReplyAuditAble(true);
			}
		}
		if (check) {
			checkUserPermission = "SECTION_REPLY_MANAGEMENT_" + sectionInfo.getId();
			hasPermission = checkUserPermission(checkUserPermission, roleAndPermission.getPermissionInfoList());
			if (hasPermission) {
				wrap.setReplyManageAble(true);
			}
		}
		if (check) {
			checkUserPermission = "SECTION_SECTION_MANAGER_" + sectionInfo.getId();
			hasPermission = checkUserPermission(checkUserPermission, roleAndPermission.getPermissionInfoList());
			if (hasPermission) {
				wrap.setSectionManageAble(true);
			}
		}
		if (check) {
			checkUserPermission = "SECTION_PERMISSION_MANAGEMENT_" + sectionInfo.getId();
			hasPermission = checkUserPermission(checkUserPermission, roleAndPermission.getPermissionInfoList());
			if (hasPermission) {
				wrap.setSectionPermissoinManageAble(true);
			}
		}
		if (check) {
			checkUserPermission = "SECTION_CONFIG_MANAGEMENT_" + sectionInfo.getId();
			hasPermission = checkUserPermission(checkUserPermission, roleAndPermission.getPermissionInfoList());
			if (hasPermission) {
				wrap.setSectionConfigManageAble(true);
			}
		}
		if (check) {
			if (effectivePerson.getDistinguishedName() != null
					&& sectionInfo.getCreatorName().equalsIgnoreCase(effectivePerson.getDistinguishedName())) {
				wrap.setSectionManageAble(true);
				wrap.setSectionPermissoinManageAble(true);
				wrap.setSectionConfigManageAble(true);
			}
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