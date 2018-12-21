package com.x.organization.assemble.authentication;

import com.x.base.core.container.EntityManagerContainer;

public abstract class AbstractFactory {

	private Business business;

	public AbstractFactory(Business business) throws Exception {
		try {
			if (null == business) {
				throw new Exception("business can not be null.");
			}
			this.business = business;
		} catch (Exception e) {
			throw new Exception("can not instantiating factory.");
		}
	}

	public EntityManagerContainer entityManagerContainer() throws Exception {
		return this.business.entityManagerContainer();
	}

}