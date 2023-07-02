package com.x.organization.core.express.empowerlog;

import com.x.base.core.project.AbstractContext;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.organization.EmpowerLog;

class ActionLog extends BaseAction {

	public static boolean execute(AbstractContext context, EmpowerLog empowerLog) throws Exception {
		Wo wo = context.applications().postQuery(applicationClass, "empowerlog", empowerLog).getData(Wo.class);
		return wo.getValue();
	}

	public static class Wo extends WrapBoolean {
	}

}