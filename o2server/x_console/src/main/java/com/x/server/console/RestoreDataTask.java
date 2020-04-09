package com.x.server.console;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.DateTools;
import com.x.server.console.action.ActionRestoreData;

public class RestoreDataTask implements Job {

	private static Logger logger = LoggerFactory.getLogger(RestoreDataTask.class);

	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		try {
			logger.print("schedule restore data task start, restore from:{}.",
					Config.currentNode().restoreData().path());
			ActionRestoreData action = new ActionRestoreData();
			action.execute(Config.currentNode().restoreData().path(), Config.token().getPassword());
		} catch (Exception e) {
			throw new JobExecutionException(e);
		}
	}

}