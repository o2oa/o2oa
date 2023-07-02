package com.x.component.assemble.control;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.component.assemble.control.factory.ComponentFactory;
import com.x.organization.core.express.Organization;

public class Business {

	private EntityManagerContainer emc;

	public Business(EntityManagerContainer emc) {
		this.emc = emc;
	}

	public EntityManagerContainer entityManagerContainer() {
		return this.emc;
	}

	private ComponentFactory component;

	public ComponentFactory component() throws Exception {
		if (null == this.component) {
			this.component = new ComponentFactory(this);
		}
		return component;
	}

	private Organization organization;

	public Organization organization() {
		if (null == this.organization) {
			this.organization = new Organization(ThisApplication.context());
		}
		return organization;
	}

	public boolean editable(EffectivePerson effectivePerson) throws Exception {
		if (effectivePerson.isManager()) {
			return true;
		}
		return this.organization().person().hasRole(effectivePerson, OrganizationDefinition.Manager);
	}
}
