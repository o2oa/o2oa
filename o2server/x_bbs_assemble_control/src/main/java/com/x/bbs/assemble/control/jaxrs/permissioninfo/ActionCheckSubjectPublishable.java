package com.x.bbs.assemble.control.jaxrs.permissioninfo;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.bbs.assemble.control.jaxrs.permissioninfo.exception.ExceptionPermissionInfoProcess;
import com.x.bbs.assemble.control.jaxrs.permissioninfo.exception.ExceptionSectionIdEmpty;
import com.x.bbs.assemble.control.jaxrs.permissioninfo.exception.ExceptionSectionNotExists;
import com.x.bbs.assemble.control.service.bean.RoleAndPermission;
import com.x.bbs.entity.BBSSectionInfo;

/**
 * @author sword
 */
public class ActionCheckSubjectPublishable extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionCheckSubjectPublishable.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson, String sectionId) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wrap = new Wo();
		RoleAndPermission roleAndPermission = null;

		if (effectivePerson.isAnonymous()) {
			roleAndPermission = new RoleAndPermission();
		} else {
			roleAndPermission = UserPermissionService.getUserRoleAndPermission(effectivePerson.getDistinguishedName());
		}
		BBSSectionInfo sectionInfo = sectionInfoService.get(sectionId);
		boolean publishAble = sectionInfoService.hasPublishPermission(sectionInfo, effectivePerson, roleAndPermission.getPermissionInfoList());
		wrap.setCheckResult(publishAble);
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
