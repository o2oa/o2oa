package com.x.program.center.jaxrs.applications;

import com.x.base.core.application.Applications;
import com.x.program.center.ThisApplication;

public class ActionGet {

	public Applications execute() throws Exception {
		return ThisApplication.applications;
	}

}
