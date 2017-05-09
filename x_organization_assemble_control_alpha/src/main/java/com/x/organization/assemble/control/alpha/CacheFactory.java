package com.x.organization.assemble.control.alpha;

import com.x.base.core.cache.ApplicationCache;
import com.x.organization.core.entity.Company;
import com.x.organization.core.entity.CompanyAttribute;

import net.sf.ehcache.Ehcache;

public class CacheFactory {

	public static Ehcache getCompanyCache() {
		return ApplicationCache.instance().getCache(Company.class);
	}

	public static Ehcache getCompanyAttributeCache() {
		return ApplicationCache.instance().getCache(CompanyAttribute.class);
	}

}
