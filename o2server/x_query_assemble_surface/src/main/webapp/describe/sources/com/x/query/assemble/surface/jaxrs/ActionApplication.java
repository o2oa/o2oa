package com.x.query.assemble.surface.jaxrs;

import java.util.Set;

import javax.ws.rs.ApplicationPath;

import com.x.base.core.project.jaxrs.AbstractActionApplication;
import com.x.query.assemble.surface.jaxrs.neural.NeuralAction;
import com.x.query.assemble.surface.jaxrs.query.QueryAction;
import com.x.query.assemble.surface.jaxrs.reveal.RevealAction;
import com.x.query.assemble.surface.jaxrs.segment.SegmentAction;
import com.x.query.assemble.surface.jaxrs.stat.StatAction;
import com.x.query.assemble.surface.jaxrs.statement.StatementAction;
import com.x.query.assemble.surface.jaxrs.table.TableAction;
import com.x.query.assemble.surface.jaxrs.test.TestAction;
import com.x.query.assemble.surface.jaxrs.view.ViewAction;

@ApplicationPath("jaxrs")
public class ActionApplication extends AbstractActionApplication {

	public Set<Class<?>> getClasses() {
		classes.add(QueryAction.class);
		classes.add(ViewAction.class);
		classes.add(StatAction.class);
		classes.add(RevealAction.class);
		classes.add(TestAction.class);
		classes.add(SegmentAction.class);
		classes.add(NeuralAction.class);
		classes.add(TableAction.class);
		classes.add(StatementAction.class);
		return classes;
	}

}
