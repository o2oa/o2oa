package com.x.query.assemble.designer.jaxrs;

import java.util.Set;

import javax.ws.rs.ApplicationPath;

import com.x.base.core.project.jaxrs.AbstractActionApplication;
import com.x.query.assemble.designer.jaxrs.id.IdAction;
import com.x.query.assemble.designer.jaxrs.input.InputAction;
import com.x.query.assemble.designer.jaxrs.neural.NeuralAction;
import com.x.query.assemble.designer.jaxrs.output.OutputAction;
import com.x.query.assemble.designer.jaxrs.query.QueryAction;
import com.x.query.assemble.designer.jaxrs.reveal.RevealAction;
import com.x.query.assemble.designer.jaxrs.stat.StatAction;
import com.x.query.assemble.designer.jaxrs.view.ViewAction;

@ApplicationPath("jaxrs")
public class ActionApplication extends AbstractActionApplication {

	public Set<Class<?>> getClasses() {
		classes.add(QueryAction.class);
		classes.add(ViewAction.class);
		classes.add(StatAction.class);
		classes.add(RevealAction.class);
		classes.add(IdAction.class);
		classes.add(NeuralAction.class);
		classes.add(OutputAction.class);
		classes.add(InputAction.class);
		return classes;
	}

}
