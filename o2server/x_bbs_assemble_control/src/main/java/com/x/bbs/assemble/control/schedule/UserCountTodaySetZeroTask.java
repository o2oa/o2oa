package com.x.bbs.assemble.control.schedule;

import java.util.List;

import org.quartz.JobExecutionContext;

import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.schedule.AbstractJob;
import com.x.base.core.project.tools.ListTools;
import com.x.bbs.assemble.control.service.BBSOperationRecordService;
import com.x.bbs.assemble.control.service.BBSUserInfoService;
import com.x.bbs.entity.BBSUserInfo;

/**
 * 定时代理，对所有用户的今日回复、今日主题 设置为0。
 * 
 * @author LIYI
 *
 */
public class UserCountTodaySetZeroTask extends AbstractJob {

	private Logger logger = LoggerFactory.getLogger(UserCountTodaySetZeroTask.class);

	private BBSUserInfoService userInfoService = new BBSUserInfoService();
	private BBSOperationRecordService operationRecordService = new BBSOperationRecordService();

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
				userCountTodaySetZero(u);
			});
		}
	}

	private void userCountTodaySetZero(String userName) {
		if (userName == null || userName.isEmpty()) {
			return;
		}
		//个人回复数增加1
		BBSUserInfo userInfo;
		try {
			userInfo = userInfoService.getByUserName(userName);
			userInfo.setReplyCountToday((long)0);
			userInfo.setSubjectCountToday((long)0);
			userInfoService.save(userInfo);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.warn("system distinct all operation user names got an exception.");
			logger.error(e);
		}
	
	}

}