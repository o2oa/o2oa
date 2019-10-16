package com.x.program.center.schedule;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.program.center.ThisApplication;

public class QiyeweixinSyncOrganizationTrigger extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(QiyeweixinSyncOrganizationTrigger.class);

	/* 向列表发送一个同步信号 */
	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		try {
			if (pirmaryCenter()) {
				ThisApplication.dingdingSyncOrganizationCallbackRequest.add(new Object());
			}
		} catch (Exception e) {
			logger.error(e);
			throw new JobExecutionException(e);
		}
	}

}
