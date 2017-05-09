package com.x.organization.assemble.control;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.exception.ExceptionWhen;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.project.instrument.Instrument;
import com.x.base.core.role.RoleDefinition;
import com.x.organization.assemble.control.factory.CompanyAttributeFactory;
import com.x.organization.assemble.control.factory.CompanyDutyFactory;
import com.x.organization.assemble.control.factory.CompanyFactory;
import com.x.organization.assemble.control.factory.DepartmentAttributeFactory;
import com.x.organization.assemble.control.factory.DepartmentDutyFactory;
import com.x.organization.assemble.control.factory.DepartmentFactory;
import com.x.organization.assemble.control.factory.GroupFactory;
import com.x.organization.assemble.control.factory.IdentityFactory;
import com.x.organization.assemble.control.factory.PersonAttributeFactory;
import com.x.organization.assemble.control.factory.PersonFactory;
import com.x.organization.assemble.control.factory.RoleFactory;
import com.x.organization.core.entity.Person;
import com.x.organization.core.entity.Role;

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

	public boolean companyEditAvailable(EffectivePerson effectivePerson, String companyId) throws Exception {
		if (effectivePerson.isManager()) {
			return true;
		}
		if (this.personHasRole(effectivePerson.getName(), RoleDefinition.CompanyCreator)) {
			return true;
		}
		if (StringUtils.isNotEmpty(companyId)) {
			/* 不是顶级，判断其上层公司的权限 */
			String personId = this.person().getWithName(effectivePerson.getName(), null);
			List<String> hasControls = this.company().listWithControl(personId);
			List<String> companies = this.company().listSupNested(companyId);
			companies.add(companyId);
			if (CollectionUtils.containsAny(hasControls, companies)) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	public boolean roleEditAvailable(EffectivePerson effectivePerson) throws Exception {
		return effectivePerson.isManager();
	}

	public void personCreateAvailable(EffectivePerson effectivePerson, ExceptionWhen exceptionWhen) throws Exception {
		boolean available = this.personCreateAvailable(effectivePerson);
		if ((!available) && ExceptionWhen.not_allow.equals(exceptionWhen)) {
			throw new Exception("person{name:" + effectivePerson.getName() + "} has sufficient permissions");
		}
	}

	public boolean personCreateAvailable(EffectivePerson effectivePerson) throws Exception {
		if (effectivePerson.isManager()) {
			return true;
		}
		if (this.personHasRole(effectivePerson.getName(), RoleDefinition.Manager)) {
			return true;
		}
		if (this.personHasRole(effectivePerson.getName(), RoleDefinition.PersonManager)) {
			return true;
		}
		if (this.personHasRole(effectivePerson.getName(), RoleDefinition.CompanyCreator)) {
			return true;
		}
		return false;
	}

	public boolean personUpdateAvailable(EffectivePerson effectivePerson, Person person) throws Exception {
		if (effectivePerson.isManager()) {
			return true;
		}
		if (this.personHasRole(effectivePerson.getName(), RoleDefinition.PersonManager)) {
			return true;
		}
		List<Person> people = emc.fetchAttribute(person.getControllerList(), Person.class, "name");
		for (Person o : people) {
			if (StringUtils.equalsIgnoreCase(o.getName(), effectivePerson.getName())) {
				return true;
			}
		}
		return false;
	}

	public boolean groupEditAvailable(EffectivePerson effectivePerson) throws Exception {
		if (effectivePerson.isManager()) {
			return true;
		}
		if (this.personHasRole(effectivePerson.getName(), RoleDefinition.GroupCreator)) {
			return true;
		}
		return false;
	}

	public boolean personHasRole(String personName, String roleName) throws Exception {
		String personId = this.person().getWithName(personName, null);
		if (StringUtils.isEmpty(personId)) {
			throw new Exception("person{name:" + personName + "} not existed.");
		}
		String roleId = this.role().getWithName(roleName);
		if (StringUtils.isEmpty(roleId)) {
			throw new Exception("role{name:" + roleId + "} not existed.");
		}
		Role role = emc.find(roleId, Role.class);
		if (role.getPersonList().contains(personId)) {
			return true;
		}
		if (CollectionUtils.containsAny(role.getGroupList(), this.group().listSupNestedWithPerson(personId))) {
			return true;
		}
		return false;
	}

}
