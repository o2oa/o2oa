package com.x.program.center.test.quartz;

import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

import org.junit.Test;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;

public class TestClient {

	@Test
	public void test() throws Exception {
		SchedulerFactory schedFact = new org.quartz.impl.StdSchedulerFactory();
	
		Scheduler sched = schedFact.getScheduler();
	//	sched.getListenerManager().addJobListener(arg0);
		sched.start();
		// define the job and tie it to our
		JobDetail job = JobBuilder.newJob(HelloJob.class).withIdentity("myJob", "group1").build();
		// Trigger the job to run now, andthen every 40 seconds
		Trigger trigger = newTrigger().withIdentity("myTrigger", "group2").startNow()
				.withSchedule(simpleSchedule().withIntervalInSeconds(1).repeatForever()).build();
		// Tell quartz to schedule the job using our trigger
		sched.scheduleJob(job, trigger);
		Thread.sleep(2000);
	}
}
