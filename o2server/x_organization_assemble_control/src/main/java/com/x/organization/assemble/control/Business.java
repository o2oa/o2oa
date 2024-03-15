package com.x.organization.assemble.control;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.organization.core.entity.enums.PersonStatusEnum;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.set.ListOrderedSet;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.bean.tuple.Quintuple;
import com.x.base.core.project.cache.Cache.CacheCategory;
import com.x.base.core.project.gson.XGsonBuilder;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.instrument.Instrument;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.base.core.project.tools.ListTools;
import com.x.organization.assemble.control.factory.GroupFactory;
import com.x.organization.assemble.control.factory.IdentityFactory;
import com.x.organization.assemble.control.factory.PermissionSettingFactory;
import com.x.organization.assemble.control.factory.PersonAttributeFactory;
import com.x.organization.assemble.control.factory.PersonCardFactory;
import com.x.organization.assemble.control.factory.PersonFactory;
import com.x.organization.assemble.control.factory.RoleFactory;
import com.x.organization.assemble.control.factory.UnitAttributeFactory;
import com.x.organization.assemble.control.factory.UnitDutyFactory;
import com.x.organization.assemble.control.factory.UnitFactory;
import com.x.organization.core.entity.Group;
import com.x.organization.core.entity.Group_;
import com.x.organization.core.entity.Identity;
import com.x.organization.core.entity.Identity_;
import com.x.organization.core.entity.Person;
import com.x.organization.core.entity.PersonAttribute;
import com.x.organization.core.entity.Person_;
import com.x.organization.core.entity.Role;
import com.x.organization.core.entity.Role_;
import com.x.organization.core.entity.Unit;
import com.x.organization.core.entity.UnitAttribute;
import com.x.organization.core.entity.UnitDuty;

public class Business {

    private EntityManagerContainer emc;

    private CacheCategory cacheCategory;

