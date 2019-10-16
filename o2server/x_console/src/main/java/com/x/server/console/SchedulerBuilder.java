package com.x.server.console;

import java.util.Properties;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import com.x.base.core.project.config.Config;
import com.x.base.core.project.tools.StringTools;

public class SchedulerBuilder {

	public Scheduler start() throws Exception {
		String scheduleGroup = StringTools.uniqueToken();
		StdSchedulerFactory stdSchedulerFactory = new StdSchedulerFactory(properties());
		Scheduler scheduler = stdSchedulerFactory.getScheduler();
		scheduler.start();

		if (Config.currentNode().dumpData().enable() && Config.currentNode().dumpData().available()) {
			JobDetail jobDetail = JobBuilder.newJob(DumpDataTask.class)
					.withIdentity(DumpDataTask.class.getName(), scheduleGroup).withDescription(Config.node()).build();
			Trigger trigger = TriggerBuilder.newTrigger().withIdentity(DumpDataTask.class.getName(), scheduleGroup)
					.withSchedule(CronScheduleBuilder.cronSchedule(Config.currentNode().dumpData().cron())).build();
			scheduler.scheduleJob(jobDetail, trigger);
		}
		if (Config.currentNode().dumpStorage().enable() && Config.currentNode().dumpStorage().available()) {
			JobDetail jobDetail = JobBuilder.newJob(DumpStorageTask.class)
					.withIdentity(DumpStorageTask.class.getName(), scheduleGroup).withDescription(Config.node())
					.build();
			Trigger trigger = TriggerBuilder.newTrigger().withIdentity(DumpStorageTask.class.getName(), scheduleGroup)
					.withSchedule(CronScheduleBuilder.cronSchedule(Config.currentNode().dumpStorage().cron())).build();
			scheduler.scheduleJob(jobDetail, trigger);
		}
		if (Config.currentNode().restoreData().enable() && Config.currentNode().restoreData().available()) {
			JobDetail jobDetail = JobBuilder.newJob(RestoreDataTask.class)
					.withIdentity(RestoreDataTask.class.getName(), scheduleGroup).withDescription(Config.node())
					.build();
			Trigger trigger = TriggerBuilder.newTrigger().withIdentity(RestoreDataTask.class.getName(), scheduleGroup)
					.withSchedule(CronScheduleBuilder.cronSchedule(Config.currentNode().restoreData().cron())).build();
			scheduler.scheduleJob(jobDetail, trigger);
		}
		if (Config.currentNode().restoreStorage().enable() && Config.currentNode().restoreStorage().available()) {
			JobDetail jobDetail = JobBuilder.newJob(RestoreStorageTask.class)
					.withIdentity(RestoreStorageTask.class.getName(), scheduleGroup).withDescription(Config.node())
					.build();
			Trigger trigger = TriggerBuilder.newTrigger()
					.withIdentity(RestoreStorageTask.class.getName(), scheduleGroup)
					.withSchedule(CronScheduleBuilder.cronSchedule(Config.currentNode().restoreStorage().cron()))
					.build();
			scheduler.scheduleJob(jobDetail, trigger);
		}
		this.registApplicationsAndVoteCenterTask(scheduler, scheduleGroup);
		return scheduler;

	}

	/* 更新node节点applications 和 选择center主节点 */
	private void registApplicationsAndVoteCenterTask(Scheduler scheduler, String scheduleGroup) throws Exception {
		JobDetail jobDetail = JobBuilder.newJob(RegistApplicationsAndVoteCenterTask.class)
				.withIdentity(RegistApplicationsAndVoteCenterTask.class.getName(), scheduleGroup).withDescription(Config.node())
				.build();
		Trigger trigger = TriggerBuilder.newTrigger()
				.withIdentity(RegistApplicationsAndVoteCenterTask.class.getName(), scheduleGroup)
				.withSchedule(CronScheduleBuilder.cronSchedule("*/30 * * * * ?")).build();
		scheduler.scheduleJob(jobDetail, trigger);
	}

	private Properties properties() {
		Properties properties = new Properties();
		properties.setProperty("org.quartz.scheduler.instanceName", "DefaultQuartzScheduler");
		properties.setProperty("org.quartz.scheduler.rmi.export", "false");
		properties.setProperty("org.quartz.scheduler.rmi.proxy", "false");
		properties.setProperty("org.quartz.scheduler.wrapJobExecutionInUserTransaction", "false");
		properties.setProperty("org.quartz.threadPool.class", "org.quartz.simpl.SimpleThreadPool");
		properties.setProperty("org.quartz.threadPool.threadCount", "5");
		properties.setProperty("org.quartz.threadPool.threadPriority", "5");
		properties.setProperty("org.quartz.threadPool.threadsInheritContextClassLoaderOfInitializingThread", "true");
		properties.setProperty("org.quartz.jobStore.misfireThreshold", "60000");
		properties.setProperty("org.quartz.jobStore.class", "org.quartz.simpl.RAMJobStore");
		return properties;
	}

}
