package com.x.organization.core.express;

import com.x.base.core.project.Context;

public class Organization {

	private Context context;

	public Organization(Context context) {
		this.context = context;
	}

	private CompanyAttributeFactory companyAttribute;

	public CompanyAttributeFactory companyAttribute() throws Exception {
		if (null == this.companyAttribute) {
			this.companyAttribute = new CompanyAttributeFactory(context);
		}
		return companyAttribute;
	}

	private CompanyDutyFactory companyDuty;

	public CompanyDutyFactory companyDuty() throws Exception {
		if (null == this.companyDuty) {
			this.companyDuty = new CompanyDutyFactory(context);
		}
		return companyDuty;
	}

	private CompanyFactory company;

	public CompanyFactory company() throws Exception {
		if (null == this.company) {
			this.company = new CompanyFactory(context);
		}
		return company;
	}

	private DepartmentAttributeFactory departmentAttribute;

	public DepartmentAttributeFactory departmentAttribute() throws Exception {
		if (null == this.departmentAttribute) {
			this.departmentAttribute = new DepartmentAttributeFactory(context);
		}
		return departmentAttribute;
	}

	private DepartmentDutyFactory departmentDuty;

	public DepartmentDutyFactory departmentDuty() throws Exception {
		if (null == this.departmentDuty) {
			this.departmentDuty = new DepartmentDutyFactory(context);
		}
		return departmentDuty;
	}

	private DepartmentFactory department;

	public DepartmentFactory department() throws Exception {
		if (null == this.department) {
			this.department = new DepartmentFactory(context);
		}
		return department;
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

	private PersonFactory person;

	public PersonFactory person() throws Exception {
		if (null == this.person) {
			this.person = new PersonFactory(context);
		}
		return person;
	}

	private RoleFactory role;

	public RoleFactory role() throws Exception {
		if (null == this.role) {
			this.role = new RoleFactory(context);
		}
		return role;
	}
}
