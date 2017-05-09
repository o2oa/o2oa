package com.x.organization.assemble.authentication;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.project.instrument.Instrument;
import com.x.organization.assemble.authentication.factory.BindFactory;
import com.x.organization.assemble.authentication.factory.PersonFactory;
import com.x.organization.assemble.authentication.factory.RoleFactory;

public class Business {

	private EntityManagerContainer emc;

	public Business(EntityManagerContainer emc) throws Exception {
		this.emc = emc;
	}

	public EntityManagerContainer entityManagerContainer() {
		return this.emc;
	}

	private Instrument instrument;

	public Instrument instrument() throws Exception {
		if (null == this.instrument) {
			this.instrument = new Instrument();
		}
		return instrument;
	}

	private PersonFactory person;

	public PersonFactory person() throws Exception {
		if (null == this.person) {
			this.person = new PersonFactory(this);
		}
		return person;
	}

	private RoleFactory role;

	public RoleFactory role() throws Exception {
		if (null == this.role) {
			this.role = new RoleFactory(this);
		}
		return role;
	}

	private BindFactory bind;

	public BindFactory bind() throws Exception {
		if (null == this.bind) {
			this.bind = new BindFactory(this);
		}
		return bind;
	}

}
