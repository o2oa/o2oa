package com.x.organization.assemble.control.jaxrs.unit;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.organization.assemble.control.Business;
import com.x.organization.core.entity.Group;
import com.x.organization.core.entity.Group_;
import com.x.organization.core.entity.Identity;
import com.x.organization.core.entity.Identity_;
import com.x.organization.core.entity.Unit;
import com.x.organization.core.entity.UnitAttribute;
import com.x.organization.core.entity.UnitAttribute_;
import com.x.organization.core.entity.UnitDuty;
import com.x.organization.core.entity.UnitDuty_;

class ActionDelete extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionDelete.class);

    ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag) throws Exception {

        LOGGER.debug("execute:{}, flag:{}.", effectivePerson::getDistinguishedName, () -> flag);

        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            ActionResult<Wo> result = new ActionResult<>();
            Business business = new Business(emc);
            Unit unit = business.unit().pick(flag);
            if (null == unit) {
                throw new ExceptionUnitNotExist(flag);
            }
            if (!business.editable(effectivePerson, unit)) {
                throw new ExceptionDenyDeleteUnit(effectivePerson, unit.getName());
            }
            List<Unit> list = new ArrayList<>();
            list.add(unit);
            /** 查找子组织 */
            list.addAll(business.unit().listSubNestedObject(unit));
            list = list.stream().sorted(Comparator.comparing(Unit::getLevel).reversed()).collect(Collectors.toList());
            for (Unit o : list) {
                this.remove(business, o);
            }

            CacheManager.notify(Unit.class);

            Wo wo = new Wo();
            wo.setId(unit.getId());
            result.setData(wo);
            return result;
        }
    }

    public static class Wo extends WoId {
    }

    private void remove(Business business, Unit o) throws Exception {
        /** 前面是pick出来的,真正删除需要重新取出 */
        Unit unit = business.entityManagerContainer().find(o.getId(), Unit.class);
        if (null != unit) {
            /** 删除组织属性 */
            business.entityManagerContainer().beginTransaction(UnitAttribute.class);
            this.removeUnitAttribute(business, unit);
            business.entityManagerContainer().commit();
            /** 删除组织职务 */
            business.entityManagerContainer().beginTransaction(UnitDuty.class);
            this.removeUnitDuty(business, unit);
            /** 先提交,否则删除身份无法通过校验 */
            business.entityManagerContainer().commit();
            /** 先获取身份 */
            List<Identity> identities = this.listIdentity(business, unit);
            /** 获取身份的ID */
            List<String> identityIds = ListTools.extractProperty(identities, JpaObject.id_FIELDNAME, String.class, true,
                    true);
            /** 删除在其他组织属性中可能的值 */
            business.entityManagerContainer().beginTransaction(UnitDuty.class);
            this.removeMemberOfUnitDuty(business, identityIds);
            business.entityManagerContainer().commit();
            /** 删除在群组中可能的值 */
            business.entityManagerContainer().beginTransaction(Group.class);
            this.removeMemberOfGroup(business, unit);
            business.entityManagerContainer().commit();
            /** 删除身份 */
            business.entityManagerContainer().beginTransaction(Identity.class);
            for (Identity identity : identities) {
                business.entityManagerContainer().remove(identity, CheckRemoveType.all);
            }
            /** 先提交,否则删除组织无法通过校验 */
            business.entityManagerContainer().commit();
            /** 最后删除组织 */
            business.entityManagerContainer().beginTransaction(Unit.class);
            business.entityManagerContainer().remove(unit, CheckRemoveType.all);
            business.entityManagerContainer().commit();
        }
    }

    private void removeUnitAttribute(Business business, Unit unit) throws Exception {
        EntityManager em = business.entityManagerContainer().get(UnitAttribute.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<UnitAttribute> cq = cb.createQuery(UnitAttribute.class);
        Root<UnitAttribute> root = cq.from(UnitAttribute.class);
        Predicate p = cb.equal(root.get(UnitAttribute_.unit), unit.getId());
        List<UnitAttribute> os = em.createQuery(cq.select(root).where(p)).getResultList();
        for (UnitAttribute o : os) {
            business.entityManagerContainer().remove(o, CheckRemoveType.all);
        }
    }

    private void removeUnitDuty(Business business, Unit unit) throws Exception {
        EntityManager em = business.entityManagerContainer().get(UnitDuty.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<UnitDuty> cq = cb.createQuery(UnitDuty.class);
        Root<UnitDuty> root = cq.from(UnitDuty.class);
        Predicate p = cb.equal(root.get(UnitDuty_.unit), unit.getId());
        List<UnitDuty> os = em.createQuery(cq.select(root).where(p)).getResultList();
        for (UnitDuty o : os) {
            business.entityManagerContainer().remove(o, CheckRemoveType.all);
        }
    }

    private List<Identity> listIdentity(Business business, Unit unit) throws Exception {
        EntityManager em = business.entityManagerContainer().get(Identity.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Identity> cq = cb.createQuery(Identity.class);
        Root<Identity> root = cq.from(Identity.class);
        Predicate p = cb.equal(root.get(Identity_.unit), unit.getId());
        return em.createQuery(cq.select(root).where(p)).getResultList();
    }

    private void removeMemberOfUnitDuty(Business business, List<String> identityIds) throws Exception {
        EntityManager em = business.entityManagerContainer().get(UnitDuty.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<UnitDuty> cq = cb.createQuery(UnitDuty.class);
        Root<UnitDuty> root = cq.from(UnitDuty.class);
        Predicate p = root.get(UnitDuty_.identityList).in(identityIds);
        List<UnitDuty> os = em.createQuery(cq.select(root).where(p)).getResultList();
        for (UnitDuty o : os) {
            o.getIdentityList().removeAll(identityIds);
        }
    }

    private void removeMemberOfGroup(Business business, Unit unit) throws Exception {
        EntityManager em = business.entityManagerContainer().get(Group.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Group> cq = cb.createQuery(Group.class);
        Root<Group> root = cq.from(Group.class);
        Predicate p = cb.isMember(unit.getId(), root.get(Group_.unitList));
        List<Group> os = em.createQuery(cq.select(root).where(p)).getResultList();
        for (Group o : os) {
            o.getUnitList().remove(unit.getId());
        }
    }

}