package com.x.base.core.project.schedule;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.connection.CipherConnectionAction;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class JobReportListener implements JobListener {

	private static Logger logger = LoggerFactory.getLogger(JobReportListener.class);
	@Override
	public String getName() {
		return this.getClass().getName();
	}

	@Override
	public void jobExecutionVetoed(JobExecutionContext jobExecutionContext) {

	}

	@Override
	public void jobToBeExecuted(JobExecutionContext jobExecutionContext) {

	}

	@Override
	public void jobWasExecuted(JobExecutionContext jobExecutionContext, JobExecutionException jobExecutionException) {
		ScheduleLogRequest request = new ScheduleLogRequest(jobExecutionContext, jobExecutionException);
		try {
			CipherConnectionAction.post(false, Config.url_x_program_center_jaxrs("schedule", "report"), request);
		} catch (Exception e) {
			logger.error(e);
		}
	}

}