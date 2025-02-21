package com.x.organization.assemble.express.jaxrs.person;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.cache.Cache.CacheKey;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WrapStringList;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.tools.ListTools;
import com.x.organization.assemble.express.Business;
import com.x.organization.core.entity.Group;
import com.x.organization.core.entity.Group_;
import com.x.organization.core.entity.Identity;
import com.x.organization.core.entity.Person;
import com.x.organization.core.entity.Role;
import com.x.organization.core.entity.Role_;
import com.x.organization.core.entity.Unit;
import com.x.organization.core.entity.UnitDuty;
import com.x.organization.core.entity.UnitDuty_;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.apache.commons.collections4.set.ListOrderedSet;
import org.apache.commons.lang3.StringUtils;

class ActionGetAuthInfo extends BaseAction {
    private static final Logger logger = LoggerFactory.getLogger(ActionGetAuthInfo.class);
    ActionResult<Wo> execute(EffectivePerson effectivePerson, String flag) throws Exception {
        logger.debug("execute:{}, flag:{}.", effectivePerson::getDistinguishedName, () -> flag);
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Business business = new Business(emc);
            ActionResult<Wo> result = new ActionResult<>();
            CacheKey cacheKey = new CacheKey(this.getClass(), flag);
            Optional<?> optional = CacheManager.get(cacheCategory, cacheKey);
            if (optional.isPresent()) {
                result.setData((Wo) optional.get());
            } else {
                Wo wo = new Wo();
                Person person = business.person().pick(flag);
                if (null != person) {
                    wo.addValue(person.getDistinguishedName(), true);
                    List<String> identities = this.referenceIdentity(business, wo, person.getId());
                    this.referenceRole(business, wo, person.getId());
                    this.referenceGroup(business, wo, person.getId(), identities);
                    CacheManager.put(cacheCategory, cacheKey, wo);
                }
                result.setData(wo);
            }
            return result;
        }
    }

    private List<String> referenceIdentity(Business business, Wo wo, String personId) throws Exception {
        List<String> identities = new ArrayList<>();
        List<Identity> os = business.identity().listByPerson(personId);
        for (Identity o : os) {
            wo.addValue(o.getDistinguishedName(), true);
            identities.add(o.getId());
            this.referenceUnit(business, wo, o);
            this.referenceUnitDuty(business, wo, o);
        }
        return identities;
    }

    private void referenceRole(Business business, Wo wo, String personId) throws Exception {
        EntityManager em = business.entityManagerContainer().get(Role.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Role> cq = cb.createQuery(Role.class);
        Root<Role> root = cq.from(Role.class);
        Predicate p = cb.isMember(personId, root.get(Role_.personList));
        em.createQuery(cq.select(root).where(p)).getResultList().stream().distinct()
                .forEach(o -> wo.addValue(o.getDistinguishedName(), true));
    }

    private void referenceGroup(Business business, Wo wo, String personId, List<String> identities) throws Exception {
        EntityManager em = business.entityManagerContainer().get(Group.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Group> cq = cb.createQuery(Group.class);
        Root<Group> root = cq.from(Group.class);
        Predicate p = cb.isMember(personId, root.get(Group_.personList));
        if (ListTools.isNotEmpty(identities)) {
            p = cb.or(p, root.get(Group_.identityList).in(identities));
        }
        List<Group> os = em.createQuery(cq.select(root).where(p)).getResultList().stream()
                .distinct().collect(Collectors.toList());
        ListOrderedSet<Group> set = new ListOrderedSet<>();
        os.forEach(o -> {
            set.add(o);
            try {
                set.addAll(business.group().listSupNestedObject(o));
            } catch (Exception e) {
                logger.error(e);
            }
        });
        set.forEach(o -> wo.addValue(o.getDistinguishedName(), true));
    }

    private void referenceUnit(Business business, Wo wo, Identity identity) throws Exception {
        if (StringUtils.isNotEmpty(identity.getUnit())) {
            Unit unit = business.unit().pick(identity.getUnit());
            if (null != unit) {
                wo.addValue(unit.getDistinguishedName(), true);
            }
        }
    }

    private void referenceUnitDuty(Business business, Wo wo, Identity identity) throws Exception {
        EntityManager em = business.entityManagerContainer().get(UnitDuty.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<UnitDuty> cq = cb.createQuery(UnitDuty.class);
        Root<UnitDuty> root = cq.from(UnitDuty.class);
        Predicate p = cb.isMember(identity.getId(), root.get(UnitDuty_.identityList));
        em.createQuery(cq.select(root).where(p)).getResultList()
                .forEach(o -> wo.addValue(o.getDistinguishedName(), true));
    }

    public static class Wo extends WrapStringList {

    }


}
