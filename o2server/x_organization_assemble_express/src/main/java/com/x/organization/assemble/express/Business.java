package com.x.organization.assemble.express;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.collections4.CollectionUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.tools.ListTools;
import com.x.organization.assemble.express.factory.GroupFactory;
import com.x.organization.assemble.express.factory.IdentityFactory;
import com.x.organization.assemble.express.factory.PersonAttributeFactory;
import com.x.organization.assemble.express.factory.PersonFactory;
import com.x.organization.assemble.express.factory.RoleFactory;
import com.x.organization.assemble.express.factory.UnitAttributeFactory;
import com.x.organization.assemble.express.factory.UnitDutyFactory;
import com.x.organization.assemble.express.factory.UnitFactory;
import com.x.organization.core.entity.Group;
import com.x.organization.core.entity.Group_;
import com.x.organization.core.entity.Identity;
import com.x.organization.core.entity.Identity_;
import com.x.organization.core.entity.Person;
import com.x.organization.core.entity.Role;
import com.x.organization.core.entity.Role_;
import com.x.organization.core.entity.Unit;

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

	public List<String> expendGroupRoleToPerson(List<String> values) throws Exception {
		List<String> groupIds = new ArrayList<>();
		List<String> roleIds = new ArrayList<>();
		List<String> personIds = new ArrayList<>();
		for (String str : values) {
			Group g = this.group().pick(str);
			if (null != g) {
				groupIds.add(g.getId());
			} else {
				Role r = this.role().pick(str);
				if (null != r) {
					roleIds.add(r.getId());
				} else {
					Person p = this.person().pick(str);
					if (null != p) {
						personIds.add(p.getId());
					}
				}
			}
		}
		personIds.addAll(this.expendGroupRoleToPerson(groupIds, roleIds));
		personIds = ListTools.trim(personIds, true, true);
		return this.person().listPersonDistinguishedNameSorted(personIds);
	}

	public List<String> expendGroupRoleToPerson(List<String> groupList, List<String> roleList) throws Exception {
		List<String> groupIds = new ArrayList<>();
		List<String> expendGroupIds = new ArrayList<>();
		List<String> personIds = new ArrayList<>();
		if (ListTools.isNotEmpty(groupList)) {
			for (String s : groupList) {
				Group g = this.group().pick(s);
				if (null != g) {
					groupIds.add(g.getId());
				}
			}
		}
		if (ListTools.isNotEmpty(roleList)) {
			for (String s : roleList) {
				Role r = this.role().pick(s);
				if (null != r) {
					groupIds.addAll(r.getGroupList());
					personIds.addAll(r.getPersonList());
				}
			}
		}
		if (ListTools.isNotEmpty(groupIds)) {
			groupIds = ListTools.trim(groupIds, true, true);
			for (String s : groupIds) {
				expendGroupIds.add(s);
				expendGroupIds.addAll(this.group().listSubNested(s));
			}
			expendGroupIds = ListTools.trim(expendGroupIds, true, true);
			EntityManager em = this.entityManagerContainer().get(Group.class);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Group> cq = cb.createQuery(Group.class);
			Root<Group> root = cq.from(Group.class);
			Predicate p = root.get(Group_.id).in(expendGroupIds);
			List<Group> os = em.createQuery(cq.select(root).where(p)).getResultList();
			for (Group o : os) {
				personIds.addAll(o.getPersonList());
			}
		}
		personIds = ListTools.trim(personIds, true, true);
		return this.person().listPersonDistinguishedNameSorted(personIds);
	}

	public List<String> expendGroupRoleToGroup(List<String> values) throws Exception {
		List<String> groupIds = new ArrayList<>();
		List<String> roleIds = new ArrayList<>();
		List<String> expendGroupIds = new ArrayList<>();
		for (String str : values) {
			Group g = this.group().pick(str);
			if (null != g) {
				groupIds.add(g.getId());
			} else {
				Role r = this.role().pick(str);
				if (null != r) {
					roleIds.add(r.getId());
				}
			}
		}
		expendGroupIds.addAll(this.expendGroupRoleToGroup(groupIds, roleIds));
		return this.group().listGroupDistinguishedNameSorted(expendGroupIds);
	}

	public List<String> expendGroupRoleToGroup(List<String> groupList, List<String> roleList) throws Exception {
		List<String> groupIds = new ArrayList<>();
		List<String> expendGroupIds = new ArrayList<>();
		if (ListTools.isNotEmpty(groupList)) {
			for (String s : groupList) {
				Group g = this.group().pick(s);
				if (null != g) {
					groupIds.add(g.getId());
				}
			}
		}
		if (ListTools.isNotEmpty(roleList)) {
			for (String s : roleList) {
				Role r = this.role().pick(s);
				if (null != r) {
					groupIds.addAll(r.getGroupList());
				}
			}
		}
		if (ListTools.isNotEmpty(groupIds)) {
			groupIds = ListTools.trim(groupIds, true, true);
			for (String s : groupIds) {
				expendGroupIds.add(s);
				expendGroupIds.addAll(this.group().listSubNested(s));
			}
			expendGroupIds = ListTools.trim(expendGroupIds, true, true);
		}
		return this.group().listGroupDistinguishedNameSorted(expendGroupIds);
	}

	public List<String> expendGroupRoleToRole(List<String> values) throws Exception {
		List<String> groupIds = new ArrayList<>();
		List<String> roleIds = new ArrayList<>();
		List<String> expendRoleIds = new ArrayList<>();
		for (String str : values) {
			Group g = this.group().pick(str);
			if (null != g) {
				groupIds.add(g.getId());
			} else {
				Role r = this.role().pick(str);
				if (null != r) {
					roleIds.add(r.getId());
				}
			}
		}
		expendRoleIds.addAll(this.expendGroupRoleToRole(groupIds, roleIds));
		return this.role().listRoleDistinguishedNameSorted(expendRoleIds);
	}

	public List<String> expendGroupRoleToRole(List<String> groupList, List<String> roleList) throws Exception {
		List<String> groupIds = new ArrayList<>();
		List<String> expendGroupIds = new ArrayList<>();
		List<String> roleIds = new ArrayList<>();
		if (ListTools.isNotEmpty(groupList)) {
			for (String s : groupList) {
				Group g = this.group().pick(s);
				if (null != g) {
					groupIds.add(g.getId());
				}
			}
		}
		if (ListTools.isNotEmpty(roleList)) {
			for (String s : roleList) {
				Role r = this.role().pick(s);
				if (null != r) {
					roleIds.add(r.getId());
					groupIds.addAll(r.getGroupList());
				}
			}
		}
		if (ListTools.isNotEmpty(groupIds)) {
			groupIds = ListTools.trim(groupIds, true, true);
			for (String s : groupIds) {
				expendGroupIds.add(s);
				expendGroupIds.addAll(this.group().listSubNested(s));
			}
			expendGroupIds = ListTools.trim(expendGroupIds, true, true);
			EntityManager em = this.entityManagerContainer().get(Role.class);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Role> cq = cb.createQuery(Role.class);
			Root<Role> root = cq.from(Role.class);
			Predicate p = root.get(Role_.groupList).in(expendGroupIds);
			List<Role> os = em.createQuery(cq.select(root).where(p)).getResultList();
			for (Role o : os) {
				roleIds.add(o.getId());
			}
		}
		roleIds = ListTools.trim(roleIds, true, true);
		return this.role().listRoleDistinguishedNameSorted(roleIds);
	}

	public List<String> expendUnitToUnit(List<String> unitList) throws Exception {
		List<String> unitIds = new ArrayList<>();
		List<String> expendUnitIds = new ArrayList<>();
		if (ListTools.isNotEmpty(unitList)) {
			for (String s : unitList) {
				Unit u = this.unit().pick(s);
				if (null != u) {
					unitIds.add(u.getId());
				}
			}
		}
		if (ListTools.isNotEmpty(unitIds)) {
			unitIds = ListTools.trim(unitIds, true, true);
			for (String s : unitIds) {
				expendUnitIds.add(s);
				expendUnitIds.addAll(this.unit().listSubNested(s));
			}
		}
		expendUnitIds = ListTools.trim(expendUnitIds, true, true);
		return this.unit().listUnitDistinguishedNameSorted(expendUnitIds);
	}

	public List<String> expendUnitToIdentityId(List<String> unitList) throws Exception {
		List<String> unitIds = new ArrayList<>();
		List<String> expendUnitIds = new ArrayList<>();
		if (ListTools.isNotEmpty(unitList)) {
			for (String s : unitList) {
				Unit u = this.unit().pick(s);
				if (null != u) {
					unitIds.add(u.getId());
				}
			}
		}
		if (ListTools.isNotEmpty(unitIds)) {
			unitIds = ListTools.trim(unitIds, true, true);
			for (String s : unitIds) {
				expendUnitIds.add(s);
				expendUnitIds.addAll(this.unit().listSubNested(s));
			}
		}
		expendUnitIds = ListTools.trim(expendUnitIds, true, true);
		EntityManager em = this.entityManagerContainer().get(Identity.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Identity> root = cq.from(Identity.class);
		Predicate p = root.get(Identity_.unit).in(expendUnitIds);
		List<String> identityIdList = em.createQuery(cq.select(root.get(Identity_.id)).where(p)).getResultList();
		identityIdList = ListTools.trim(identityIdList, true, true);
		return identityIdList;
	}

	public List<String> expendUnitToIdentity(List<String> unitList) throws Exception {
		return this.identity().listIdentityDistinguishedNameSorted(expendUnitToIdentityId(unitList));
	}

	public List<String> expendUnitToPersonId(List<String> unitList) throws Exception {
		if (ListTools.isEmpty(unitList)) {
			return new ArrayList<String>();
		}
		List<String> identityIds = this.expendUnitToIdentityId(unitList);
		if (ListTools.isEmpty(identityIds)) {
			return new ArrayList<String>();
		}
		EntityManager em = this.entityManagerContainer().get(Identity.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Identity> root = cq.from(Identity.class);
		Predicate p = cb.isMember(root.get(Identity_.id), cb.literal(identityIds));
		List<String> personIds = em.createQuery(cq.select(root.get(Identity_.person)).where(p)).getResultList();
		personIds = ListTools.trim(personIds, true, true);
		return personIds;
	}

	public List<String> expendUnitToPerson(List<String> unitList) throws Exception {
		return this.person().listPersonDistinguishedNameSorted(expendUnitToPersonId(unitList));
	}

	public boolean hasAnyRole(EffectivePerson effectivePerson, String... roleFlags) throws Exception {
		/** 如果不加这个xadmin会报错 */
		if (effectivePerson.isManager()) {
			return true;
		}
		Person person = this.person().pick(effectivePerson.getDistinguishedName());
		if (null != person) {
			List<String> groupIds = this.group().listSupNestedWithPerson(person.getId());
			if (null != person) {
				List<Role> roles = this.role().pick(Arrays.asList(roleFlags));
				for (Role o : roles) {
					if (o.getPersonList().contains(person.getId())) {
						return true;
					}
					if (CollectionUtils.containsAny(o.getGroupList(), groupIds)) {
						return true;
					}
				}

			}
		}
		return false;
	}

}
