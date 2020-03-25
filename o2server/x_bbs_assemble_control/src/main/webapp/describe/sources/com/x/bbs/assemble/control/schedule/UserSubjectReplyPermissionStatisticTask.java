package com.x.bbs.assemble.control.schedule;

import java.util.List;

import org.quartz.JobExecutionContext;

import com.google.gson.Gson;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.schedule.AbstractJob;
import com.x.base.core.project.tools.ListTools;
import com.x.bbs.assemble.control.service.BBSOperationRecordService;
import com.x.bbs.assemble.control.service.BBSPermissionInfoService;
import com.x.bbs.assemble.control.service.BBSReplyInfoService;
import com.x.bbs.assemble.control.service.BBSRoleInfoService;
import com.x.bbs.assemble.control.service.BBSSubjectInfoService;
import com.x.bbs.assemble.control.service.BBSUserInfoService;
import com.x.bbs.assemble.control.service.bean.RoleAndPermission;

/**
 * 定时代理，对所有用户的发贴，回贴量以及权限进行分析统计并且记录。
 * 
 * @author LIYI
 *
 */
public class UserSubjectReplyPermissionStatisticTask extends AbstractJob {

	private Logger logger = LoggerFactory.getLogger(UserSubjectReplyPermissionStatisticTask.class);

	private BBSUserInfoService userInfoService = new BBSUserInfoService();
	private BBSSubjectInfoService subjectInfoService = new BBSSubjectInfoService();
	private BBSReplyInfoService replyInfoService = new BBSReplyInfoService();
	private BBSOperationRecordService operationRecordService = new BBSOperationRecordService();
	private BBSPermissionInfoService permissionInfoService = new BBSPermissionInfoService();
	private BBSRoleInfoService roleInfoService = new BBSRoleInfoService();

	@Override
	public void schedule(JobExecutionContext jobExecutionContext) throws Exception {
		process();
		logger.info("Timertask[UserSubjectReplyStatisticTask] completed and excute success.");
	}

	/**
	 * 执行定时代理的业务逻辑
	 */
	private void process() {
		List<String> operationUserNames = null;
		// 1、在BBS_OPERATIONRECORD表里查询参与BBS系统的所有人员姓名列表
		try {
			operationUserNames = operationRecordService.distinctAllOperationUserNames();
		} catch (Exception e) {
			logger.warn("system distinct all operation user names got an exception.");
			logger.error(e);
		}
		if ( ListTools.isNotEmpty( operationUserNames )) {
			// 2、遍历所有的人员，分别进行统计
			operationUserNames.forEach(u -> {
				statisticSubjectReplyCountForUser(u);
			});
		}
	}

	private void statisticSubjectReplyCountForUser(String userName) {
		if (userName == null || userName.isEmpty()) {
			return;
		}
		Gson gson = null;
		RoleAndPermission roleAndPermission = null;
		List<String> roleCodes = null;
		List<String> permissionCodes = null;
		Long subjectCount = 0L; // 人员发贴量
		Long replyCount = 0L; // 人员回贴量
		Long subjectCountToday = 0L; // 今日发贴量
		Long replyCountToday = 0L;// 今日回贴量
		Long creamCount = 0L;// 精华贴数量
		Long originalCount = 0L;// 原创贴数量
		Long fansCount = 0L; // 粉丝数
		Long popularity = 0L; // 人气
		Long credit = 0L;// 积分
		String permissionContent = ""; // 权限角色内容

		// 统计人员发贴量
		try {
			subjectCount = subjectInfoService.countSubjectByUserName(userName);
		} catch (Exception e) {
			logger.warn("system count subject total by user name got an exception.");
			logger.error(e);
		}

		// 人员回贴量
		try {
			replyCount = replyInfoService.countReplyByUserName(userName);
		} catch (Exception e) {
			logger.warn("system count reply total by user name got an exception.");
			logger.error(e);
		}

		// 今日发贴量
		try {
			subjectCountToday = subjectInfoService.countSubjectForTodayByUserName(userName);
		} catch (Exception e) {
			logger.warn("system count today subject total by user name got an exception.");
			logger.error(e);
		}

		// 今日回贴量
		try {
			replyCountToday = replyInfoService.countReplyForTodayByUserName(userName);
		} catch (Exception e) {
			logger.warn("system count today reply total by user name got an exception.");
			logger.error(e);
		}

		// 精华贴数量
		try {
			creamCount = subjectInfoService.countCreamSubjectByUserName(userName);
		} catch (Exception e) {
			logger.warn("system count cream subject total by user name got an exception.");
			logger.error(e);
		}

		// 原创贴数量
		try {
			originalCount = subjectInfoService.countOriginalSubjectByUserName(userName);
		} catch (Exception e) {
			logger.warn("system count original subject total by user name got an exception.");
			logger.error(e);
		}

		// 粉丝数
		// 人气
		// 积分
		// 权限角色内容
		gson = XGsonBuilder.instance();
		roleAndPermission = new RoleAndPermission();
		roleAndPermission.setPerson(userName);
		try {
			roleCodes = roleInfoService.listAllRoleCodesForUser(userName);
			roleAndPermission.setRoleInfoList(roleCodes);
		} catch (Exception e) {
			logger.warn("system list all role for user got an exception.");
			logger.error(e);
		}
		if ( ListTools.isNotEmpty( roleCodes )) {
			try {
				permissionCodes = permissionInfoService.listPermissionCodesByRoleCodes(roleCodes);
				roleAndPermission.setPermissionInfoList(permissionCodes);
			} catch (Exception e) {
				logger.warn("system list all permission for user got an exception.");
				logger.error(e);
			}
		}
		try {
			permissionContent = gson.toJson(roleAndPermission);
		} catch (Exception e) {
			logger.warn("system translate object to json got an exception.");
			logger.error(e);
		}
		// 从数据库中查询出人员信息，进行信息更新
		try {
			userInfoService.save(userName, subjectCount, replyCount, subjectCountToday, replyCountToday, creamCount,
					originalCount, fansCount, popularity, credit, permissionContent);
		} catch (Exception e) {
			logger.warn("system save user info got an exception. username:" + userName);
			logger.error(e);
		}
	}

}