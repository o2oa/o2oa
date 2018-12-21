package com.x.processplatform.assemble.bam.jaxrs.state;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.processplatform.assemble.bam.Business;
import com.x.processplatform.assemble.bam.ThisApplication;
import com.x.processplatform.assemble.bam.stub.ApplicationStubs;

class ActionApplicationStubsTrigger extends ActionBase {

	private static Logger logger = LoggerFactory.getLogger(ActionApplicationStubsTrigger.class);

	ActionResult<Wo> execute() throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<Wo> result = new ActionResult<>();
			Business business = new Business(emc);
			new TimerApplicationStubs().execute(business);
			Wo wo = new Wo();
			wo.addAll(ThisApplication.state.getApplicationStubs());
			result.setData((wo));
			return result;
		}

	}

	public static class Wo extends ApplicationStubs {

	}

}
