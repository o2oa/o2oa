package com.x.base.core.project.schedule;

import java.util.Properties;

public class SchedulerFactoryProperties extends Properties {

	private static final long serialVersionUID = -2875400768379456579L;

	private SchedulerFactoryProperties() {
	}

	public static SchedulerFactoryProperties concrete() {
		SchedulerFactoryProperties properties = new SchedulerFactoryProperties();
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
		properties.setProperty("log4j.logger.org.quartz", "OFF");
		return properties;
	}

	public static SchedulerFactoryProperties min() {
		SchedulerFactoryProperties properties = new SchedulerFactoryProperties();
		properties.setProperty("org.quartz.scheduler.instanceName", "DefaultQuartzScheduler");
		properties.setProperty("org.quartz.scheduler.rmi.export", "false");
		properties.setProperty("org.quartz.scheduler.rmi.proxy", "false");
		properties.setProperty("org.quartz.scheduler.wrapJobExecutionInUserTransaction", "false");
		properties.setProperty("org.quartz.threadPool.class", "org.quartz.simpl.SimpleThreadPool");
		properties.setProperty("org.quartz.threadPool.threadCount", "1");
		properties.setProperty("org.quartz.threadPool.threadPriority", "5");
		properties.setProperty("org.quartz.threadPool.threadsInheritContextClassLoaderOfInitializingThread", "true");
		properties.setProperty("org.quartz.jobStore.misfireThreshold", "60000");
		properties.setProperty("org.quartz.jobStore.class", "org.quartz.simpl.RAMJobStore");
		// org.quartz.scheduler.instanceName: DefaultQuartzScheduler
		// org.quartz.scheduler.rmi.export: false
		// org.quartz.scheduler.rmi.proxy: false
		// org.quartz.scheduler.wrapJobExecutionInUserTransaction: false
		//
		// org.quartz.threadPool.class: org.quartz.simpl.SimpleThreadPool
		// org.quartz.threadPool.threadCount: 10
		// org.quartz.threadPool.threadPriority: 5
		// org.quartz.threadPool.threadsInheritContextClassLoaderOfInitializingThread:
		// true
		//
		// org.quartz.jobStore.misfireThreshold: 60000
		//
		// org.quartz.jobStore.class: org.quartz.simpl.RAMJobStore
		return properties;
	}

}
