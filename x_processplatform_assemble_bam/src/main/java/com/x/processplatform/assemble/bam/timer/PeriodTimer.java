package com.x.processplatform.assemble.bam.timer;

import java.util.TimerTask;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.processplatform.assemble.bam.ThisApplication;
import com.x.processplatform.assemble.bam.jaxrs.period.TimerCompletedTaskApplicationStubs;
import com.x.processplatform.assemble.bam.jaxrs.period.TimerCompletedTaskCompanyStubs;
import com.x.processplatform.assemble.bam.jaxrs.period.TimerCompletedWorkApplicationStubs;
import com.x.processplatform.assemble.bam.jaxrs.period.TimerCompletedWorkCompanyStubs;
import com.x.processplatform.assemble.bam.jaxrs.period.TimerExpiredTaskApplicationStubs;
import com.x.processplatform.assemble.bam.jaxrs.period.TimerExpiredTaskCompanyStubs;
import com.x.processplatform.assemble.bam.jaxrs.period.TimerExpiredWorkApplicationStubs;
import com.x.processplatform.assemble.bam.jaxrs.period.TimerExpiredWorkCompanyStubs;
import com.x.processplatform.assemble.bam.jaxrs.period.TimerStartTaskApplicationStubs;
import com.x.processplatform.assemble.bam.jaxrs.period.TimerStartTaskCompanyStubs;
import com.x.processplatform.assemble.bam.jaxrs.period.TimerStartWorkApplicationStubs;
import com.x.processplatform.assemble.bam.jaxrs.period.TimerStartWorkCompanyStubs;

public class PeriodTimer extends TimerTask {

	public void run() {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			/* 每月产生工作所需要的ApplicationStub和CompanyStub */
			ThisApplication.period.setStartTaskApplicationStubs(new TimerStartTaskApplicationStubs().execute(emc));
			ThisApplication.period.setStartTaskCompanyStubs(new TimerStartTaskCompanyStubs().execute(emc));
			/* 每月完成工作所需要的ApplicationStub和CompanyStub */
			ThisApplication.period
					.setCompletedTaskApplicationStubs(new TimerCompletedTaskApplicationStubs().execute(emc));
			ThisApplication.period.setCompletedTaskCompanyStubs(new TimerCompletedTaskCompanyStubs().execute(emc));
			/* 每月超时工作所需要的ApplicationStub和CompanyStub */
			ThisApplication.period.setExpiredTaskApplicationStubs(new TimerExpiredTaskApplicationStubs().execute(emc));
			ThisApplication.period.setExpiredTaskCompanyStubs(new TimerExpiredTaskCompanyStubs().execute(emc));
			/* 每月产生工作所需要的ApplicationStub和CompanyStub */
			ThisApplication.period.setStartWorkApplicationStubs(new TimerStartWorkApplicationStubs().execute(emc));
			ThisApplication.period.setStartWorkCompanyStubs(new TimerStartWorkCompanyStubs().execute(emc));
			/* 每月完成工作所需要的ApplicationStub和CompanyStub */
			ThisApplication.period
					.setCompletedWorkApplicationStubs(new TimerCompletedWorkApplicationStubs().execute(emc));
			ThisApplication.period.setCompletedWorkCompanyStubs(new TimerCompletedWorkCompanyStubs().execute(emc));
			/* 每月超时工作所需要的ApplicationStub和CompanyStub */
			ThisApplication.period.setExpiredWorkApplicationStubs(new TimerExpiredWorkApplicationStubs().execute(emc));
			ThisApplication.period.setExpiredWorkCompanyStubs(new TimerExpiredWorkCompanyStubs().execute(emc));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}