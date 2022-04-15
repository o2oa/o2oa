package com.x.processplatform.service.processing.schedule;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.schedule.AbstractJob;
import com.x.processplatform.service.processing.ThisApplication;

public class ArchiveHadoop extends AbstractJob {

	private static final Logger LOGGER = LoggerFactory.getLogger(ArchiveHadoop.class);

	@Override
	public void schedule(JobExecutionContext jobExecutionContext) throws Exception {
		LOGGER.debug("send archiveHadoopQueue signal.");
		try {
			ThisApplication.archiveHadoopQueue.send("");
		} catch (Exception e) {
			throw new JobExecutionException(e);
		}
	}

}