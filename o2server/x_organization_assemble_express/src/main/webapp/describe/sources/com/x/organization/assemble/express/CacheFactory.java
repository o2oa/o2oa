package com.x.organization.assemble.express;

import com.x.base.core.project.cache.ApplicationCache;
import com.x.organization.core.entity.Group;
import com.x.organization.core.entity.Identity;
import com.x.organization.core.entity.Person;
import com.x.organization.core.entity.PersonAttribute;
import com.x.organization.core.entity.Role;
import com.x.organization.core.entity.Unit;
import com.x.organization.core.entity.UnitAttribute;
import com.x.organization.core.entity.UnitDuty;
import com.x.organization.core.entity.accredit.Empower;

import net.sf.ehcache.Ehcache;

public class CacheFactory {

	public static Ehcache getOrganizationCache() {
		return ApplicationCache.instance().getCache(Identity.class, Unit.class, UnitAttribute.class, UnitDuty.class,
				Role.class, Person.class, PersonAttribute.class, Group.class, Empower.class);
	}

	public static Ehcache getIdentityCache() {
		return ApplicationCache.instance().getCache(Identity.class);
	}

	public static Ehcache getUnitCache() {
		return ApplicationCache.instance().getCache(Unit.class);
	}

	public static Ehcache getTrustCache() {
		return ApplicationCache.instance().getCache(Empower.class);
	}

	public static Ehcache getUnitAttributeCache() {
		return ApplicationCache.instance().getCache(UnitAttribute.class);
	}

	public static Ehcache getUnitDutyCache() {
		return ApplicationCache.instance().getCache(UnitDuty.class);
	}

	public static Ehcache getRoleCache() {
		return ApplicationCache.instance().getCache(Role.class);
	}

	public static Ehcache getPersonCache() {
		return ApplicationCache.instance().getCache(Person.class);
	}

	public static Ehcache getPersonAttributeCache() {
		return ApplicationCache.instance().getCache(PersonAttribute.class);
	}

	public static Ehcache getGroupCache() {
		return ApplicationCache.instance().getCache(Group.class);
	}

	public static void cacheNotifyAll() throws Exception {
		ApplicationCache.notify(Person.class);
		ApplicationCache.notify(PersonAttribute.class);
		ApplicationCache.notify(Identity.class);
		ApplicationCache.notify(Unit.class);
		ApplicationCache.notify(UnitAttribute.class);
		ApplicationCache.notify(UnitDuty.class);
		ApplicationCache.notify(Group.class);
		ApplicationCache.notify(Role.class);
		ApplicationCache.notify(Empower.class);
	}
}
