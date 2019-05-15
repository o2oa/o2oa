package com.x.organization.core.express.trustlog;

import com.x.base.core.project.AbstractContext;
import com.x.base.core.project.jaxrs.WrapBoolean;
import com.x.base.core.project.organization.TrustLog;

class ActionLog extends BaseAction {

	public static boolean execute(AbstractContext context, TrustLog trustLog) throws Exception {
		Wo wo = context.applications().postQuery(applicationClass, "trustlog", trustLog).getData(Wo.class);
		return wo.getValue();
	}

	public static class Wo extends WrapBoolean {
	}

}