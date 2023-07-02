package com.x.server.console;

import java.util.Properties;

import org.apache.commons.lang3.BooleanUtils;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.tools.StringTools;

public class SchedulerBuilder {

	private SchedulerBuilder() {
		// nothing
	}

	public static void start() throws Exception {
		String scheduleGroup = StringTools.uniqueToken();
		StdSchedulerFactory stdSchedulerFactory = new StdSchedulerFactory(properties());
		Scheduler scheduler = stdSchedulerFactory.getScheduler();
		scheduler.start();

		if (BooleanUtils.isTrue(Config.currentNode().dumpData().enable())
				&& Config.currentNode().dumpData().available()) {
			dumpDataTask(scheduleGroup, scheduler);
		}

		if (BooleanUtils.isTrue(Config.currentNode().restoreData().enable())
				&& Config.currentNode().restoreData().available()) {
			restoreDataTask(scheduleGroup, scheduler);
		}

		if (BooleanUtils.isTrue(Config.currentNode().stackTrace().getEnable())) {
			stackTraceTask(scheduleGroup, scheduler);
		}

		registApplicationsAndVoteCenterTask(scheduleGroup, scheduler);
		// return scheduler;

	}

	private static void restoreDataTask(String scheduleGroup, Scheduler scheduler) throws Exception {
		JobDetail jobDetail = JobBuilder.newJob(RestoreDataTask.class)
				.withIdentity(RestoreDataTask.class.getName(), scheduleGroup).withDescription(Config.node()).build();
		Trigger trigger = TriggerBuilder.newTrigger().withIdentity(RestoreDataTask.class.getName(), scheduleGroup)
				.withSchedule(CronScheduleBuilder.cronSchedule(Config.currentNode().restoreData().cron())).build();
		scheduler.scheduleJob(jobDetail, trigger);
	}

	private static void dumpDataTask(String scheduleGroup, Scheduler scheduler) throws Exception {
		JobDetail jobDetail = JobBuilder.newJob(DumpDataTask.class)
				.withIdentity(DumpDataTask.class.getName(), scheduleGroup).withDescription(Config.node()).build();
		Trigger trigger = TriggerBuilder.newTrigger().withIdentity(DumpDataTask.class.getName(), scheduleGroup)
				.withSchedule(CronScheduleBuilder.cronSchedule(Config.currentNode().dumpData().cron())).build();
		scheduler.scheduleJob(jobDetail, trigger);
	}

	private static void stackTraceTask(String scheduleGroup, Scheduler scheduler) throws Exception {
		JobDetail jobDetail = JobBuilder.newJob(StackTraceTask.class)
				.withIdentity(StackTraceTask.class.getName(), scheduleGroup).withDescription(Config.node()).build();
		Trigger trigger = TriggerBuilder.newTrigger().withIdentity(StackTraceTask.class.getName(), scheduleGroup)
				.withSchedule(SimpleScheduleBuilder.simpleSchedule()
						.withIntervalInSeconds(Config.currentNode().stackTrace().getInterval()).repeatForever())
				.build();
		scheduler.scheduleJob(jobDetail, trigger);
	}

	/* 更新node节点applications 和 选择center主节点 */
	private static void registApplicationsAndVoteCenterTask(String scheduleGroup, Scheduler scheduler)
			throws Exception {
		JobDetail jobDetail = JobBuilder.newJob(RegistApplicationsAndVoteCenterTask.class)
				.withIdentity(RegistApplicationsAndVoteCenterTask.class.getName(), scheduleGroup)
				.withDescription(Config.node()).build();
		Trigger trigger = TriggerBuilder.newTrigger()
				.withIdentity(RegistApplicationsAndVoteCenterTask.class.getName(), scheduleGroup)
				.withSchedule(CronScheduleBuilder.cronSchedule("0/10 * * * * ?")).build();
		scheduler.scheduleJob(jobDetail, trigger);
	}

	private static Properties properties() {
		Properties properties = new Properties();
		properties.setProperty("org.quartz.scheduler.instanceName", "DefaultQuartzScheduler");
		properties.setProperty("org.quartz.scheduler.rmi.export", "false");
		properties.setProperty("org.quartz.scheduler.rmi.proxy", "false");
		properties.setProperty("org.quartz.scheduler.wrapJobExecutionInUserTransaction", "false");
		properties.setProperty("org.quartz.threadPool.class", "org.quartz.simpl.SimpleThreadPool");
		properties.setProperty("org.quartz.threadPool.threadCount", "2");
		properties.setProperty("org.quartz.threadPool.threadPriority", "5");
		properties.setProperty("org.quartz.threadPool.threadsInheritContextClassLoaderOfInitializingThread", "true");
		properties.setProperty("org.quartz.jobStore.misfireThreshold", "60000");
		properties.setProperty("org.quartz.jobStore.class", "org.quartz.simpl.RAMJobStore");
		properties.setProperty("org.quartz.scheduler.instanceName", "Main-QuartzScheduler-");
		properties.setProperty("org.quartz.scheduler.makeSchedulerThreadDaemon", "true");

		return properties;
	}

}
