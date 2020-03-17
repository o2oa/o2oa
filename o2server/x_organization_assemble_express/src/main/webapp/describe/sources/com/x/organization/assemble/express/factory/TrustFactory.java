package com.x.organization.assemble.express.factory;

import com.x.organization.assemble.express.AbstractFactory;
import com.x.organization.assemble.express.Business;
import com.x.organization.assemble.express.CacheFactory;

import net.sf.ehcache.Ehcache;

public class TrustFactory extends AbstractFactory {

	private Ehcache cache;

	public TrustFactory(Business business) throws Exception {
		super(business);
		this.cache = CacheFactory.getTrustCache();
	}

}