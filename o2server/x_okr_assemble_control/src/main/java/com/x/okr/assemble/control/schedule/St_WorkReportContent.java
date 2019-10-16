package com.x.okr.assemble.control.schedule;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.schedule.AbstractJob;
import com.x.okr.assemble.control.service.ExcuteSt_WorkReportContentService;

/**
 * 定时代理，对工作的汇报情况进行统计分析。 1、遍历所有未归档的工作，以工作为记录维度 ,有多少工作就有多少条记录 2、分析当前这一周的工作汇报情况
 * 
 * @author LIYI
 *
 */
public class St_WorkReportContent extends AbstractJob {

	private static Logger logger = LoggerFactory.getLogger(St_WorkReportContent.class);

	@Override
	public void schedule(JobExecutionContext jobExecutionContext) throws Exception {
		try {
			new ExcuteSt_WorkReportContentService().execute();
			logger.info("Timertask OKR_St_WorkReportContent completed and excute success.");
		} catch (Exception e) {
			logger.error(e);
			throw new JobExecutionException(e);
		}
	}

}