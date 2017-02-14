package com.x.organization.assemble.express;

import com.x.base.core.container.EntityManagerContainer;
import com.x.organization.assemble.express.factory.CompanyAttributeFactory;
import com.x.organization.assemble.express.factory.CompanyDutyFactory;
import com.x.organization.assemble.express.factory.CompanyFactory;
import com.x.organization.assemble.express.factory.DepartmentAttributeFactory;
import com.x.organization.assemble.express.factory.DepartmentDutyFactory;
import com.x.organization.assemble.express.factory.DepartmentFactory;
import com.x.organization.assemble.express.factory.GroupFactory;
import com.x.organization.assemble.express.factory.IdentityFactory;
import com.x.organization.assemble.express.factory.PersonAttributeFactory;
import com.x.organization.assemble.express.factory.PersonFactory;
import com.x.organization.assemble.express.factory.RoleFactory;

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

	private DepartmentFactory department;

	public DepartmentFactory department() throws Exception {
		if (null == this.department) {
			this.department = new DepartmentFactory(this);
		}
		return department;
	}

	private DepartmentAttributeFactory departmentAttribute;

	public DepartmentAttributeFactory departmentAttribute() throws Exception {
		if (null == this.departmentAttribute) {
			this.departmentAttribute = new DepartmentAttributeFactory(this);
		}
		return departmentAttribute;
	}

	private DepartmentDutyFactory departmentDuty;

	public DepartmentDutyFactory departmentDuty() throws Exception {
		if (null == this.departmentDuty) {
			this.departmentDuty = new DepartmentDutyFactory(this);
		}
		return departmentDuty;
	}

	private CompanyFactory company;

	public CompanyFactory company() throws Exception {
		if (null == this.company) {
			this.company = new CompanyFactory(this);
		}
		return company;
	}

	private CompanyAttributeFactory companyAttribute;

	public CompanyAttributeFactory companyAttribute() throws Exception {
		if (null == this.companyAttribute) {
			this.companyAttribute = new CompanyAttributeFactory(this);
		}
		return companyAttribute;
	}

	private CompanyDutyFactory companyDuty;

	public CompanyDutyFactory companyDuty() throws Exception {
		if (null == this.companyDuty) {
			this.companyDuty = new CompanyDutyFactory(this);
		}
		return companyDuty;
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

}
