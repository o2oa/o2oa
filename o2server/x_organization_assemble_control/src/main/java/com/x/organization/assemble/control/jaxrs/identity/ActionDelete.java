package com.x.organization.assemble.control.jaxrs.identity;

import java.util.*;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.organization.core.entity.*;
import org.apache.commons.lang3.StringUtils;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.exception.ExceptionEntityNotExist;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoId;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.organization.assemble.control.Business;

public class ActionDelete extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionDelete.class);

    ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag) throws Exception {

        LOGGER.debug("execute:{}, flag:{}.", effectivePerson::getDistinguishedName, () -> flag);

        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            ActionResult<Wo> result = new ActionResult<>();
            Business business = new Business(emc);
            Identity identity = business.identity().pick(flag);
            if (null == identity) {
                throw new ExceptionEntityNotExist(flag, Identity.class);
            }
            if (StringUtils.isNotEmpty(identity.getUnit())) {
                Unit unit = business.unit().pick(identity.getUnit());
                if (null == unit) {
                    throw new ExceptionUnitNotExist(identity.getUnit());
                }
                if (!effectivePerson.isSecurityManager() && !business.editable(effectivePerson, unit)) {
                    throw new ExceptionAccessDenied(effectivePerson, unit);
                }
                /** 由于有关联所以要分段提交，提交UnitDuty的成员删除。 */
                this.removeMemberOfUnitDuty(business, identity);
            }
            /** group的身份成员删除。 */
            this.removeMemberOfGroup(business, identity);

            /** 由于前面pick出来的需要重新取出 */
            identity = emc.find(identity.getId(), Identity.class);
            // /** 删除下属身份 */
            // emc.beginTransaction(Identity.class);
            // this.removeMemberOfJunior(business, identity);
            // emc.commit();
            /** 最后进行身份的删除 */
            /* 设置主身份 */
            final String id = identity.getId();
            List<Identity> others = emc.listEqual(Identity.class, Identity.person_FIELDNAME, identity.getPerson())
                    .stream().filter(o -> !StringUtils.equals(id, o.getId()))
                    .sorted(Comparator.comparing(Identity::getCreateTime, Comparator.nullsLast(Date::compareTo)))
                    .collect(Collectors.toList());
            Set<String> topUnits = new HashSet<>();
            for (int i = 0; i < others.size(); i++) {
                if (i == 0) {
                    others.get(i).setMajor(true);
                } else {
                    others.get(i).setMajor(false);
                }
                Unit u = business.unit().pick(others.get(i).getUnit());
                if (u != null) {
                    topUnits.add(this.topUnit(business, u).getId());
                }
            }
            Person person = emc.find(identity.getPerson(), Person.class);
            emc.beginTransaction(Identity.class);
            emc.remove(identity, CheckRemoveType.all);
            if(person != null){
                emc.beginTransaction(Person.class);
                person.setTopUnitList(new ArrayList<>(topUnits));
            }
            emc.commit();
            CacheManager.notify(Identity.class);
            CacheManager.notify(Person.class);

            Wo wo = new Wo();
            wo.setId(identity.getId());
            result.setData(wo);
            return result;
        }
    }

    public static class Wo extends WoId {

    }

    private void removeMemberOfUnitDuty(Business business, Identity identity) throws Exception {
        EntityManager em = business.entityManagerContainer().get(UnitDuty.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<UnitDuty> cq = cb.createQuery(UnitDuty.class);
        Root<UnitDuty> root = cq.from(UnitDuty.class);
        Predicate p = cb.isMember(identity.getId(), root.get(UnitDuty_.identityList));
        List<UnitDuty> os = em.createQuery(cq.select(root).where(p)).getResultList().stream().distinct()
                .collect(Collectors.toList());
        if (ListTools.isNotEmpty(os)) {
            business.entityManagerContainer().beginTransaction(UnitDuty.class);
            for (UnitDuty o : os) {
                o.getIdentityList().remove(identity.getId());
            }
            business.entityManagerContainer().commit();
            CacheManager.notify(UnitDuty.class);
        }
    }

    private void removeMemberOfGroup(Business business, Identity identity) throws Exception {
        List<Group> groups = business.group().listSupDirectWithIdentityObject(identity.getId());
        if (ListTools.isNotEmpty(groups)) {
            business.entityManagerContainer().beginTransaction(Group.class);
            for (Group g : groups) {
                g.getIdentityList().remove(identity.getId());
            }
            business.entityManagerContainer().commit();
            CacheManager.notify(Group.class);
        }
    }
}
