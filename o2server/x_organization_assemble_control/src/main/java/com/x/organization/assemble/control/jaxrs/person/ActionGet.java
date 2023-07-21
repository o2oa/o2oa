package com.x.organization.assemble.control.jaxrs.person;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.bean.WrapCopier;
import com.x.base.core.project.bean.WrapCopierFactory;
import com.x.base.core.project.bean.tuple.Quintuple;
import com.x.base.core.project.cache.Cache;
import com.x.base.core.project.cache.Cache.CacheKey;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.config.Config;
import com.x.base.core.project.exception.ExceptionPersonNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.organization.assemble.control.Business;
import com.x.organization.core.entity.Group;
import com.x.organization.core.entity.Identity;
import com.x.organization.core.entity.Person;
import com.x.organization.core.entity.PersonAttribute;
import com.x.organization.core.entity.Role;
import com.x.organization.core.entity.Unit;
import com.x.organization.core.entity.UnitDuty;
import com.x.organization.core.entity.UnitDuty_;

class ActionGet extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionGet.class);

    ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag) throws Exception {

        LOGGER.debug("execute:{}, flag:{}.", effectivePerson::getDistinguishedName, () -> flag);

        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Business business = new Business(emc);
            ActionResult<Wo> result = new ActionResult<>();
            CacheKey cacheKey = new Cache.CacheKey(this.getClass(), flag);
            Optional<?> optional = CacheManager.get(business.cache(), cacheKey);

            if (optional.isPresent()) {
                result.setData((Wo) optional.get());
            } else {
                Wo wo = new Wo();
                if (Config.token().isInitialManager(flag)) {
                    // 如果是xadmin单独处理
                    Config.token().initialManagerInstance().copyTo(wo, "password");
                    result.setData(wo);
                } else {
                    Person person = business.person().pick(flag);
                    if (null == person) {
                        throw new ExceptionPersonNotExist(flag);
                    }
                    wo = Wo.copier.copy(person);
                    Optional<Quintuple<Collection<String>, Collection<String>, Collection<String>, Collection<String>, Collection<String>>> detail = business
                            .detailOfPerson(flag, true, true, true, true, true);
                    if (detail.isPresent()) {
                        this.referenceIdentity(business, wo, detail.get().first());
                        wo.setWoGroupList(business.group()
                                .sort(WoGroup.copier.copy(emc.list(Group.class, detail.get().third()))));
                        wo.setWoRoleList(business.role()
                                .sort(WoRole.copier.copy(emc.list(Role.class, detail.get().fourth()))));
                        wo.setWoPersonAttributeList(business.personAttribute()
                                .sort(WoPersonAttribute.copier
                                        .copy(emc.list(PersonAttribute.class, detail.get().fifth()))));
                    }
                }
                CacheManager.put(business.cache(), cacheKey, wo);
                result.setData(wo);
            }
            this.updateControl(effectivePerson, business, result.getData());
            this.hide(effectivePerson, business, result.getData());
            return result;
        }
    }

    private void referenceIdentity(Business business, Wo wo, Collection<String> identityIds) throws Exception {
        List<Identity> os = business.entityManagerContainer().list(Identity.class, identityIds);
        List<WoIdentity> wos = WoIdentity.copier.copy(os);
        wos = business.identity().sort(wos);
        wo.setWoIdentityList(wos);
        for (WoIdentity o : wo.getWoIdentityList()) {
            this.referenceUnit(business, o);
            this.referenceUnitDuty(business, o);
        }
    }

    private void referenceUnit(Business business, WoIdentity woIdentity) throws Exception {
        if (StringUtils.isNotEmpty(woIdentity.getUnit())) {
            Unit unit = business.unit().pick(woIdentity.getUnit());
            if (null == unit) {
                throw new ExceptionUnitNotExist(woIdentity.getUnit());
            }
            WoUnit wo = WoUnit.copier.copy(unit);
            woIdentity.setWoUnit(wo);
        }
    }

    private void referenceUnitDuty(Business business, WoIdentity woIdentity) throws Exception {
        EntityManager em = business.entityManagerContainer().get(UnitDuty.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<UnitDuty> cq = cb.createQuery(UnitDuty.class);
        Root<UnitDuty> root = cq.from(UnitDuty.class);
        Predicate p = cb.isMember(woIdentity.getId(), root.get(UnitDuty_.identityList));
        List<UnitDuty> os = em.createQuery(cq.select(root).where(p)).getResultList();
        List<WoUnitDuty> wos = WoUnitDuty.copier.copy(os);
        wos = business.unitDuty().sort(wos);
        for (WoUnitDuty woUnitDuty : wos) {
            this.referenceUnit(business, woUnitDuty);
        }
        woIdentity.setWoUnitDutyList(wos);
    }

    private void referenceUnit(Business business, WoUnitDuty woUnitDuty) throws Exception {
        if (StringUtils.isNotEmpty(woUnitDuty.getUnit())) {
            Unit unit = business.unit().pick(woUnitDuty.getUnit());
            if (null == unit) {
                throw new ExceptionUnitNotExist(woUnitDuty.getUnit());
            }
            WoUnit wo = WoUnit.copier.copy(unit);
            woUnitDuty.setWoUnit(wo);
        }
    }

    public static class Wo extends WoPersonAbstract {

        private static final long serialVersionUID = -8456354949288335211L;

        static WrapCopier<Person, Wo> copier = WrapCopierFactory.wo(Person.class, Wo.class, null,
                person_fieldsInvisible);

        @FieldDescribe("身份对象")
        private List<WoIdentity> woIdentityList;

        @FieldDescribe("角色对象")
        private List<WoRole> woRoleList;

        @FieldDescribe("群组对象")
        private List<WoGroup> woGroupList;

        @FieldDescribe("个人属性对象")
        private List<WoPersonAttribute> woPersonAttributeList;

        public List<WoIdentity> getWoIdentityList() {
            return woIdentityList;
        }

        public void setWoIdentityList(List<WoIdentity> woIdentityList) {
            this.woIdentityList = woIdentityList;
        }

        public List<WoRole> getWoRoleList() {
            return woRoleList;
        }

        public void setWoRoleList(List<WoRole> woRoleList) {
            this.woRoleList = woRoleList;
        }

        public List<WoGroup> getWoGroupList() {
            return woGroupList;
        }

        public void setWoGroupList(List<WoGroup> woGroupList) {
            this.woGroupList = woGroupList;
        }

        public List<WoPersonAttribute> getWoPersonAttributeList() {
            return woPersonAttributeList;
        }

        public void setWoPersonAttributeList(List<WoPersonAttribute> woPersonAttributeList) {
            this.woPersonAttributeList = woPersonAttributeList;
        }

    }

    public static class WoIdentity extends Identity {

        private static final long serialVersionUID = 6193615461099768815L;

        static WrapCopier<Identity, WoIdentity> copier = WrapCopierFactory.wo(Identity.class, WoIdentity.class, null,
                ListTools.toList(JpaObject.FieldsInvisible));

        @FieldDescribe("组织对象")
        private WoUnit woUnit;

        @FieldDescribe("组织职务对象")
        private List<WoUnitDuty> woUnitDutyList;

        public WoUnit getWoUnit() {
            return woUnit;
        }

        public void setWoUnit(WoUnit woUnit) {
            this.woUnit = woUnit;
        }

        public List<WoUnitDuty> getWoUnitDutyList() {
            return woUnitDutyList;
        }

        public void setWoUnitDutyList(List<WoUnitDuty> woUnitDutyList) {
            this.woUnitDutyList = woUnitDutyList;
        }

    }

    public static class WoGroup extends Group {

        private static final long serialVersionUID = 4503618773692247688L;

        static WrapCopier<Group, WoGroup> copier = WrapCopierFactory.wo(Group.class, WoGroup.class, null,
                ListTools.toList(JpaObject.FieldsInvisible));

    }

    public static class WoRole extends Role {

        private static final long serialVersionUID = -3903028273062897622L;

        static WrapCopier<Role, WoRole> copier = WrapCopierFactory.wo(Role.class, WoRole.class, null,
                ListTools.toList(JpaObject.FieldsInvisible));

    }

    public static class WoUnit extends Unit {

        private static final long serialVersionUID = 6172047743675016186L;

        private Long subDirectUnitCount;
        private Long subDirectIdentityCount;

        static WrapCopier<Unit, WoUnit> copier = WrapCopierFactory.wo(Unit.class, WoUnit.class, null,
                ListTools.toList(JpaObject.FieldsInvisible));

        public Long getSubDirectUnitCount() {
            return subDirectUnitCount;
        }

        public void setSubDirectUnitCount(Long subDirectUnitCount) {
            this.subDirectUnitCount = subDirectUnitCount;
        }

        public Long getSubDirectIdentityCount() {
            return subDirectIdentityCount;
        }

        public void setSubDirectIdentityCount(Long subDirectIdentityCount) {
            this.subDirectIdentityCount = subDirectIdentityCount;
        }
    }

    public static class WoUnitDuty extends UnitDuty {

        private static final long serialVersionUID = 3145496265299807549L;

        @FieldDescribe("组织对象")
        private WoUnit woUnit;

        static WrapCopier<UnitDuty, WoUnitDuty> copier = WrapCopierFactory.wo(UnitDuty.class, WoUnitDuty.class, null,
                ListTools.toList(JpaObject.FieldsInvisible));

        public WoUnit getWoUnit() {
            return woUnit;
        }

        public void setWoUnit(WoUnit woUnit) {
            this.woUnit = woUnit;
        }
    }

    public static class WoPersonAttribute extends PersonAttribute {

        private static final long serialVersionUID = -3155093360276871418L;

        static WrapCopier<PersonAttribute, WoPersonAttribute> copier = WrapCopierFactory.wo(PersonAttribute.class,
                WoPersonAttribute.class, null, ListTools.toList(JpaObject.FieldsInvisible));
    }

}
