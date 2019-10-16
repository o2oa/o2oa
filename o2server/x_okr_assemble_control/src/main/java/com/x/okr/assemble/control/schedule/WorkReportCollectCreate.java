package com.x.okr.assemble.control.schedule;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.schedule.AbstractJob;
import com.x.okr.assemble.control.service.OkrWorkReportTaskCollectService;

/**
 * 定时代理，定时分析所有员工的工作汇报汇总待办是否正常
 * 
 * @author LIYI
 *
 */
public class WorkReportCollectCreate extends AbstractJob {

	private static Logger logger = LoggerFactory.getLogger(WorkReportCollectCreate.class);
	private OkrWorkReportTaskCollectService okrWorkReportTaskCollectService = new OkrWorkReportTaskCollectService();

	@Override
	public void schedule(JobExecutionContext jobExecutionContext) throws Exception {

		// 调用服务去核对汇总待办
		try {
			okrWorkReportTaskCollectService.checkAllReportCollectTask();
			logger.info("Timertask OKR_WorkReportCollectCreate completed and excute success.");
		} catch (Exception e) {
			logger.warn("系统核对工作汇报待办汇总发生异常。");
			logger.error(e);
			throw new JobExecutionException(e);
		}
	}
}