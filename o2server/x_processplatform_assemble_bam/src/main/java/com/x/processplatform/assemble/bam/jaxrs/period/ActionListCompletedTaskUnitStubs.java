package com.x.processplatform.assemble.bam.jaxrs.period;

import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.assemble.bam.ThisApplication;
import com.x.processplatform.assemble.bam.stub.UnitStubs;

class ActionListCompletedTaskUnitStubs extends BaseAction {
	
	private static Logger logger = LoggerFactory.getLogger(ActionListCompletedTaskUnitStubs.class);

	ActionResult<UnitStubs> execute() throws Exception {
		ActionResult<UnitStubs> result = new ActionResult<>();
		result.setData(ThisApplication.period.getCompletedTaskUnitStubs());
		return result;
	}

}