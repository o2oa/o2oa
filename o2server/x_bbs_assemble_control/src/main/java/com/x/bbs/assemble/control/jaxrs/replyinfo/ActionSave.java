package com.x.bbs.assemble.control.jaxrs.replyinfo;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.bbs.assemble.control.jaxrs.replyinfo.exception.ExceptionForumInfoNotExists;
import com.x.bbs.assemble.control.jaxrs.replyinfo.exception.ExceptionForumInsufficientPermission;
import com.x.bbs.assemble.control.jaxrs.replyinfo.exception.ExceptionForumPermissionsCheck;
import com.x.bbs.assemble.control.jaxrs.replyinfo.exception.ExceptionReplyContentEmpty;
import com.x.bbs.assemble.control.jaxrs.replyinfo.exception.ExceptionReplyInfoProcess;
import com.x.bbs.assemble.control.jaxrs.replyinfo.exception.ExceptionReplySubjectIdEmpty;
import com.x.bbs.assemble.control.jaxrs.replyinfo.exception.ExceptionSectionInsufficientPermissions;
import com.x.bbs.assemble.control.jaxrs.replyinfo.exception.ExceptionSectionNotExists;
import com.x.bbs.assemble.control.jaxrs.replyinfo.exception.ExceptionSectionPermissionsCheck;
import com.x.bbs.assemble.control.jaxrs.replyinfo.exception.ExceptionSubjectLocked;
import com.x.bbs.assemble.control.jaxrs.replyinfo.exception.ExceptionSubjectNotExists;
import com.x.bbs.entity.BBSForumInfo;
import com.x.bbs.entity.BBSReplyInfo;
import com.x.bbs.entity.BBSSectionInfo;
import com.x.bbs.entity.BBSSubjectInfo;

public class ActionSave extends BaseAction {

	private static  Logger logger = LoggerFactory.getLogger(ActionSave.class);

