package com.x.organization.assemble.personal;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.project.cache.ApplicationCache;
import com.x.base.core.project.instrument.Instrument;
import com.x.organization.assemble.personal.factory.GroupFactory;
import com.x.organization.assemble.personal.factory.IdentityFactory;
import com.x.organization.assemble.personal.factory.PersonAttributeFactory;
import com.x.organization.assemble.personal.factory.PersonFactory;
import com.x.organization.assemble.personal.factory.RoleFactory;
import com.x.organization.assemble.personal.factory.UnitAttributeFactory;
import com.x.organization.assemble.personal.factory.UnitDutyFactory;
import com.x.organization.assemble.personal.factory.UnitFactory;
import com.x.organization.core.entity.Group;
import com.x.organization.core.entity.Identity;
import com.x.organization.core.entity.Person;
import com.x.organization.core.entity.PersonAttribute;
import com.x.organization.core.entity.Role;
import com.x.organization.core.entity.Unit;
import com.x.organization.core.entity.UnitAttribute;
import com.x.organization.core.entity.UnitDuty;

import net.sf.ehcache.Ehcache;

public class Business {

	private EntityManagerContainer emc;

	private Ehcache cache;

	public Business(EntityManagerContainer emc) throws Exception {
		this.emc = emc;
		this.cache = ApplicationCache.instance().getCache(Group.class, Role.class, Person.class, PersonAttribute.class,
				Unit.class, UnitDuty.class, UnitAttribute.class, Identity.class);
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

	private PersonAttributeFactory personAttribute;

	public PersonAttributeFactory personAttribute() throws Exception {
		if (null == this.personAttribute) {
			this.personAttribute = new PersonAttributeFactory(this);
		}
		return personAttribute;
	}

	private IdentityFactory identity;

	public IdentityFactory identity() throws Exception {
		if (null == this.identity) {
			this.identity = new IdentityFactory(this);
		}
		return identity;
	}

	private GroupFactory group;

	public GroupFactory group() throws Exception {
		if (null == this.group) {
			this.group = new GroupFactory(this);
		}
		return group;
	}

	private RoleFactory role;

	public RoleFactory role() throws Exception {
		if (null == this.role) {
			this.role = new RoleFactory(this);
		}
		return role;
	}

	private UnitFactory unit;

	public UnitFactory unit() throws Exception {
		if (null == this.unit) {
			this.unit = new UnitFactory(this);
		}
		return unit;
	}

	private UnitAttributeFactory unitAttribute;

	public UnitAttributeFactory unitAttribute() throws Exception {
		if (null == this.unitAttribute) {
			this.unitAttribute = new UnitAttributeFactory(this);
		}
		return unitAttribute;
	}

	private UnitDutyFactory unitDuty;

	public UnitDutyFactory unitDuty() throws Exception {
		if (null == this.unitDuty) {
			this.unitDuty = new UnitDutyFactory(this);
		}
		return unitDuty;
	}

	private Instrument instrument;

	public Instrument instrument() throws Exception {
		if (null == this.instrument) {
			this.instrument = new Instrument();
		}
		return instrument;
	}

	public Ehcache cache() {
		return cache;
	}

}
