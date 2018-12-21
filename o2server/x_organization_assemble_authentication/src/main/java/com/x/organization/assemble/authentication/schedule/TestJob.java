package com.x.organization.assemble.authentication.schedule;

import java.util.Date;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class TestJob implements Job {

	private static Logger logger = LoggerFactory.getLogger(TestJob.class);

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			logger.print("run at {}", new Date());
		} catch (Exception e) {
			logger.error(e);
			throw new JobExecutionException(e);
		}
	}

}
