package com.x.processplatform.assemble.bam.timer;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.Context;
import com.x.base.core.project.clock.ClockTimerTask;
import com.x.processplatform.assemble.bam.Business;
import com.x.processplatform.assemble.bam.ThisApplication;
import com.x.processplatform.assemble.bam.jaxrs.state.TimerApplicationStubs;
import com.x.processplatform.assemble.bam.jaxrs.state.TimerCategory;
import com.x.processplatform.assemble.bam.jaxrs.state.TimerCompanyStubs;
import com.x.processplatform.assemble.bam.jaxrs.state.TimerDepartmentStubs;
import com.x.processplatform.assemble.bam.jaxrs.state.TimerOrganization;
import com.x.processplatform.assemble.bam.jaxrs.state.TimerPersonStubs;
import com.x.processplatform.assemble.bam.jaxrs.state.TimerRunning;
import com.x.processplatform.assemble.bam.jaxrs.state.TimerSummary;

public class StateTimer extends ClockTimerTask {

	public StateTimer(Context context) {
		super(context);
	}

	public void execute() {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			Business business = new Business(emc);
			ThisApplication.state.setCompanyStubs(new TimerCompanyStubs().execute());
			ThisApplication.state.setDepartmentStubs(new TimerDepartmentStubs().execute());
			ThisApplication.state.setPersonStubs(new TimerPersonStubs().execute());
			ThisApplication.state.setApplicationStubs(new TimerApplicationStubs().execute(business));
			ThisApplication.state.setSummary(new TimerSummary().execute(business));
			ThisApplication.state.setRunning(new TimerRunning().execute(business));
			ThisApplication.state.setOrganization(new TimerOrganization().execute(business));
			ThisApplication.state.setCategory(new TimerCategory().execute(business));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}