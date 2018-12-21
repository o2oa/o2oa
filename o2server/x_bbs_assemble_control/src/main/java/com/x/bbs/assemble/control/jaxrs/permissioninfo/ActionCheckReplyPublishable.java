package com.x.bbs.assemble.control.jaxrs.permissioninfo;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.bbs.assemble.control.jaxrs.permissioninfo.exception.ExceptionPermissionInfoProcess;
import com.x.bbs.assemble.control.jaxrs.permissioninfo.exception.ExceptionSectionNotExists;
import com.x.bbs.assemble.control.jaxrs.permissioninfo.exception.ExceptionSubjectIdEmpty;
import com.x.bbs.assemble.control.jaxrs.permissioninfo.exception.ExceptionSubjectNotExists;
import com.x.bbs.assemble.control.service.bean.RoleAndPermission;
import com.x.bbs.entity.BBSSectionInfo;
import com.x.bbs.entity.BBSSubjectInfo;

public class ActionCheckReplyPublishable extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionCheckReplyPublishable.class);

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
				publishAble = false;
				Exception exception = new ExceptionSubjectIdEmpty();
				result.error(exception);
			}
		}
		if (check) {// 获取用户的权限列表
			if ("anonymous".equalsIgnoreCase(effectivePerson.getTokenType().name())) {
				roleAndPermission = new RoleAndPermission();
			} else {
				try {
					roleAndPermission = UserPermissionService.getUserRoleAndPermission(effectivePerson.getDistinguishedName());
				} catch (Exception e) {
					check = false;
					publishAble = false;
					Exception exception = new ExceptionPermissionInfoProcess(e,
							"获取用户的论坛访问权限列表时发生异常.Person:" + effectivePerson.getDistinguishedName());
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}
		}
		if (check) {
			try {
				subjectInfo = subjectInfoService.get(subjectId);
			} catch (Exception e) {
				check = false;
				publishAble = false;
				Exception exception = new ExceptionPermissionInfoProcess(e, "根据指定ID查询主题信息时发生异常.ID:" + subjectId);
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		if (check) {
			if (subjectInfo == null) {
				check = false;
				publishAble = false;
				Exception exception = new ExceptionSubjectNotExists(subjectId);
				result.error(exception);
			} else {
				if (subjectInfo.getStopReply()) {
					publishAble = false;
				}
				if (!"启用".equals(subjectInfo.getSubjectStatus())) {
					publishAble = false;
				}
			}
		}
		if (check && publishAble) { // 再判断用户是否可以在主题所在的版块进行回复
			try {
				sectionInfo = sectionInfoService.get(subjectInfo.getSectionId());
			} catch (Exception e) {
				check = false;
				publishAble = false;
				Exception exception = new ExceptionPermissionInfoProcess(e,
						"根据指定ID查询版块信息时发生异常.ID:" + subjectInfo.getSectionId());
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		if (check && publishAble) {
			if (sectionInfo == null) {
				check = false;
				publishAble = false;
				Exception exception = new ExceptionSectionNotExists(subjectInfo.getSectionId());
				result.error(exception);
			} else {
				if ("所有人".equals(sectionInfo.getReplyPublishAble())) {
				} else {// 判断权限
					checkUserPermission = "SECTION_REPLY_PUBLISH_" + sectionInfo.getId();
					hasPermission = checkUserPermission(checkUserPermission, roleAndPermission.getPermissionInfoList());
					if (!hasPermission) {
						publishAble = false;
					}
				}
			}
		}

		if (check) {
			wrap.setCheckResult(publishAble);
		}

		result.setData(wrap);
		return result;
	}

	public static class Wo{
		
		public static List<String> Excludes = new ArrayList<String>();
		
		//是否拥有权限
		private Boolean checkResult = false;

		public Boolean getCheckResult() {
			return checkResult;
		}

		public void setCheckResult(Boolean checkResult) {
			this.checkResult = checkResult;
		}
		
	}
}