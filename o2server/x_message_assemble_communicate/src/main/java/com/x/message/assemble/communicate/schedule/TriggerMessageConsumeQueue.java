package com.x.message.assemble.communicate.schedule;

import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.schedule.AbstractJob;
import com.x.message.assemble.communicate.ThisApplication;
import org.quartz.JobExecutionContext;

public class TriggerMessageConsumeQueue extends AbstractJob {

	private static final Logger LOGGER = LoggerFactory.getLogger(TriggerMessageConsumeQueue.class);

	@Override
	public void schedule(JobExecutionContext jobExecutionContext) throws Exception {
		LOGGER.debug(
				"schedule trigger kafkaConsumeQueue, activeMqConsumeQueue, restfulConsumeQueue, mailConsumeQueue, apiConsumeQueue, jdbcConsumeQueue, tableConsumeQueue.");
		ThisApplication.kafkaConsumeQueue.send(null);
		ThisApplication.activemqConsumeQueue.send(null);
		ThisApplication.restfulConsumeQueue.send(null);
		ThisApplication.mailConsumeQueue.send(null);
		ThisApplication.apiConsumeQueue.send(null);
		ThisApplication.jdbcConsumeQueue.send(null);
		ThisApplication.tableConsumeQueue.send(null);
	}

}
