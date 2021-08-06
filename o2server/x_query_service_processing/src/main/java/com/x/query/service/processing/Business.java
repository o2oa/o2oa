package com.x.query.service.processing;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.organization.core.express.Organization;
import com.x.query.service.processing.factory.QueryFactory;

public class Business {

	private EntityManagerContainer emc;

	public Business() {
	}

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
		if (this.organization().person().hasRole(effectivePerson, OrganizationDefinition.QueryManager,
				OrganizationDefinition.Manager)) {
			return true;
		}
		return false;
	}

	public boolean isProcessManager(EffectivePerson effectivePerson) throws Exception {
		if (effectivePerson.isManager()) {
			return true;
		}
		if (this.organization().person().hasRole(effectivePerson, OrganizationDefinition.ProcessPlatformManager,
				OrganizationDefinition.Manager)) {
			return true;
		}
		return false;
	}

	public boolean isServiceManager(EffectivePerson effectivePerson) throws Exception {
		if (effectivePerson.isManager()) {
			return true;
		}
		if (this.organization().person().hasRole(effectivePerson, OrganizationDefinition.ServiceManager,
				OrganizationDefinition.Manager)) {
			return true;
		}
		return false;
	}

	public boolean isCmsManager(EffectivePerson effectivePerson) throws Exception {
		if (effectivePerson.isManager()) {
			return true;
		}
		if (this.organization().person().hasRole(effectivePerson, OrganizationDefinition.CMSManager,
				OrganizationDefinition.Manager)) {
			return true;
		}
		return false;
	}

	public boolean isPortalManager(EffectivePerson effectivePerson) throws Exception {
		if (effectivePerson.isManager()) {
			return true;
		}
		if (this.organization().person().hasRole(effectivePerson, OrganizationDefinition.PortalManager,
				OrganizationDefinition.Manager)) {
			return true;
		}
		return false;
	}

}