	protected ActionResult<Wo> execute(HttpServletRequest request, EffectivePerson effectivePerson,
			JsonElement jsonElement) throws Exception {
		ActionResult<Wo> result = new ActionResult<>();
		BBSSubjectInfo subjectInfo = null;
		BBSReplyInfo replyInfo = null;
		BBSSectionInfo sectionInfo = null;
		BBSForumInfo forumInfo = null;
		Boolean hasPermission = false;
		Boolean check = true;
		String hostIp = request.getRemoteAddr();
		String hostName = request.getRemoteAddr();
		Wi wrapIn = null;

		try {
			wrapIn = this.convertToWrapIn(jsonElement, Wi.class);
		} catch (Exception e) {
			check = false;
			Exception exception = new ExceptionReplyInfoProcess(e,
					"系统在将JSON信息转换为对象时发生异常。JSON:" + jsonElement.toString());
			result.error(exception);
			logger.error(e, effectivePerson, request, null);
		}

		if (check) {
			wrapIn.setHostIp(request.getRemoteHost());
			if (wrapIn.getSubjectId() == null) {
				check = false;
				Exception exception = new ExceptionReplySubjectIdEmpty();
				result.error(exception);
			}
		}
		if (check) {
			if (wrapIn.getContent() == null) {
				check = false;
				Exception exception = new ExceptionReplyContentEmpty();
				result.error(exception);
			}
		}
		// 查询关联的主题信息是否存在
		if (check) {
			try {
				subjectInfo = subjectInfoService.get(wrapIn.getSubjectId());
				if (subjectInfo == null) {
					check = false;
					Exception exception = new ExceptionSubjectNotExists(wrapIn.getSubjectId());
					result.error(exception);
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionReplyInfoProcess(e, "根据指定ID查询主题信息时发生异常.ID:" + wrapIn.getSubjectId());
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}

		// 判断主题是否允许用户回复，已锁定的主题不允许用户进行回复
		if (check) {
			if ("已锁定".equals(subjectInfo.getSubjectStatus())) {
				check = false;
				Exception exception = new ExceptionSubjectLocked(wrapIn.getSubjectId());
				result.error(exception);
			}
		}

		// 判断主题所在的版块是否允许用户回复，或者用户是否有权限进行主题回复
		if (check) {
			try {
				sectionInfo = sectionInfoServiceAdv.get(subjectInfo.getSectionId());
				if (sectionInfo == null) {
					check = false;
					Exception exception = new ExceptionSectionNotExists(subjectInfo.getSectionId());
					result.error(exception);
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionReplyInfoProcess(e,
						"根据指定ID查询版块信息时发生异常.ID:" + subjectInfo.getSectionId());
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}

		// 开始判断用户是否有对版内的主题权限进行回复
		if (check) {
			if ("根据权限".equals(sectionInfo.getReplyPublishAble())) {
				try {
					hasPermission = UserPermissionService.hasPermission(effectivePerson.getDistinguishedName(),
							"SECTION_REPLY_PUBLISH_" + subjectInfo.getSectionId());
					if (!hasPermission) {
						check = false;
						Exception exception = new ExceptionSectionInsufficientPermissions(sectionInfo.getSectionName(),
								"SECTION_REPLY_PUBLISH");
						result.error(exception);
					}
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionSectionPermissionsCheck(e,
							effectivePerson.getDistinguishedName(), sectionInfo.getSectionName(),
							"SECTION_REPLY_PUBLISH");
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}
		}
		// 再查询用户是否有主版块的回复权限
		if ( subjectInfo != null && !subjectInfo.getMainSectionId().equals(subjectInfo.getSectionId())) {
			if (check) {
				try {
					sectionInfo = sectionInfoServiceAdv.get(subjectInfo.getMainSectionId());
					if (sectionInfo == null) {
						check = false;
						Exception exception = new ExceptionSectionNotExists(subjectInfo.getMainSectionId());
						result.error(exception);
					}
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionReplyInfoProcess(e,
							"根据指定ID查询版块信息时发生异常.ID:" + subjectInfo.getMainSectionId());
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}
			if (check) {
				if ("根据权限".equals(sectionInfo.getReplyPublishAble())) {
					// 那么要开始判断用户是否有对版内的主题权限进行回复
					try {
						hasPermission = UserPermissionService.hasPermission(effectivePerson.getDistinguishedName(),
								"SECTION_REPLY_PUBLISH_" + subjectInfo.getMainSectionId());
						if (!hasPermission) {
							check = false;
							Exception exception = new ExceptionSectionInsufficientPermissions( sectionInfo.getSectionName(), "SECTION_REPLY_PUBLISH");
							result.error(exception);
						}
					} catch (Exception e) {
						check = false;
						Exception exception = new ExceptionSectionPermissionsCheck(e,
								effectivePerson.getDistinguishedName(), sectionInfo.getSectionName(), "SECTION_REPLY_PUBLISH");
						result.error(exception);
						logger.error(e, effectivePerson, request, null);
					}
				}
			}
		}
		// 判断主题所在的论坛是否允许用户回复，或者用户是否有权限进行主题回复
		if (check) {
			try {
				forumInfo = forumInfoServiceAdv.get(subjectInfo.getForumId());
				if (forumInfo == null) {
					check = false;
					Exception exception = new ExceptionForumInfoNotExists(subjectInfo.getForumId());
					result.error(exception);
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionReplyInfoProcess(e,
						"系统在根据ID获取BBS论坛分区信息时发生异常！ID:" + subjectInfo.getForumId());
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		if (check) {
			if ("根据权限".equals(forumInfo.getReplyPublishAble())) {
				// 那么要开始判断用户是否有对论坛分区的主题权限进行回复
				try {
					hasPermission = UserPermissionService.hasPermission(effectivePerson.getDistinguishedName(),
							"FORUM_REPLY_PUBLISH_" + subjectInfo.getForumId());
					if (!hasPermission) {
						check = false;
						Exception exception = new ExceptionForumInsufficientPermission(subjectInfo.getForumName(),
								"FORUM_REPLY_PUBLISH");
						result.error(exception);
					}
				} catch (Exception e) {
					check = false;
					Exception exception = new ExceptionForumPermissionsCheck(e, effectivePerson.getDistinguishedName(), subjectInfo.getForumName(), "FORUM_REPLY_PUBLISH");
					result.error(exception);
					logger.error(e, effectivePerson, request, null);
				}
			}
		}

		// 权限判断完成，开始保存回复内容
		if (check) {
			wrapIn.setForumId(subjectInfo.getForumId());
			wrapIn.setForumName(subjectInfo.getForumName());
			wrapIn.setMainSectionId(subjectInfo.getMainSectionId());
			wrapIn.setMainSectionName(subjectInfo.getMainSectionName());
			wrapIn.setSectionId(subjectInfo.getSectionId());
			wrapIn.setSectionName(subjectInfo.getSectionName());
			wrapIn.setCreatorName(effectivePerson.getDistinguishedName());
		}

		if (check) {
			if ( wrapIn.getTitle() == null || wrapIn.getTitle().isEmpty()) {
				if ( StringUtils.isNotEmpty( subjectInfo.getTitle() )) {
					wrapIn.setTitle(subjectInfo.getTitle());
				} else {
					wrapIn.setTitle("无标题");
				}
			}
		}
		if (check) {
			try {
				replyInfo = Wi.copier.copy(wrapIn);
				if ( StringUtils.isNotEmpty( wrapIn.getId() )) {
					replyInfo.setId(wrapIn.getId());
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionReplyInfoProcess(e, "将用户传入的信息转换为一个回复信息对象时发生异常。");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		if (check) {
			try {
				replyInfo.setMachineName(wrapIn.getReplyMachineName());
				replyInfo.setSystemType(wrapIn.getReplySystemName());
				replyInfo = replyInfoService.save(replyInfo);

				Wo wo = new Wo();
				wo.setId(replyInfo.getId());
				result.setData(wo);
				
				ApplicationCache.notify( BBSReplyInfo.class );
				ApplicationCache.notify( BBSForumInfo.class );
				ApplicationCache.notify( BBSSectionInfo.class );
				ApplicationCache.notify( BBSSubjectInfo.class );
				
				operationRecordService.replyOperation(effectivePerson.getDistinguishedName(), replyInfo, "CREATE", hostIp, hostName);
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionReplyInfoProcess(e, "系统在保存回复信息时发生异常。");
				result.error(exception);
				logger.error(e, effectivePerson, request, null);
			}
		}
		return result;
	}

	public static class Wi extends BBSReplyInfo {

		private static final long serialVersionUID = -5076990764713538973L;

		public static List<String> Excludes = new ArrayList<String>();

		public static WrapCopier<Wi, BBSReplyInfo> copier = WrapCopierFactory.wi(Wi.class, BBSReplyInfo.class, null,
				JpaObject.FieldsUnmodify);

		private String replyMachineName = "PC";

		private String replySystemName = "Windows";

		private String userHostIp = "";

		public String getReplyMachineName() {
			return replyMachineName;
		}

		public void setReplyMachineName(String replyMachineName) {
			this.replyMachineName = replyMachineName;
		}

		public String getReplySystemName() {
			return replySystemName;
		}

		public void setReplySystemName(String replySystemName) {
			this.replySystemName = replySystemName;
		}

		public String getUserHostIp() {
			return userHostIp;
		}

		public void setUserHostIp(String userHostIp) {
			this.userHostIp = userHostIp;
		}

	}

	public static class Wo extends WoId {

	}
}