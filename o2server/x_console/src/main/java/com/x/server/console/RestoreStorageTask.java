//package com.x.server.console;
//
//import com.x.base.core.project.config.Config;
//import com.x.base.core.project.logger.Logger;
//import com.x.base.core.project.logger.LoggerFactory;
//import com.x.server.console.action.RestoreStorage;
//
//import org.quartz.Job;
//import org.quartz.JobExecutionContext;
//import org.quartz.JobExecutionException;
//
//public class RestoreStorageTask implements Job {
//
//	private static Logger logger = LoggerFactory.getLogger(RestoreStorageTask.class);
//
//	@Override
//	public void execute(JobExecutionContext arg0) throws JobExecutionException {
//		try {
//			logger.print("schedule restore storage task start, restore from:{}.",
//					Config.currentNode().restoreData().path());
//			RestoreStorage action = new RestoreStorage();
//			action.execute(Config.currentNode().restoreStorage().path());
//		} catch (Exception e) {
//			throw new JobExecutionException(e);
//		}
//
//	}
//
//}