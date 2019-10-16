package com.x.base.core.project.schedule;

import java.util.concurrent.CopyOnWriteArrayList;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public abstract class AbstractJob implements Job {

	private static Logger logger = LoggerFactory.getLogger(AbstractJob.class);

	private static final CopyOnWriteArrayList<String> LOCK = new CopyOnWriteArrayList<>();

	public final void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		try {
			if (LOCK.contains(this.getClass().getName())) {
				throw new ExceptionScheduleLastNotEnd(this.getClass().getName());
			}
			try {
				LOCK.add(this.getClass().getName());
				this.schedule(jobExecutionContext);
			} finally {
				LOCK.remove(this.getClass().getName());
			}
		} catch (Exception e) {
			logger.error(e);
			throw new JobExecutionException(e);
		}
	}

	public abstract void schedule(JobExecutionContext jobExecutionContext) throws Exception;

}
