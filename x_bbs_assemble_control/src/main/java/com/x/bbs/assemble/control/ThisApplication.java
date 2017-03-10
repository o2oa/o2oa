package com.x.bbs.assemble.control;

import java.util.List;

import com.x.base.core.logger.LoggerFactory;
import com.x.base.core.project.AbstractThisApplication;
import com.x.base.core.project.ReportTask;
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
import com.x.collaboration.core.message.Collaboration;

public class ThisApplication extends AbstractThisApplication {
	private static Boolean subjectReplyTotalStatisticTaskRunning = false;
	private static Boolean subjectTotalStatisticTaskRunning = false;
	private static Boolean userSubjectReplyStatisticTaskRunning = false;

	public static void init() throws Exception {
		timerWithFixedDelay(new ReportTask(), 1, 20);
		initDatasFromCenters();
		initStoragesFromCenters();
		Collaboration.start();
		initAllSystemConfig();
		initAllTimerTask();
	}

	public static void destroy() throws Exception {
		Collaboration.stop();
	}

	private static void initAllTimerTask() throws Exception {
		timerWithFixedDelay(new SubjectTotalStatisticTask(), 60 * 20, 60 * 60);
		timerWithFixedDelay(new SubjectReplyTotalStatisticTask(), 60 * 30, 60 * 60);
		timerWithFixedDelay(new UserSubjectReplyPermissionStatisticTask(), 60 * 10, 60 * 60 * 60);
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

	public static Boolean getSubjectTotalStatisticTaskRunning() {
		return subjectTotalStatisticTaskRunning;
	}

	public static void setSubjectTotalStatisticTaskRunning(Boolean subjectTotalStatisticTaskRunning) {
		ThisApplication.subjectTotalStatisticTaskRunning = subjectTotalStatisticTaskRunning;
	}

	public static Boolean getSubjectReplyTotalStatisticTaskRunning() {
		return subjectReplyTotalStatisticTaskRunning;
	}

	public static void setSubjectReplyTotalStatisticTaskRunning(Boolean subjectReplyTotalStatisticTaskRunning) {
		ThisApplication.subjectReplyTotalStatisticTaskRunning = subjectReplyTotalStatisticTaskRunning;
	}

	public static Boolean getUserSubjectReplyStatisticTaskRunning() {
		return userSubjectReplyStatisticTaskRunning;
	}

	public static void setUserSubjectReplyStatisticTaskRunning(Boolean userSubjectReplyStatisticTaskRunning) {
		ThisApplication.userSubjectReplyStatisticTaskRunning = userSubjectReplyStatisticTaskRunning;
	}
}
