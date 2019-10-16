package com.x.okr.assemble.control.schedule;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.schedule.AbstractJob;
import com.x.okr.assemble.control.service.ExcuteSt_WorkReportStatusService;

/**
 * 定时代理，对工作的汇报提交情况进行统计分析
 * 
 * 1、遍历所有未归档的工作 2、分析从工作开始日期到工作结束日期之间 所有周的工作汇报提交情况
 * 
 * @author LIYI
 *
 */
public class St_WorkReportStatus extends AbstractJob {

	private static Logger logger = LoggerFactory.getLogger(St_WorkReportContent.class);

	@Override
	public void schedule(JobExecutionContext jobExecutionContext) throws Exception {
		try {
			new ExcuteSt_WorkReportStatusService().execute();
			logger.info("Timertask OKR_St_WorkReportStatus completed and excute success.");
		} catch (Exception e) {
			logger.error(e);
			throw new JobExecutionException(e);
		}
	}

}