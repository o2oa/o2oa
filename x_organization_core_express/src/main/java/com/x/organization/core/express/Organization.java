package com.x.organization.core.express;

import com.x.organization.core.express.factory.CompanyAttributeFactory;
import com.x.organization.core.express.factory.CompanyDutyFactory;
import com.x.organization.core.express.factory.CompanyFactory;
import com.x.organization.core.express.factory.DepartmentAttributeFactory;
import com.x.organization.core.express.factory.DepartmentDutyFactory;
import com.x.organization.core.express.factory.DepartmentFactory;
import com.x.organization.core.express.factory.GroupFactory;
import com.x.organization.core.express.factory.IdentityFactory;
import com.x.organization.core.express.factory.PersonAttributeFactory;
import com.x.organization.core.express.factory.PersonFactory;
import com.x.organization.core.express.factory.RoleFactory;

public class Organization {

	private CompanyAttributeFactory companyAttribute;

	public CompanyAttributeFactory companyAttribute() throws Exception {
		if (null == this.companyAttribute) {
			this.companyAttribute = new CompanyAttributeFactory();
		}
		return companyAttribute;
	}

	private CompanyDutyFactory companyDuty;

	public CompanyDutyFactory companyDuty() throws Exception {
		if (null == this.companyDuty) {
			this.companyDuty = new CompanyDutyFactory();
		}
		return companyDuty;
	}

	private CompanyFactory company;

	public CompanyFactory company() throws Exception {
		if (null == this.company) {
			this.company = new CompanyFactory();
		}
		return company;
	}

	private DepartmentAttributeFactory departmentAttribute;

	public DepartmentAttributeFactory departmentAttribute() throws Exception {
		if (null == this.departmentAttribute) {
			this.departmentAttribute = new DepartmentAttributeFactory();
		}
		return departmentAttribute;
	}

	private DepartmentDutyFactory departmentDuty;

	public DepartmentDutyFactory departmentDuty() throws Exception {
		if (null == this.departmentDuty) {
			this.departmentDuty = new DepartmentDutyFactory();
		}
		return departmentDuty;
	}

	private DepartmentFactory department;

	public DepartmentFactory department() throws Exception {
		if (null == this.department) {
			this.department = new DepartmentFactory();
		}
		return department;
	}

	private GroupFactory group;

	public GroupFactory group() throws Exception {
		if (null == this.group) {
			this.group = new GroupFactory();
		}
		return group;
	}

	private IdentityFactory identity;

	public IdentityFactory identity() throws Exception {
		if (null == this.identity) {
			this.identity = new IdentityFactory();
		}
		return identity;
	}

	private PersonAttributeFactory personAttribute;

	public PersonAttributeFactory personAttribute() throws Exception {
		if (null == this.personAttribute) {
			this.personAttribute = new PersonAttributeFactory();
		}
		return personAttribute;
	}

	private PersonFactory person;

	public PersonFactory person() throws Exception {
		if (null == this.person) {
			this.person = new PersonFactory();
		}
		return person;
	}

	private RoleFactory role;

	public RoleFactory role() throws Exception {
		if (null == this.role) {
			this.role = new RoleFactory();
		}
		return role;
	}
}
