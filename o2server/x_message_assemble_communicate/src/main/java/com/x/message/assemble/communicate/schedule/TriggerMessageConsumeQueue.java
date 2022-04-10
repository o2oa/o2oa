package com.x.message.assemble.communicate.schedule;

import org.quartz.JobExecutionContext;

import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.schedule.AbstractJob;
import com.x.message.assemble.communicate.ThisApplication;

public class TriggerMessageConsumeQueue extends AbstractJob {

	private static final Logger LOGGER = LoggerFactory.getLogger(TriggerMessageConsumeQueue.class);

	@Override
	public void schedule(JobExecutionContext jobExecutionContext) throws Exception {
		LOGGER.debug(
				"schedule trigger restfulConsumeQueue, mqConsumeQueue, mailConsumeQueue, apiConsumeQueue, jdbcConsumeQueue, tableConsumeQueue.");
		ThisApplication.restfulConsumeQueue.send(null);
		ThisApplication.mqConsumeQueue.send(null);
		ThisApplication.mailConsumeQueue.send(null);
		ThisApplication.apiConsumeQueue.send(null);
		ThisApplication.jdbcConsumeQueue.send(null);
		ThisApplication.tableConsumeQueue.send(null);
	}

}