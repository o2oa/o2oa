package com.x.bbs.assemble.control.schedule;

import java.util.List;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.bbs.assemble.control.service.BBSForumInfoServiceAdv;
import com.x.bbs.assemble.control.service.BBSForumSubjectStatisticService;
import com.x.bbs.entity.BBSForumInfo;

/**
 * 定时代理，对所有的论坛以及版块进行主题和回贴数、参与人数的统计。 1、遍历所有论坛 2、对每一个论坛，每一个主版块以及子版块进行数据统计
 * 
 * @author LIYI
 *
 */
public class SubjectTotalStatisticTask implements Job {

	private Logger logger = LoggerFactory.getLogger(SubjectTotalStatisticTask.class);
	private BBSForumInfoServiceAdv forumInfoServiceAdv = new BBSForumInfoServiceAdv();
	private BBSForumSubjectStatisticService forumSubjectStatisticService = new BBSForumSubjectStatisticService();

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		List<BBSForumInfo> forumInfoList = null;

		try {
			forumInfoList = forumInfoServiceAdv.listAll();
			if ( ListTools.isNotEmpty( forumInfoList )) {
				forumSubjectStatisticService.statisticSubjectTotalAndReplayTotalForForum(forumInfoList);
			}
			logger.info("Timertask[SubjectReplyTotalStatisticTask] completed and excute success.");
		} catch (Exception e) {
			logger.warn("SubjectTotalStatisticTask got an exception.");
			logger.error(e);
		}
	}
}