package com.x.query.assemble.designer.jaxrs;

import java.util.Set;

import javax.ws.rs.ApplicationPath;

import com.x.base.core.project.jaxrs.AbstractActionApplication;
import com.x.query.assemble.designer.jaxrs.designer.DesignerAction;
import com.x.query.assemble.designer.jaxrs.id.IdAction;
import com.x.query.assemble.designer.jaxrs.importmodel.ImportModelAction;
import com.x.query.assemble.designer.jaxrs.input.InputAction;
import com.x.query.assemble.designer.jaxrs.neural.NeuralAction;
import com.x.query.assemble.designer.jaxrs.output.OutputAction;
import com.x.query.assemble.designer.jaxrs.query.QueryAction;
import com.x.query.assemble.designer.jaxrs.stat.StatAction;
import com.x.query.assemble.designer.jaxrs.statement.StatementAction;
import com.x.query.assemble.designer.jaxrs.table.TableAction;
import com.x.query.assemble.designer.jaxrs.view.ViewAction;

@ApplicationPath("jaxrs")
public class ActionApplication extends AbstractActionApplication {

	public Set<Class<?>> getClasses() {
		classes.add(QueryAction.class);
		classes.add(ViewAction.class);
		classes.add(StatAction.class);
		classes.add(IdAction.class);
		classes.add(NeuralAction.class);
		classes.add(OutputAction.class);
		classes.add(InputAction.class);
		classes.add(TableAction.class);
		classes.add(StatementAction.class);
		classes.add(DesignerAction.class);
		classes.add(ImportModelAction.class);
		return classes;
	}

}
