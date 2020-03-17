package com.x.query.service.processing;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.organization.core.express.Organization;
import com.x.query.core.entity.Query;
import com.x.query.core.entity.Reveal;
import com.x.query.core.entity.Stat;
import com.x.query.core.entity.View;
import com.x.query.service.processing.factory.QueryFactory;

import net.sf.ehcache.Ehcache;

public class Business {

	private static Ehcache cache = ApplicationCache.instance().getCache(Query.class, View.class, Stat.class,
			Reveal.class);

	public Ehcache cache() {
		return cache;
	}

	private EntityManagerContainer emc;

	public Business(EntityManagerContainer emc) throws Exception {
		this.emc = emc;
	}

	public EntityManagerContainer entityManagerContainer() {
		return this.emc;
	}

	private Organization organization;

	public Organization organization() throws Exception {
		if (null == this.organization) {
			this.organization = new Organization(ThisApplication.context());
		}
		return organization;
	}

	private QueryFactory query;

	public QueryFactory query() throws Exception {
		if (null == this.query) {
			this.query = new QueryFactory(this);
		}
		return query;
	}

	public boolean isManager(EffectivePerson effectivePerson) throws Exception {
		if (effectivePerson.isManager()) {
			return true;
		}
		if (this.organization.person().hasRole(effectivePerson, OrganizationDefinition.QueryManager,
				OrganizationDefinition.Manager)) {
			return true;
		}
		return false;
	}

}