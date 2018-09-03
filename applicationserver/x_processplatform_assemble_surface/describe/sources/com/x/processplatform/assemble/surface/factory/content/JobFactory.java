package com.x.processplatform.assemble.surface.factory.content;

import com.x.processplatform.assemble.surface.AbstractFactory;
import com.x.processplatform.assemble.surface.Business;

public class JobFactory extends AbstractFactory {

	public JobFactory(Business abstractBusiness) throws Exception {
		super(abstractBusiness);
	}

	public boolean hasMultiRelative(String job) throws Exception {
		Business business = this.business();
		return (business.work().countWithJob(job) + business.workCompleted().countWithJob(job)) > 1 ? true : false;
	}
}