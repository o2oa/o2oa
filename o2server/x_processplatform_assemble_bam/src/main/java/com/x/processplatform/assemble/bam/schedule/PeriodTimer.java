package com.x.processplatform.assemble.bam.schedule;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.schedule.AbstractJob;
import com.x.processplatform.assemble.bam.ThisApplication;
import com.x.processplatform.assemble.bam.jaxrs.period.TimerCompletedTaskApplicationStubs;
import com.x.processplatform.assemble.bam.jaxrs.period.TimerCompletedTaskUnitStubs;
import com.x.processplatform.assemble.bam.jaxrs.period.TimerCompletedWorkApplicationStubs;
import com.x.processplatform.assemble.bam.jaxrs.period.TimerCompletedWorkUnitStubs;
import com.x.processplatform.assemble.bam.jaxrs.period.TimerExpiredTaskApplicationStubs;
import com.x.processplatform.assemble.bam.jaxrs.period.TimerExpiredTaskUnitStubs;
import com.x.processplatform.assemble.bam.jaxrs.period.TimerExpiredWorkApplicationStubs;
import com.x.processplatform.assemble.bam.jaxrs.period.TimerExpiredWorkUnitStubs;
import com.x.processplatform.assemble.bam.jaxrs.period.TimerStartTaskApplicationStubs;
import com.x.processplatform.assemble.bam.jaxrs.period.TimerStartTaskUnitStubs;
import com.x.processplatform.assemble.bam.jaxrs.period.TimerStartWorkApplicationStubs;
import com.x.processplatform.assemble.bam.jaxrs.period.TimerStartWorkUnitStubs;

public class PeriodTimer extends AbstractJob {

	private static Logger logger = LoggerFactory.getLogger(PeriodTimer.class);

	@Override
	public void schedule(JobExecutionContext jobExecutionContext) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			/* 每月产生工作所需要的ApplicationStub和CompanyStub */
			ThisApplication.period.setStartTaskApplicationStubs(new TimerStartTaskApplicationStubs().execute(emc));
			ThisApplication.period.setStartTaskUnitStubs(new TimerStartTaskUnitStubs().execute(emc));
			/* 每月完成工作所需要的ApplicationStub和CompanyStub */
			ThisApplication.period
					.setCompletedTaskApplicationStubs(new TimerCompletedTaskApplicationStubs().execute(emc));
			ThisApplication.period.setCompletedTaskUnitStubs(new TimerCompletedTaskUnitStubs().execute(emc));
			/* 每月超时工作所需要的ApplicationStub和CompanyStub */
			ThisApplication.period.setExpiredTaskApplicationStubs(new TimerExpiredTaskApplicationStubs().execute(emc));
			ThisApplication.period.setExpiredTaskUnitStubs(new TimerExpiredTaskUnitStubs().execute(emc));
			/* 每月产生工作所需要的ApplicationStub和CompanyStub */
			ThisApplication.period.setStartWorkApplicationStubs(new TimerStartWorkApplicationStubs().execute(emc));
			ThisApplication.period.setStartWorkUnitStubs(new TimerStartWorkUnitStubs().execute(emc));
			/* 每月完成工作所需要的ApplicationStub和CompanyStub */
			ThisApplication.period
					.setCompletedWorkApplicationStubs(new TimerCompletedWorkApplicationStubs().execute(emc));
			ThisApplication.period.setCompletedWorkUnitStubs(new TimerCompletedWorkUnitStubs().execute(emc));
			/* 每月超时工作所需要的ApplicationStub和CompanyStub */
			ThisApplication.period.setExpiredWorkApplicationStubs(new TimerExpiredWorkApplicationStubs().execute(emc));
			ThisApplication.period.setExpiredWorkUnitStubs(new TimerExpiredWorkUnitStubs().execute(emc));
		} catch (Exception e) {
			logger.error(e);
			throw new JobExecutionException(e);
		}
	}

}