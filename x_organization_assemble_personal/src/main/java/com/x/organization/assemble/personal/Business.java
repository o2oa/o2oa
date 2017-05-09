package com.x.organization.assemble.personal;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.project.instrument.Instrument;
import com.x.organization.assemble.personal.factory.IdentityFactory;
import com.x.organization.assemble.personal.factory.PersonFactory;
import com.x.organization.core.express.Organization;

public class Business {

	private EntityManagerContainer emc;

	public Business(EntityManagerContainer emc) throws Exception {
		this.emc = emc;
	}

	public EntityManagerContainer entityManagerContainer() {
		return this.emc;
	}

	private PersonFactory person;

	public PersonFactory person() throws Exception {
		if (null == this.person) {
			this.person = new PersonFactory(this);
		}
		return person;
	}

	private IdentityFactory identity;

	public IdentityFactory identity() throws Exception {
		if (null == this.identity) {
			this.identity = new IdentityFactory(this);
		}
		return identity;
	}

	private Instrument instrument;

	public Instrument instrument() throws Exception {
		if (null == this.instrument) {
			this.instrument = new Instrument();
		}
		return instrument;
	}

	private Organization organization;

	public Organization organization() throws Exception {
		if (null == this.organization) {
			this.organization = new Organization(ThisApplication.context());

		}
		return organization;
	}

}
