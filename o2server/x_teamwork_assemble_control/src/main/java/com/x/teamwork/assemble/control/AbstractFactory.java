package com.x.teamwork.assemble.control;

import com.x.base.core.container.EntityManagerContainer;

public abstract class AbstractFactory {

	protected Business business;

	public static final String EMPTY_SYMBOL = "(0)";

	public AbstractFactory( Business business ) throws Exception {
		if ( null == business ) {
			throw new Exception( "business can not be null.  " );
		}
		this.business = business;
	}

	public EntityManagerContainer entityManagerContainer() throws Exception {
		return this.business.entityManagerContainer();
	}

}
