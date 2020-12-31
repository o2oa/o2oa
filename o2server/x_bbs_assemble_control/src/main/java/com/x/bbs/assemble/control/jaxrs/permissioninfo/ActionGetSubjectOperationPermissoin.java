package com.x.bbs.assemble.control.jaxrs.permissioninfo;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.bbs.assemble.control.Business;
import com.x.bbs.assemble.control.jaxrs.permissioninfo.exception.ExceptionPermissionInfoProcess;
import com.x.bbs.assemble.control.jaxrs.permissioninfo.exception.ExceptionSectionNotExists;
import com.x.bbs.assemble.control.jaxrs.permissioninfo.exception.ExceptionSubjectIdEmpty;
import com.x.bbs.assemble.control.jaxrs.permissioninfo.exception.ExceptionSubjectNotExists;
import com.x.bbs.assemble.control.service.bean.RoleAndPermission;
import com.x.bbs.entity.BBSSectionInfo;
import com.x.bbs.entity.BBSSubjectInfo;

public class ActionGetSubjectOperationPermissoin extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionGetSubjectOperationPermissoin.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, String subjectId) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wrap = new Wo();
		RoleAndPermission roleAndPermission = null;
		BBSSubjectInfo subjectInfo = null;
		BBSSectionInfo sectionInfo = null;
		Boolean hasPermission = false;
		Boolean publishAble = true;
		Boolean check = true;
		String checkUserPermission = null;

		if (check) {
			if (subjectId == null || subjectId.isEmpty()) {
				check = false;
				Exception exception = new ExceptionSubjectIdEmpty();
				result.error(exception);
			}
		}
		if (check) {
			try {
				subjectInfo = subjectInfoService.get(subjectId);
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionPermissionInfoProcess(e, "根据指定ID查询主题信息时发生异常.ID:" + subjectId);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		if (check) {
			if (subjectInfo == null) {
				check = false;
				Exception exception = new ExceptionSubjectNotExists(subjectId);
				result.error(exception);
			}
		}
		if (check) {
			try {
				sectionInfo = sectionInfoService.get(subjectInfo.getSectionId());
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionPermissionInfoProcess(e,
						"根据指定ID查询版块信息时发生异常.ID:" + subjectInfo.getSectionId());
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		if (check && publishAble) {
			if (sectionInfo == null) {
				check = false;
				Exception exception = new ExceptionSectionNotExists(subjectInfo.getSectionId());
				result.error(exception);
				// logger.error( e, effectivePerson, request, null);
			}
		}
		if (check) {// 获取用户的权限列表
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
		}
		if (roleAndPermission == null) {
			roleAndPermission = new RoleAndPermission();
		}
		if (check) {
			if ("所有人".equals(sectionInfo.getReplyPublishAble())) {
				wrap.setReplyPublishAble(true);
			} else {
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
				wrap.setAuditAble(true);
			}
		}
		if (check) {
			checkUserPermission = "SECTION_SUBJECT_MANAGEMENT_" + sectionInfo.getId();
			hasPermission = checkUserPermission(checkUserPermission, roleAndPermission.getPermissionInfoList());
			if (hasPermission) {
				wrap.setManageAble(true);
				wrap.setEditAble(true);
			}
		}
		if (check) {
			checkUserPermission = "SECTION_SUBJECT_RECOMMEND_" + sectionInfo.getId();
			hasPermission = checkUserPermission(checkUserPermission, roleAndPermission.getPermissionInfoList());
			if (hasPermission) {
				wrap.setRecommendAble(true);
			}
		}
		if (check) {
			checkUserPermission = "SECTION_SUBJECT_STICK_" + sectionInfo.getId();
			hasPermission = checkUserPermission(checkUserPermission, roleAndPermission.getPermissionInfoList());
			if (hasPermission) {
				wrap.setStickAble(true);
			}
		}
		if (check) {
			checkUserPermission = "SECTION_SUBJECT_CREAM_" + sectionInfo.getId();
			hasPermission = checkUserPermission(checkUserPermission, roleAndPermission.getPermissionInfoList());
			if (hasPermission) {
				wrap.setCreamAble(true);
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
			if (subjectInfo.getStopReply()) {
				wrap.setReplyPublishAble(false);
			}
			if (!"启用".equals(subjectInfo.getSubjectStatus())) {
				wrap.setReplyPublishAble(false);
			}
		}
		// 自己发的贴子自己可以删除和编辑
		if (check) {
			if (effectivePerson.getDistinguishedName() != null
					&& subjectInfo.getCreatorName().equalsIgnoreCase(effectivePerson.getDistinguishedName())) {
				//如果该贴子已经有回复内容了，就不允许删除了
				if( replyInfoService.countWithSubjectForPage( subjectId, false ) == 0 ){
					wrap.setEditAble(true);
				}else{
					wrap.setEditAble(false);
				}
			}
		}
		
		//是否是管理员
		Business business = new Business(null);
		Boolean userBBSManager = business.organization().person().hasRole(effectivePerson, OrganizationDefinition.BBSManager);
		Boolean userManager = business.organization().person().hasRole(effectivePerson, OrganizationDefinition.Manager);
		if(userBBSManager || userManager) {
			wrap.setReplyPublishAble(true);
			wrap.setAuditAble(true);
			wrap.setManageAble(true);
			wrap.setEditAble(true);
			wrap.setRecommendAble(true);
			wrap.setStickAble(true);
			wrap.setCreamAble(true);
			wrap.setReplyAuditAble(true);
		    wrap.setReplyManageAble(true);		
		 }

		result.setData(wrap);
		return result;
	}

	public static class Wo{
		
		@FieldDescribe( "用户是否可以审核该主题." )
		private Boolean auditAble = false;
		
		@FieldDescribe( "用户是否可以编辑该主题." )
		private Boolean editAble = false;
		
		@FieldDescribe( "用户是否可以管理该主题." )
		private Boolean manageAble = false;	
		
		@FieldDescribe( "用户是否可以推荐该主题." )
		private Boolean recommendAble = false;
		
		@FieldDescribe( "用户是否可以置顶该主题." )
		private Boolean stickAble = false;
		
		@FieldDescribe( "用户是否可以对该主题进行精华主题设置操作." )
		private Boolean creamAble = false;
		
		@FieldDescribe( "用户是否可以在该主题中进行回复操作." )
		private Boolean replyPublishAble = false;
		
		@FieldDescribe( "用户是否可以在版块中对回复进行审核." )
		private Boolean replyAuditAble = false;	
		
		@FieldDescribe( "用户是否可以在主题中对回复进行查询或者删除." )
		private Boolean replyManageAble = false;		

		public Boolean getEditAble() {
			return editAble;
		}

		public void setEditAble(Boolean editAble) {
			this.editAble = editAble;
		}

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
}