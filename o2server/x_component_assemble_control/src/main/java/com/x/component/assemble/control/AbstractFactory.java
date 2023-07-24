package com.x.component.assemble.control;

import com.x.base.core.container.EntityManagerContainer;

public abstract class AbstractFactory {

	private Business business;

	protected AbstractFactory(Business business) {
		try {
			if (null == business) {
				throw new Exception("business can not be null.");
			}
			this.business = business;
		} catch (Exception e) {
			throw new IllegalStateException("can not instantiating factory.");
		}
	}

	public EntityManagerContainer entityManagerContainer() {
		return this.business.entityManagerContainer();
	}

}