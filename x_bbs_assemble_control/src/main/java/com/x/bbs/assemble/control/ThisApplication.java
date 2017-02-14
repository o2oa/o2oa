package com.x.bbs.assemble.control;

import java.util.List;

import org.slf4j.LoggerFactory;

import com.x.base.core.application.task.ReportTask;
import com.x.base.core.project.AbstractThisApplication;
import com.x.base.core.project.server.Config;
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
		scheduleWithFixedDelay( new ReportTask(), 1, 20 );
		initDatasFromCenters();
		initStoragesFromCenters();
		Config.workTimeConfig().initWorkTime();
		Collaboration.start();
		initAllSystemConfig();
		initAllTimerTask();
	}
	
	public static void destroy() throws Exception {
		Collaboration.stop();
	}
	
	private static void initAllTimerTask() {
		scheduleWithFixedDelay( new SubjectTotalStatisticTask(), 60 * 5, 60 * 30 );
		scheduleWithFixedDelay( new SubjectReplyTotalStatisticTask(), 60 * 3, 60 * 60 );
		scheduleWithFixedDelay( new UserSubjectReplyPermissionStatisticTask(), 60 * 1, 60 * 60 * 2 );
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
			LoggerFactory.getLogger( ThisApplication.class ).error( "BBS system check all config setting got an exception.", e );
		}
		
		try {
			forumInfoList = forumInfoServiceAdv.listAll();
			if( forumInfoList != null ){
				for( BBSForumInfo forumInfo : forumInfoList ){
					permissionInfoService.createForumPermission( forumInfo );
					roleInfoService.createForumRole( forumInfo );
				}
			}
		} catch (Exception e) {
			LoggerFactory.getLogger( ThisApplication.class ).error( "BBS system check all forum permission and role got an exception.", e );
		}
		
		try {
			sectionInfoList = sectionInfoServiceAdv.listAll();
			if( sectionInfoList != null ){
				for( BBSSectionInfo sectionInfo : sectionInfoList ){
					permissionInfoService.createSectionPermission( sectionInfo );
					roleInfoService.createSectionRole( sectionInfo );
				}
			}
		} catch (Exception e) {
			LoggerFactory.getLogger( ThisApplication.class ).error( "BBS system check all section permission and role got an exception.", e );
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
