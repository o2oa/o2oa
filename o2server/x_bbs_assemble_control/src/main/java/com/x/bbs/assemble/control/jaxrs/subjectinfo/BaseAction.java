package com.x.bbs.assemble.control.jaxrs.subjectinfo;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.project.cache.Cache;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.ExceptionPublicSectionFilter;
import com.x.bbs.assemble.control.jaxrs.subjectinfo.exception.ExceptionSubjectInfoProcess;
import com.x.bbs.assemble.control.service.BBSConfigSettingService;
import com.x.bbs.assemble.control.service.BBSForumInfoServiceAdv;
import com.x.bbs.assemble.control.service.BBSOperationRecordService;
import com.x.bbs.assemble.control.service.BBSReplyInfoService;
import com.x.bbs.assemble.control.service.BBSSectionInfoServiceAdv;
import com.x.bbs.assemble.control.service.BBSSubjectInfoServiceAdv;
import com.x.bbs.assemble.control.service.BBSSubjectVoteService;
import com.x.bbs.assemble.control.service.UserManagerService;
import com.x.bbs.assemble.control.service.UserPermissionService;
import com.x.bbs.entity.BBSPermissionInfo;
import com.x.bbs.entity.BBSSectionInfo;
import com.x.bbs.entity.BBSSubjectAttachment;
import com.x.bbs.entity.BBSSubjectInfo;

public class BaseAction extends StandardJaxrsAction{

	private static  Logger logger = LoggerFactory.getLogger(BaseAction.class);
	protected Cache.CacheCategory cacheCategory = new Cache.CacheCategory(BBSSubjectInfo.class);

	protected UserPermissionService UserPermissionService = new UserPermissionService();
	protected BBSReplyInfoService replyInfoService = new BBSReplyInfoService();
	protected BBSSectionInfoServiceAdv sectionInfoServiceAdv = new BBSSectionInfoServiceAdv();
	protected BBSSubjectInfoServiceAdv subjectInfoServiceAdv = new BBSSubjectInfoServiceAdv();
	protected BBSSubjectVoteService subjectVoteService = new BBSSubjectVoteService();
	protected BBSForumInfoServiceAdv forumInfoServiceAdv = new BBSForumInfoServiceAdv();
	protected UserManagerService userManagerService = new UserManagerService();
	protected BBSOperationRecordService operationRecordService = new BBSOperationRecordService();
	protected BBSConfigSettingService configSettingService = new BBSConfigSettingService();

	protected boolean isImage(BBSSubjectAttachment fileInfo) {
		if (fileInfo == null || fileInfo.getExtension() == null || fileInfo.getExtension().isEmpty()) {
			return false;
		}
		if ("jpg".equalsIgnoreCase(fileInfo.getExtension())) {
			return true;
		} else if ("png".equalsIgnoreCase(fileInfo.getExtension())) {
			return true;
		} else if ("jpeg".equalsIgnoreCase(fileInfo.getExtension())) {
			return true;
		} else if ("tiff".equalsIgnoreCase(fileInfo.getExtension())) {
			return true;
		} else if ("gif".equalsIgnoreCase(fileInfo.getExtension())) {
			return true;
		} else if ("bmp".equalsIgnoreCase(fileInfo.getExtension())) {
			return true;
		}
		return false;
	}

	/**
	 * 获取用户可访问的所有版块ID列表
	 *
	 * @param request
	 * @param currentPerson
	 * @return
	 * @throws Exception
	 */
	protected List<String> getViewableSectionIds(HttpServletRequest request, EffectivePerson currentPerson) throws Exception {
		List<BBSSectionInfo> sectionInfoList = null;
		List<BBSSectionInfo> subSectionInfoList = null;
		List<BBSPermissionInfo> permissonList = null;
		List<String> publicForumIds = null;
		List<String> publicSectionIds = null;
		List<String> viewforumIds = new ArrayList<String>();
		List<String> viewSectionIds = new ArrayList<String>();
		Boolean check = true;

		if (check) {
			permissonList = UserPermissionService.getUserPermissionInfoList(currentPerson.getDistinguishedName());
		}
		if (check) {
			if (permissonList != null) {
				for (BBSPermissionInfo permissionInfo : permissonList) {
					if ("FORUM_VIEW".equalsIgnoreCase(permissionInfo.getPermissionFunction())
							&& !viewforumIds.contains(permissionInfo.getForumId())) {
						viewforumIds.add(permissionInfo.getForumId());
					}
					if ("SECTION_VIEW".equalsIgnoreCase(permissionInfo.getPermissionFunction())
							&& !viewSectionIds.contains(permissionInfo.getSectionId())) {
						viewSectionIds.add(permissionInfo.getSectionId());
					}
				}
			}
		}
		if (check) {
			try {
				publicForumIds = forumInfoServiceAdv.listAllPublicForumIds();
				if (publicForumIds != null) {
					for (String _id : publicForumIds) {
						if (!viewforumIds.contains(_id)) {
							viewforumIds.add(_id);
						}
					}
				}
			} catch (Exception e) {
				check = false;
				logger.warn("system query all public forum got an exceptin.", e);
			}
		}
		if (check) {
			try {
				publicSectionIds = sectionInfoServiceAdv.viewSectionByForumIds(viewforumIds, true);
			} catch (Exception e) {
				check = false;
				logger.warn("system query all public section with forumIds got an exceptin.");
				Exception exception = new ExceptionPublicSectionFilter(e);
				logger.error(e, currentPerson, request, null);
				throw exception;
			}
		}

		if (check) {
			try {
				sectionInfoList = sectionInfoServiceAdv.list(publicSectionIds);
				if (sectionInfoList != null) {
					for (BBSSectionInfo _sectionInfo : sectionInfoList) {
						if (!viewSectionIds.contains(_sectionInfo.getId())) {
							viewSectionIds.add(_sectionInfo.getId());
						}
						if ("主板块".equals(_sectionInfo.getSectionLevel())) {
							subSectionInfoList = sectionInfoServiceAdv
									.listSubSectionByMainSectionId(_sectionInfo.getId());
							if (subSectionInfoList != null) {
								for (BBSSectionInfo _subSectionInfo : subSectionInfoList) {
									if (!viewSectionIds.contains(_subSectionInfo.getId())) {
										viewSectionIds.add(_subSectionInfo.getId());
									}
								}
							}
						}
					}
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new ExceptionSubjectInfoProcess(e, "根据指定ID列表查询版块信息时发生异常.");
				logger.error(e, currentPerson, request, null);
				throw exception;
			}
		}
		return viewSectionIds;
	}
}
