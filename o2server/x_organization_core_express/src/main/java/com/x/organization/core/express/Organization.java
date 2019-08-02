package com.x.organization.core.express;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.x.base.core.project.AbstractContext;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.organization.core.express.empower.EmpowerFactory;
import com.x.organization.core.express.empowerlog.EmpowerLogFactory;
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

	private EmpowerFactory empower;

	public EmpowerFactory empower() throws Exception {
		if (null == this.empower) {
			this.empower = new EmpowerFactory(context);
		}
		return empower;
	}

	private EmpowerLogFactory empowerLog;

	public EmpowerLogFactory empowerLog() throws Exception {
		if (null == this.empowerLog) {
			this.empowerLog = new EmpowerLogFactory(context);
		}
		return empowerLog;
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

	public ClassifyDistinguishedName classifyDistinguishedNames(Collection<String> distinguishedNames) {
		ClassifyDistinguishedName o = new ClassifyDistinguishedName();
		if (null != distinguishedNames) {
			for (String v : distinguishedNames) {
				if (OrganizationDefinition.isIdentityDistinguishedName(v)) {
					o.getIdentityList().add(v);
				} else if (OrganizationDefinition.isPersonDistinguishedName(v)) {
					o.getPersonList().add(v);
				} else if (OrganizationDefinition.isUnitDistinguishedName(v)) {
					o.getUnitList().add(v);
				} else if (OrganizationDefinition.isRoleDistinguishedName(v)) {
					o.getRoleList().add(v);
				} else if (OrganizationDefinition.isGroupDistinguishedName(v)) {
					o.getGroupList().add(v);
				} else if (OrganizationDefinition.isPersonAttributeDistinguishedName(v)) {
					o.getPersonAttributeList().add(v);
				} else if (OrganizationDefinition.isUnitAttributeDistinguishedName(v)) {
					o.getUnitAttributeList().add(v);
				} else if (OrganizationDefinition.isUnitDutyDistinguishedName(v)) {
					o.getUnitDutyList().add(v);
				}
			}
		}
		return o;
	}

	public static class ClassifyDistinguishedName extends GsonPropertyObject {

		private List<String> groupList = new ArrayList<>();
		private List<String> identityList = new ArrayList<>();
		private List<String> personList = new ArrayList<>();
		private List<String> personAttributeList = new ArrayList<>();
		private List<String> roleList = new ArrayList<>();
		private List<String> unitList = new ArrayList<>();
		private List<String> unitAttributeList = new ArrayList<>();
		private List<String> unitDutyList = new ArrayList<>();

		public List<String> getGroupList() {
			return groupList;
		}

		public void setGroupList(List<String> groupList) {
			this.groupList = groupList;
		}

		public List<String> getIdentityList() {
			return identityList;
		}

		public void setIdentityList(List<String> identityList) {
			this.identityList = identityList;
		}

		public List<String> getPersonList() {
			return personList;
		}

		public void setPersonList(List<String> personList) {
			this.personList = personList;
		}

		public List<String> getPersonAttributeList() {
			return personAttributeList;
		}

		public void setPersonAttributeList(List<String> personAttributeList) {
			this.personAttributeList = personAttributeList;
		}

		public List<String> getRoleList() {
			return roleList;
		}

		public void setRoleList(List<String> roleList) {
			this.roleList = roleList;
		}

		public List<String> getUnitList() {
			return unitList;
		}

		public void setUnitList(List<String> unitList) {
			this.unitList = unitList;
		}

		public List<String> getUnitAttributeList() {
			return unitAttributeList;
		}

		public void setUnitAttributeList(List<String> unitAttributeList) {
			this.unitAttributeList = unitAttributeList;
		}

		public List<String> getUnitDutyList() {
			return unitDutyList;
		}

		public void setUnitDutyList(List<String> unitDutyList) {
			this.unitDutyList = unitDutyList;
		}

	}

//	public PersonDetail detail(EffectivePerson effectivePerson) throws Exception {
//		PersonDetail o = new PersonDetail();
//		o.setPerson(effectivePerson.getDistinguishedName());
//		o.setIdentityList(identity().listWithPerson(effectivePerson));
//		o.setUnitList(unit().listWithPerson(effectivePerson));
//		o.setUnitAllList(unit().listWithPersonSupNested(effectivePerson));
//		o.setGroupList(group().listWithPerson(effectivePerson.getDistinguishedName()));
//		o.setRoleList(role().listWithPerson(effectivePerson));
//		return o;
//	}
//
//	public PersonDetail detail(String name) throws Exception {
//		PersonDetail o = new PersonDetail();
//		String person = this.person().get(name);
//		o.setPerson(person);
//		o.setIdentityList(identity().listWithPerson(person));
//		o.setUnitList(unit().listWithPerson(person));
//		o.setUnitAllList(unit().listWithPersonSupNested(person));
//		o.setGroupList(group().listWithPerson(person));
//		o.setRoleList(role().listWithPerson(person));
//		return o;
//	}

}
