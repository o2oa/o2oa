package com.x.bbs.assemble.control.timertask;

import java.util.List;
import java.util.TimerTask;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.bbs.assemble.control.ThisApplication;
import com.x.bbs.assemble.control.service.BBSForumInfoServiceAdv;
import com.x.bbs.assemble.control.service.BBSForumSubjectStatisticService;
import com.x.bbs.entity.BBSForumInfo;

/**
 * 定时代理，对所有的论坛以及版块进行主题和回贴数、参与人数的统计。 1、遍历所有论坛 2、对每一个论坛，每一个主版块以及子版块进行数据统计
 * 
 * @author LIYI
 *
 */
public class SubjectTotalStatisticTask extends TimerTask {

	private Logger logger = LoggerFactory.getLogger(SubjectTotalStatisticTask.class);
	private BBSForumInfoServiceAdv forumInfoServiceAdv = new BBSForumInfoServiceAdv();
	private BBSForumSubjectStatisticService forumSubjectStatisticService = new BBSForumSubjectStatisticService();

	public void run() {

		if (ThisApplication.getSubjectReplyTotalStatisticTaskRunning()) {
			logger.info("Timertask[SubjectReplyTotalStatisticTask] service is running, wait for next time......");
			return;
		}

		ThisApplication.setSubjectReplyTotalStatisticTaskRunning(true);

		List<BBSForumInfo> forumInfoList = null;

		try {
			forumInfoList = forumInfoServiceAdv.listAll();
			if (forumInfoList != null && !forumInfoList.isEmpty()) {
				forumSubjectStatisticService.statisticSubjectTotalAndReplayTotalForForum(forumInfoList);
			}
		} catch (Exception e) {
			logger.warn("SubjectTotalStatisticTask got an exception." );
			logger.error(e);
		}

		ThisApplication.setSubjectReplyTotalStatisticTaskRunning(false);
		logger.info("Timertask[SubjectReplyTotalStatisticTask] completed and excute success.");
	}
}