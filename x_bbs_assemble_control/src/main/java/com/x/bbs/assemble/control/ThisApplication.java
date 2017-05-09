package com.x.bbs.assemble.control;

import java.util.List;

import com.x.base.core.logger.LoggerFactory;
import com.x.base.core.project.Context;
import com.x.bbs.assemble.control.service.BBSConfigSettingService;
import com.x.bbs.assemble.control.service.BBSForumInfoServiceAdv;
import com.x.bbs.assemble.control.service.BBSPermissionInfoService;
import com.x.bbs.assemble.control.service.BBSRoleInfoService;
import com.x.bbs.assemble.control.service.BBSSectionInfoServiceAdv;
import com.x.bbs.assemble.control.timertask.SubjectReplyTotalStatisticTask;
import com.x.bbs.assemble.control.timertask.SubjectTotalStatisticTask;
import com.x.bbs.assemble.control.timertask.UserSubjectReplyPermissionStatisticTask;
import com.x.bbs.entity.BBSForumInfo;
import com.x.bbs.entity.BBSSectionInfo;

public class ThisApplication {
	
	protected static Context context;
	
	public static Context context() {
		return context;
	}

	public static void init() throws Exception {
		try {
			initAllSystemConfig();
			context().schedule( SubjectTotalStatisticTask.class, "0 10 * * * ?");
			context().schedule( SubjectReplyTotalStatisticTask.class, "0 40 * * * ?");
			context().schedule( UserSubjectReplyPermissionStatisticTask.class, "0 0/30 * * * ?");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void destroy() {
		try {
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void initAllSystemConfig() {
		BBSPermissionInfoService permissionInfoService = new BBSPermissionInfoService();
		BBSRoleInfoService roleInfoService = new BBSRoleInfoService();
		BBSForumInfoServiceAdv forumInfoServiceAdv = new BBSForumInfoServiceAdv();
		BBSSectionInfoServiceAdv sectionInfoServiceAdv = new BBSSectionInfoServiceAdv();
		BBSConfigSettingService configSettingService = new BBSConfigSettingService();
		List<BBSForumInfo> forumInfoList = null;
		List<BBSSectionInfo> sectionInfoList = null;

		try {
			configSettingService.initAllSystemConfig();
		} catch (Exception e) {
			LoggerFactory.getLogger(ThisApplication.class)
					.warn("BBS system check all config setting got an exception.");
			LoggerFactory.getLogger(ThisApplication.class).error(e);
		}

		try {
			forumInfoList = forumInfoServiceAdv.listAll();
			if (forumInfoList != null) {
				for (BBSForumInfo forumInfo : forumInfoList) {
					permissionInfoService.createForumPermission(forumInfo);
					roleInfoService.createForumRole(forumInfo);
				}
			}
		} catch (Exception e) {
			LoggerFactory.getLogger(ThisApplication.class)
					.warn("BBS system check all forum permission and role got an exception.");
			LoggerFactory.getLogger(ThisApplication.class).error(e);
		}

		try {
			sectionInfoList = sectionInfoServiceAdv.listAll();
			if (sectionInfoList != null) {
				for (BBSSectionInfo sectionInfo : sectionInfoList) {
					permissionInfoService.createSectionPermission(sectionInfo);
					roleInfoService.createSectionRole(sectionInfo);
				}
			}
		} catch (Exception e) {
			LoggerFactory.getLogger(ThisApplication.class)
					.warn("BBS system check all section permission and role got an exception.");
			LoggerFactory.getLogger(ThisApplication.class).error(e);
		}
	}
}
