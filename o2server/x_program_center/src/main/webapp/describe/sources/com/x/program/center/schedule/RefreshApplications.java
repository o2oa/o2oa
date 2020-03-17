package com.x.program.center.schedule;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.program.center.CenterQueueRefreshBody;
import com.x.program.center.ThisApplication;

public class RefreshApplications extends BaseAction {

	private static Logger logger = LoggerFactory.getLogger(RefreshApplications.class);

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		try {
			if (pirmaryCenter()) {
				CenterQueueRefreshBody body = new CenterQueueRefreshBody();
				ThisApplication.centerQueue.send(body);
			}
		} catch (Exception e) {
			logger.error(e);
			throw new JobExecutionException(e);
		}
	}

}