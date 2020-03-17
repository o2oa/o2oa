package com.x.program.center.jaxrs.applications;

import com.x.base.core.project.Applications;
import com.x.program.center.ThisApplication;

public class ActionGet extends BaseAction {

	public Applications execute() throws Exception {
		return ThisApplication.context().applications();
	}

}
