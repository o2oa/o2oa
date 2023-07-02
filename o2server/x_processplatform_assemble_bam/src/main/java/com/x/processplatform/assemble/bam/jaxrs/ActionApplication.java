package com.x.processplatform.assemble.bam.jaxrs;

import java.util.Set;

import javax.ws.rs.ApplicationPath;

import com.x.base.core.project.jaxrs.AbstractActionApplication;
import com.x.processplatform.assemble.bam.jaxrs.period.PeriodAction;
import com.x.processplatform.assemble.bam.jaxrs.state.StateAction;

@ApplicationPath("jaxrs")
public class ActionApplication extends AbstractActionApplication {

	public Set<Class<?>> getClasses() {
		classes.add(PeriodAction.class);
		classes.add(StateAction.class);
		return classes;
	}

}
