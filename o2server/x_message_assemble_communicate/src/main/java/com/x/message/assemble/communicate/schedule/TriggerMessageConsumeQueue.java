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
				"schedule trigger kafkaConsumeQueue, activeMqConsumeQueue, restfulConsumeQueue, mailConsumeQueue, apiConsumeQueue, jdbcConsumeQueue, tableConsumeQueue, hadoopConsumeQueue.");
		ThisApplication.kafkaConsumeQueue.send(null);
		ThisApplication.activemqConsumeQueue.send(null);
		ThisApplication.restfulConsumeQueue.send(null);
		ThisApplication.mailConsumeQueue.send(null);
		ThisApplication.apiConsumeQueue.send(null);
		ThisApplication.jdbcConsumeQueue.send(null);
		ThisApplication.tableConsumeQueue.send(null);
		ThisApplication.hadoopConsumeQueue.send(null);
	}

}