    public Business(EntityManagerContainer emc) throws Exception {
        this.emc = emc;
        this.cacheCategory = new CacheCategory(Group.class, Role.class, Person.class, PersonAttribute.class, Unit.class,
                UnitDuty.class, UnitAttribute.class, Identity.class);
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

    private PersonCardFactory personCard;

    public PersonCardFactory personCard() throws Exception {
        if (null == this.personCard) {
            this.personCard = new PersonCardFactory(this);
        }
        return personCard;
    }

    private PermissionSettingFactory permissionSetting;

    public PermissionSettingFactory permissionSetting() throws Exception {
        if (null == this.permissionSetting) {
            this.permissionSetting = new PermissionSettingFactory(this);
        }
        return permissionSetting;
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

    public RoleFactory role() throws ExceptionRoleFactory {
        if (null == this.role) {
            try {
                this.role = new RoleFactory(this);
            } catch (Exception e) {
                throw new ExceptionRoleFactory(e);
            }
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

    public boolean hasAnyRole(EffectivePerson effectivePerson, String... roleFlags) throws Exception {
        /** 如果不加这个xadmin会报错 */
        if (effectivePerson.isManager()) {
            return true;
        }
        Person p = this.person().pick(effectivePerson.getDistinguishedName());
        if (null != p) {
            List<String> groupIds = this.group().listSupNestedWithPerson(p.getId());
            List<Role> roles = this.role().pick(Arrays.asList(roleFlags));
            for (Role o : roles) {
                if (o.getPersonList().contains(p.getId())) {
                    return true;
                }
                if (CollectionUtils.containsAny(o.getGroupList(), groupIds)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean editable(EffectivePerson effectivePerson, Group group) throws Exception {
        if (effectivePerson.isSecurityManager()) {
            return true;
        }
        if (this.hasAnyRole(effectivePerson, OrganizationDefinition.OrganizationManager,
                OrganizationDefinition.GroupManager, OrganizationDefinition.SystemManager,
                OrganizationDefinition.SecurityManager)) {
            return true;
        }
        return false;
    }

    public boolean editable(EffectivePerson effectivePerson, Unit unit) throws Exception {
        if (effectivePerson.isManager()) {
            return true;
        }
        if (this.hasAnyRole(effectivePerson, OrganizationDefinition.Manager, OrganizationDefinition.OrganizationManager,
                OrganizationDefinition.UnitManager, OrganizationDefinition.SystemManager,
                OrganizationDefinition.SecurityManager)) {
            return true;
        }
        if (ListTools.isNotEmpty(unit.getControllerList())) {
            List<Person> os = this.person().pick(unit.getControllerList());
            List<String> list = ListTools.extractProperty(os, JpaObject.DISTINGUISHEDNAME, String.class, true, true);
            if (ListTools.contains(list, effectivePerson.getDistinguishedName())) {
                return true;
            }
        }
        for (Unit u : unit().pick(unit().listSupNested(unit.getId()))) {
            List<Person> os = this.person().pick(u.getControllerList());
            List<String> list = ListTools.extractProperty(os, JpaObject.DISTINGUISHEDNAME, String.class, true, true);
            if (ListTools.contains(list, effectivePerson.getDistinguishedName())) {
                return true;
            }
        }
        return false;
    }

    public boolean editable(EffectivePerson effectivePerson, Person person) throws Exception {
        if (effectivePerson.isManager()) {
            return true;
        }
        if (this.hasAnyRole(effectivePerson, OrganizationDefinition.Manager, OrganizationDefinition.OrganizationManager,
                OrganizationDefinition.PersonManager, OrganizationDefinition.SystemManager,
                OrganizationDefinition.SecurityManager)) {
            return true;
        }
        return false;
    }

    public boolean editable(EffectivePerson effectivePerson, Role role) throws Exception {
        if (effectivePerson.isSecurityManager()) {
            return true;
        }
        if(role!=null && OrganizationDefinition.Manager.equals(role.getName())){
            return false;
        }
        if (this.hasAnyRole(effectivePerson, OrganizationDefinition.Manager, OrganizationDefinition.OrganizationManager,
                OrganizationDefinition.RoleManager, OrganizationDefinition.SecurityManager)) {
            return true;
        }
        return false;
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
        return personIds;
    }

    public List<String> expendGroupRoleToPerson(List<String> groupList, List<String> roleList) throws Exception {
        List<String> groupIds = new ArrayList<>();
        List<String> expendGroupIds = new ArrayList<>();
        List<String> personIds = new ArrayList<>();
        for (String s : ListTools.trim(groupList, true, true)) {
            Group g = this.group().pick(s);
            if (null != g) {
                groupIds.add(g.getId());
            }
        }
        for (String s : ListTools.trim(roleList, true, true)) {
            Role r = this.role().pick(s);
            if (null != r) {
                groupIds.addAll(r.getGroupList());
                personIds.addAll(r.getPersonList());
            }
        }
        for (String s : ListTools.trim(groupIds, true, true)) {
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
        personIds = ListTools.trim(personIds, true, true);
        return personIds;
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
        return expendGroupIds;
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
        return expendGroupIds;
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
        return expendRoleIds;
    }

    public List<String> expendGroupRoleToRole(List<String> groupList, List<String> roleList) throws Exception {
        List<String> groupIds = new ArrayList<>();
        List<String> expendGroupIds = new ArrayList<>();
        List<String> roleIds = new ArrayList<>();
        for (String s : ListTools.trim(groupList, true, true)) {
            Group g = this.group().pick(s);
            if (null != g) {
                groupIds.add(g.getId());
            }
        }
        for (String s : ListTools.trim(roleList, true, true)) {
            Role r = this.role().pick(s);
            if (null != r) {
                roleIds.add(r.getId());
                groupIds.addAll(r.getGroupList());
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
        return roleIds;
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
        return expendUnitIds;
    }

    public List<String> expendUnitToIdentity(List<String> unitList) throws Exception {
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
        List<String> os = em.createQuery(cq.select(root.get(Identity_.id)).where(p)).getResultList();
        os = ListTools.trim(os, true, true);
        return os;
    }

    public List<String> expendGroupToIdentity(List<String> groupList) throws Exception {
        List<Group> groups = new ArrayList<>();
        List<Group> expendGroups = new ArrayList<>();
        if (ListTools.isNotEmpty(groupList)) {
            groupList = ListTools.trim(groupList, true, true);
            for (String s : groupList) {
                Group g = this.group().pick(s);
                if (null != g) {
                    groups.add(g);
                }
            }
        }
        if (ListTools.isNotEmpty(groups)) {
            for (Group g : groups) {
                expendGroups.add(g);
                expendGroups.addAll(this.group().listSubNestedObject(g));
            }
        }
        ListOrderedSet<String> set = new ListOrderedSet<String>();
        for (Group g : groups) {
            if (ListTools.isNotEmpty(g.getIdentityList())) {
                set.addAll(g.getIdentityList());
            }
        }
        return set.asList();
    }

    public CacheCategory cache() {
        return cacheCategory;
    }

    public List<Person> listPersonWithUnit(String unitFlag) throws Exception {
        Unit u = this.unit().pick(unitFlag);
        EntityManager em = this.entityManagerContainer().get(Identity.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<String> cq = cb.createQuery(String.class);
        Root<Identity> root = cq.from(Identity.class);
        Predicate p = cb.equal(root.get(Identity_.unit), u.getId());
        List<String> os = em.createQuery(cq.select(root.get(Identity_.person)).where(p)).getResultList();
        return this.person().pick(os);
    }

    public List<Unit> listTopUnitWithPerson(String personFlag) throws Exception {
        List<Unit> os = new ArrayList<>();
        Person p = this.person().pick(personFlag);
        if (null != p) {
            List<Identity> identities = emc.listEqual(Identity.class, Identity.person_FIELDNAME, p.getId());
            List<String> unitIds = new ArrayList<>();
            for (String id : ListTools.extractField(identities, Identity.unit_FIELDNAME, String.class, true, true)) {
                unitIds.add(id);
                unitIds.addAll(unit().listSupNested(id));
            }
            // 加入预设的顶层组织
            unitIds.addAll(p.getTopUnitList());
            unitIds = ListTools.trim(unitIds, true, true);
            for (Unit u : this.unit().pick(unitIds)) {
                if (Objects.deepEquals(Unit.TOP_LEVEL, u.getLevel())) {
                    os.add(u);
                }
            }
        }
        return os;
    }

    public boolean sameTopUnit(EffectivePerson effectivePerson, String person) throws Exception {
        List<Unit> o = this.listTopUnitWithPerson(effectivePerson.getDistinguishedName());
        List<Unit> t = this.listTopUnitWithPerson(person);
        return ListTools.containsAny(o, t);
    }

    public Predicate personPredicateWithTopUnit(EffectivePerson effectivePerson, boolean all) throws Exception {
        EntityManager em = emc.get(Person.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        if (effectivePerson.isSecurityManager() || this.hasAnyRole(effectivePerson, OrganizationDefinition.Manager,
                OrganizationDefinition.OrganizationManager)) {
            if(all) {
                return cb.conjunction();
            }else{
                CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
                Root<Person> root = cq.from(Person.class);
                return cb.or(cb.isNull(root.get(Person_.status)), cb.notEqual(root.get(Person_.status), PersonStatusEnum.BAN.getValue()));
            }
        } else {
            CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
            Root<Person> root = cq.from(Person.class);
            List<Unit> units = listTopUnitWithPerson(effectivePerson.getDistinguishedName());
            List<String> ids = ListTools.extractField(units, JpaObject.id_FIELDNAME, String.class, true, true);
            Predicate p = cb.or(root.get(Person_.topUnitList).in(ids), cb.isEmpty(root.get(Person_.topUnitList)));
            if(!all){
                p = cb.and(p, cb.or(cb.isNull(root.get(Person_.status)), cb.notEqual(root.get(Person_.status), PersonStatusEnum.BAN.getValue())));
            }
            return p;
        }
    }

    public Optional<Quintuple<Collection<String>, Collection<String>, Collection<String>, Collection<String>, Collection<String>>> detailOfPerson(
            String flag, boolean fetchIdentity, boolean fetchUnit, boolean fetchGroup, boolean fetchRole,
            boolean fetchPersonAttribute)
            throws Exception {
        // identity,unit,group,role,personAttribute
        Person p = this.person().pick(flag);
        if (null == p) {
            return Optional.empty();
        }

        List<String> identityIds = new ArrayList<>();
        ListOrderedSet<String> unitIds = new ListOrderedSet<>();
        ListOrderedSet<String> groupIds = new ListOrderedSet<>();
        ListOrderedSet<String> roleIds = new ListOrderedSet<>();
        List<String> personAttributeIds = new ArrayList<>();
        if (fetchIdentity || fetchUnit || fetchGroup || fetchRole) {
            identityIds = this.entityManagerContainer().idsEqual(Identity.class, Identity.person_FIELDNAME,
                    p.getId());
        }
        if (fetchUnit || fetchGroup || fetchRole) {
            detailOfPersonUnits(unitIds, identityIds);
        }
        if (fetchGroup || fetchRole) {
            detailOfPersonGroups(groupIds, p, identityIds, unitIds);
        }
        if (fetchRole) {
            roleIds.addAll(this.entityManagerContainer().idsInOrIsMember(Role.class, Role.groupList_FIELDNAME,
                    groupIds, Role.personList_FIELDNAME, p.getId()));
        }
        if (fetchPersonAttribute) {
            personAttributeIds = this.entityManagerContainer().idsEqual(PersonAttribute.class,
                    PersonAttribute.person_FIELDNAME,
                    p.getId());
        }
        return Optional.of(Quintuple.of(identityIds, unitIds, groupIds, roleIds, personAttributeIds));
    }

    private void detailOfPersonUnits(ListOrderedSet<String> unitIds, List<String> identityIds) throws Exception {
        for (String s : identityIds) {
            Identity obj = this.identity().pick(s);
            if (null != obj) {
                unitIds.add(obj.getUnit());
                unitIds.addAll(this.unit().listSupNested(obj.getUnit()));
            }
        }
    }

    private void detailOfPersonGroups(ListOrderedSet<String> groupIds, Person p, List<String> identityIds,
            ListOrderedSet<String> unitIds) throws Exception {
        groupIds.addAll(this.entityManagerContainer().idsInOrInOrIsMember(Group.class, Group.unitList_FIELDNAME,
                unitIds, Group.identityList_FIELDNAME, identityIds, Group.personList_FIELDNAME, p.getId()));
        for (String s : new ArrayList<>(groupIds)) {
            groupIds.add(s);
            groupIds.addAll(this.group().listSupNested(s));
        }
    }
}
