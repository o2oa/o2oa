package com.x.processplatform.service.processing.schedule;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.schedule.AbstractJob;
import com.x.processplatform.service.processing.ThisApplication;

public class UpdateTable extends AbstractJob {

	private static final Logger LOGGER = LoggerFactory.getLogger(UpdateTable.class);

	@Override
	public void schedule(JobExecutionContext jobExecutionContext) throws Exception {
		LOGGER.debug("send updateTableQueue signal.");
		try {
			ThisApplication.updateTableQueue.send("");
		} catch (Exception e) {
			throw new JobExecutionException(e);
		}
	}

}