package com.x.okr.assemble.control.schedule;

import org.quartz.JobExecutionContext;

import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.schedule.AbstractJob;
import com.x.okr.assemble.control.service.OkrSystemIdentityOperatorService;

/**
 * 定时代理，定时对系统中涉及到的身份信息进行检查
 * 
 * @author LIYI
 *
 */
public class ErrorIdentityCheckTask extends AbstractJob {
	private static Logger logger = LoggerFactory.getLogger(ErrorIdentityCheckTask.class);
	private OkrSystemIdentityOperatorService okrSystemIdentityOperatorService = new OkrSystemIdentityOperatorService();

	@Override
	public void schedule(JobExecutionContext jobExecutionContext) throws Exception {
		try {
			okrSystemIdentityOperatorService.checkAllAbnormalIdentityInSystem();
			logger.info("Timertask_ErrorIdentityCheckTask completed and excute success.");
		} catch (Exception e) {
			logger.warn("Timertask_ErrorIdentityCheckTask excute got an exception.");
			logger.error(e);
		}

	}
}