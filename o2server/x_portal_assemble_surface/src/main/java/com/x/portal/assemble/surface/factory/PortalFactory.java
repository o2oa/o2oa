package com.x.portal.assemble.surface.factory;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.BooleanUtils;

import com.x.base.core.project.cache.Cache.CacheCategory;
import com.x.base.core.project.cache.Cache.CacheKey;
import com.x.base.core.project.cache.CacheManager;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.organization.OrganizationDefinition;
import com.x.base.core.project.tools.ListTools;
import com.x.portal.assemble.surface.AbstractFactory;
import com.x.portal.assemble.surface.Business;
import com.x.portal.core.entity.Portal;
import com.x.portal.core.entity.Portal_;

public class PortalFactory extends AbstractFactory {

    static CacheCategory cache = new CacheCategory(Portal.class);

    public PortalFactory(Business abstractBusiness) throws Exception {
        super(abstractBusiness);
    }

    public List<String> list(EffectivePerson effectivePerson) throws Exception {
        EntityManager em = this.entityManagerContainer().get(Portal.class);
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<String> cq = cb.createQuery(String.class);
        Root<Portal> root = cq.from(Portal.class);
        Predicate p = cb.conjunction();
        if (effectivePerson.isNotManager() && (!this.business().organization().person().hasRole(effectivePerson,
                OrganizationDefinition.PortalManager))) {
            List<String> identities = this.business().organization().identity()
                    .listWithPerson(effectivePerson.getDistinguishedName());
            List<String> units = this.business().organization().unit()
                    .listWithPersonSupNested(effectivePerson.getDistinguishedName());
            p = cb.equal(root.get(Portal_.creatorPerson), effectivePerson.getDistinguishedName());
            p = cb.or(p, cb.isMember(effectivePerson.getDistinguishedName(), root.get(Portal_.controllerList)));
            p = cb.or(p, cb.and(cb.isEmpty(root.get(Portal_.availableIdentityList)),
                    cb.isEmpty(root.get(Portal_.availableUnitList))));
            p = cb.or(p, root.get(Portal_.availableIdentityList).in(identities));
            p = cb.or(p, root.get(Portal_.availableUnitList).in(units));
        }
        cq.select(root.get(Portal_.id)).where(p);
        return em.createQuery(cq).getResultList().stream().distinct().collect(Collectors.toList());
    }

    public boolean visible(EffectivePerson effectivePerson, Portal portal) throws Exception {
        if (ListTools.isEmpty(portal.getAvailableIdentityList(), portal.getAvailableUnitList())) {
            return true;
        }
        if (effectivePerson.isManager() || BooleanUtils.isTrue(this.business().organization().person()
                .hasRole(effectivePerson, OrganizationDefinition.PortalManager))) {
            return true;
        }
        if (effectivePerson.isPerson(portal.getCreatorPerson())) {
            return true;
        }
        if (effectivePerson.isPerson(portal.getControllerList())) {
            return true;
        }
        List<String> identities = this.business().organization().identity()
                .listWithPerson(effectivePerson.getDistinguishedName());
        if (ListTools.containsAny(identities, portal.getAvailableIdentityList())) {
            return true;
        }
        List<String> units = this.business().organization().unit().listWithPersonSupNested(effectivePerson);
        if (ListTools.containsAny(units, portal.getAvailableUnitList())) {
            return true;
        }
        return false;
    }

    public Portal pick(String flag) throws Exception {
        CacheKey cacheKey = new CacheKey(flag);
        Optional<?> optional = CacheManager.get(cache, cacheKey);
        if (optional.isPresent()) {
            return (Portal) optional.get();
        } else {
            Portal o = this.business().entityManagerContainer().flag(flag, Portal.class);
            if (null != o) {
                this.business().entityManagerContainer().get(Portal.class).detach(o);
                CacheManager.put(cache, cacheKey, o);
                return o;
            }
            return null;
        }
    }
}
