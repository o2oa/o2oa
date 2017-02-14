package com.x.processplatform.assemble.bam.jaxrs.period;

import com.x.base.core.http.ActionResult;
import com.x.processplatform.assemble.bam.ThisApplication;
import com.x.processplatform.assemble.bam.stub.CompanyStubs;

class ActionListExpiredWorkCompanyStubs extends ActionBase {

	ActionResult<CompanyStubs> execute() throws Exception {
		ActionResult<CompanyStubs> result = new ActionResult<>();
		result.setData(ThisApplication.period.getExpiredWorkCompanyStubs());
		return result;
	}

}