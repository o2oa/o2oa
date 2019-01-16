package com.x.query.service.processing.jaxrs;

import java.util.Set;

import javax.ws.rs.ApplicationPath;

import com.x.base.core.project.jaxrs.AbstractActionApplication;
import com.x.query.service.processing.jaxrs.neural.NeuralAction;
import com.x.query.service.processing.jaxrs.segment.SegmentAction;
import com.x.query.service.processing.jaxrs.test.TestAction;

@ApplicationPath("jaxrs")
public class ActionApplication extends AbstractActionApplication {

	public Set<Class<?>> getClasses() {
		classes.add(TestAction.class);
		classes.add(NeuralAction.class);
		classes.add(SegmentAction.class);
		return classes;
	}

}
