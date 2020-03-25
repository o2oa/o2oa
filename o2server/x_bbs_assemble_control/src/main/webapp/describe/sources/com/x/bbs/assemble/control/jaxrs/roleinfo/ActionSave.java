package com.x.bbs.assemble.control.jaxrs.roleinfo;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.github.stuxuhai.jpinyin.PinyinHelper;
import com.google.gson.JsonElement;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.bbs.assemble.control.jaxrs.roleinfo.exception.ExceptionForumIdEmpty;
import com.x.bbs.assemble.control.jaxrs.roleinfo.exception.ExceptionForumInfoNotExists;
import com.x.bbs.assemble.control.jaxrs.roleinfo.exception.ExceptionRoleINameEmpty;
import com.x.bbs.assemble.control.jaxrs.roleinfo.exception.ExceptionRoleITypeEmpty;
import com.x.bbs.assemble.control.jaxrs.roleinfo.exception.ExceptionRoleITypeInvalid;
import com.x.bbs.assemble.control.jaxrs.roleinfo.exception.ExceptionRoleInfoProcess;
import com.x.bbs.assemble.control.jaxrs.roleinfo.exception.ExceptionSectionIdEmpty;
import com.x.bbs.assemble.control.jaxrs.roleinfo.exception.ExceptionSectionNotExists;
import com.x.bbs.entity.BBSForumInfo;
import com.x.bbs.entity.BBSRoleInfo;
import com.x.bbs.entity.BBSSectionInfo;

public class ActionSave extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionSave.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson,
			JsonElement jsonElement) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		Wo wrap = null;
		BBSForumInfo forumInfo = null;
		BBSSectionInfo sectionInfo = null;
		BBSRoleInfo roleInfo_old = null;
		BBSRoleInfo roleInfo = new BBSRoleInfo();
		List<String> permissionCodes = null;
		String hostIp = request.getRemoteAddr();
		String hostName = request.getRemoteAddr();
		String uniCode = null;
		Wi wrapIn = null;
		Boolean check = true;

		try {
			wrapIn = this.convertToWrapIn(jsonElement, Wi.class);
		} catch (Exception e) {
			check = false;
			Exception exception = new ExceptionRoleInfoProcess(e,
					"系统在将JSON信息转换为对象时发生异常。JSON:" + jsonElement.toString());
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}

		if (check) {
			if (wrapIn.getRoleName() == null || wrapIn.getRoleName().isEmpty()) {
				check = false;
				Exception exception = new ExceptionRoleINameEmpty();
				result.error(exception);
			}
		}
		// 校验角色类别
		if (check) {
			if (wrapIn.getRoleType() == null || wrapIn.getRoleType().isEmpty()) {
				check = false;
				Exception exception = new ExceptionRoleITypeEmpty();
				result.error(exception);
			}
		}
		// 检验信息，如果是论坛角色，那么论坛ID为必须填写，如果是版块角色那么版块ID必须填写
		if (check) {
			if ("论坛角色".equals(wrapIn.getRoleType())) {
				if (wrapIn.getForumId() == null || wrapIn.getForumId().isEmpty()) {
					check = false;
					Exception exception = new ExceptionForumIdEmpty();
					result.error(exception);
				}
			} else if ("版块角色".equals(wrapIn.getRoleType())) {
				if (wrapIn.getSectionId() == null || wrapIn.getSectionId().isEmpty()) {
					check = false;
					Exception exception = new ExceptionSectionIdEmpty();
					result.error(exception);
				}
			} else {
				check = false;
				Exception exception = new ExceptionRoleITypeInvalid(wrapIn.getRoleType());
				result.error(exception);
			}
		}
		// 检验传入的版块ID和论坛ID是否合法
		if (check) {
			if ("论坛角色".equals(wrapIn.getRoleType())) {
				try {
					forumInfo = forumInfoServiceAdv.get(wrapIn.getForumId());
					if (forumInfo == null) {
						check = false;
						Exception exception = new ExceptionForumInfoNotExists(wrapIn.getForumId());
						result.error(exception);
					} else {
						uniCode = forumInfo.getId();
						wrapIn.setForumId(forumInfo.getId());
						wrapIn.setForumName(forumInfo.getForumName());
					}
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionRoleInfoProcess(e,
							"系统在根据ID获取BBS论坛分区信息时发生异常！ID:" + wrapIn.getForumId());
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			} else if ("版块角色".equals(wrapIn.getRoleType())) {
				try {
					sectionInfo = sectionInfoServiceAdv.get(wrapIn.getSectionId());
					if (sectionInfo == null) {
						check = false;
						Exception exception = new ExceptionSectionNotExists(wrapIn.getSectionId());
						result.error(exception);
					} else {
						uniCode = sectionInfo.getId();
						wrapIn.setSectionId(sectionInfo.getId());
						wrapIn.setSectionName(sectionInfo.getSectionName());
						wrapIn.setForumId(sectionInfo.getForumId());
						wrapIn.setForumName(sectionInfo.getForumName());
					}
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionRoleInfoProcess(e,
							"根据指定ID查询版块信息时发生异常.ID:" + wrapIn.getSectionId());
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}
		}
		if (check) {
			permissionCodes = wrapIn.getPermissionCodes();
			wrapIn.setCreatorName(effectivePerson.getDistinguishedName());
		}
		if (check) {
			if (wrapIn.getRoleCode() == null || wrapIn.getRoleCode().isEmpty()) {
				try {
					// 自动组织一个角色编码：BBS + 角色名称的拼音 + ID
					wrapIn.setRoleCode(
							"BBS_" + PinyinHelper.getShortPinyin(wrapIn.getRoleName()).toUpperCase() + "_" + uniCode);
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionRoleInfoProcess(e,
							"根据指定ID查询版块信息时发生异常.ID:" + wrapIn.getRoleName());
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}
		}
		if (check) {
			try {
				roleInfo = Wi.copier.copy(wrapIn);
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionRoleInfoProcess(e, "将用户传入的信息转换为一个角色信息对象时发生异常。");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		if (check) {
			if ( StringUtils.isNotEmpty( roleInfo.getRoleCode() )) {
				try {
					roleInfo_old = roleInfoService.getByRoleCode(roleInfo.getRoleCode());
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionRoleInfoProcess(e,
							"系统在根据编码获取BBS角色信息时发生异常！Code:" + roleInfo.getRoleCode());
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}
		}
		if (check) {
			try {
				roleInfo = roleInfoService.save(roleInfo, permissionCodes);

				wrap = new Wo();
				wrap.setId(roleInfo.getId());
				result.setData(wrap);

				if (roleInfo_old != null) {
					operationRecordService.roleOperation(effectivePerson.getDistinguishedName(), roleInfo, "MODIFY",
							hostIp, hostName);
				} else {
					operationRecordService.roleOperation(effectivePerson.getDistinguishedName(), roleInfo, "CREATE",
							hostIp, hostName);
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionRoleInfoProcess(e, "系统在保存BBS角色信息时发生异常.");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		return result;
	}

	public static class Wi extends BBSRoleInfo {

		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> Excludes = new ArrayList<String>();

		public static WrapCopier<Wi, BBSRoleInfo> copier = WrapCopierFactory.wi(Wi.class, BBSRoleInfo.class, null,
				JpaObject.FieldsUnmodify);

		private List<String> permissionCodes = null;

		public List<String> getPermissionCodes() {
			return permissionCodes;
		}

		public void setPermissionCodes(List<String> permissionCodes) {
			this.permissionCodes = permissionCodes;
		}
	}

	public static class Wo extends WoId {

	}
}