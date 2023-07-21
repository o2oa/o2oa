package com.x.program.center.schedule;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;

public class DingdingMessage implements Job {

	private static Logger logger = LoggerFactory.getLogger(DingdingMessage.class);

	/* 向列表发送一个同步信号 */
	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

	}

}
