package com.x.organization.core.express;

import com.x.base.core.project.AbstractContext;
import com.x.organization.core.express.group.GroupFactory;
import com.x.organization.core.express.identity.IdentityFactory;
import com.x.organization.core.express.person.PersonFactory;
import com.x.organization.core.express.personattribute.PersonAttributeFactory;
import com.x.organization.core.express.role.RoleFactory;
import com.x.organization.core.express.unit.UnitFactory;
import com.x.organization.core.express.unitattribute.UnitAttributeFactory;
import com.x.organization.core.express.unitduty.UnitDutyFactory;

public class Organization {

	private AbstractContext context;

	public Organization(AbstractContext context) {
		this.context = context;
	}

	private PersonFactory person;

	public PersonFactory person() throws Exception {
		if (null == this.person) {
			this.person = new PersonFactory(context);
		}
		return person;
	}

	private GroupFactory group;

	public GroupFactory group() throws Exception {
		if (null == this.group) {
			this.group = new GroupFactory(context);
		}
		return group;
	}

	private IdentityFactory identity;

	public IdentityFactory identity() throws Exception {
		if (null == this.identity) {
			this.identity = new IdentityFactory(context);
		}
		return identity;
	}

	private PersonAttributeFactory personAttribute;

	public PersonAttributeFactory personAttribute() throws Exception {
		if (null == this.personAttribute) {
			this.personAttribute = new PersonAttributeFactory(context);
		}
		return personAttribute;
	}

	private RoleFactory role;

	public RoleFactory role() throws Exception {
		if (null == this.role) {
			this.role = new RoleFactory(context);
		}
		return role;
	}

	private UnitFactory unit;

	public UnitFactory unit() throws Exception {
		if (null == this.unit) {
			this.unit = new UnitFactory(context);
		}
		return unit;
	}

	private UnitAttributeFactory unitAttribute;

	public UnitAttributeFactory unitAttribute() throws Exception {
		if (null == this.unitAttribute) {
			this.unitAttribute = new UnitAttributeFactory(context);
		}
		return unitAttribute;
	}

	private UnitDutyFactory unitDuty;

	public UnitDutyFactory unitDuty() throws Exception {
		if (null == this.unitDuty) {
			this.unitDuty = new UnitDutyFactory(context);
		}
		return unitDuty;
	}

	/** 根据个人身份获取组织 */
	/** 表示获取第几层的组织 */
	public String listSupUnit(String name, Integer nested) throws Exception {
		return this.unit().getWithIdentityWithLevel(name, nested);
	}

	/** 字符串 表示获取指定类型的组织 */
	public String listSupUnit(String name, String nested) throws Exception {
		return this.unit().getWithIdentityWithType(name, nested);
	}

	/** 直接获取所在的组织 */
	public String listSupUnit(String name) throws Exception {
		return this.unit().getWithIdentity(name);
	}

}
