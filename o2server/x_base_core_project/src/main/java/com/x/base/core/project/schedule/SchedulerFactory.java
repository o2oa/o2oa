package com.x.base.core.project.schedule;

import java.util.Properties;

import org.quartz.Scheduler;
import org.quartz.impl.StdSchedulerFactory;

public class SchedulerFactory extends Properties {

	private static final long serialVersionUID = -2875400768379456579L;

	private SchedulerFactory() {
	}

	public static Scheduler create() throws Exception {
		StdSchedulerFactory factory = new org.quartz.impl.StdSchedulerFactory(SchedulerFactoryProperties.concrete());
		return factory.getScheduler();
	}

}
