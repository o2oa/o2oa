package com.x.processplatform.assemble.bam.schedule;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.schedule.AbstractJob;
import com.x.processplatform.assemble.bam.Business;
import com.x.processplatform.assemble.bam.jaxrs.state.TimerApplicationStubs;
import com.x.processplatform.assemble.bam.jaxrs.state.TimerCategory;
import com.x.processplatform.assemble.bam.jaxrs.state.TimerOrganization;
import com.x.processplatform.assemble.bam.jaxrs.state.TimerPersonStubs;
import com.x.processplatform.assemble.bam.jaxrs.state.TimerRunning;
import com.x.processplatform.assemble.bam.jaxrs.state.TimerSummary;
import com.x.processplatform.assemble.bam.jaxrs.state.TimerUnitStubs;

public class StateTimer extends AbstractJob {

	private static Logger logger = LoggerFactory.getLogger(StateTimer.class);

	@Override
	public void schedule(JobExecutionContext jobExecutionContext) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			new TimerUnitStubs().execute(business);
			new TimerPersonStubs().execute(business);
			new TimerApplicationStubs().execute(business);
			new TimerSummary().execute(business);
			new TimerRunning().execute(business);
			new TimerOrganization().execute(business);
			new TimerCategory().execute(business);
		} catch (Exception e) {
			logger.error(e);
			throw new JobExecutionException(e);
		}
	}

}