package com.x.portal.assemble.surface.factory;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.cache.ApplicationCache;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.role.RoleDefinition;
import com.x.base.core.utils.ListTools;
import com.x.portal.assemble.surface.AbstractFactory;
import com.x.portal.assemble.surface.Business;
import com.x.portal.core.entity.Portal;
import com.x.portal.core.entity.Portal_;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

public class PortalFactory extends AbstractFactory {

	static Ehcache portalCache = ApplicationCache.instance().getCache(Portal.class);

	public PortalFactory(Business abstractBusiness) throws Exception {
		super(abstractBusiness);
	}

	public List<String> list(EffectivePerson effectivePerson) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Portal.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Portal> root = cq.from(Portal.class);
		Predicate p = cb.conjunction();
		if (effectivePerson.isNotManager() && (!this.business().organization().role().hasAny(effectivePerson.getName(),
				RoleDefinition.PortalManager))) {
			List<String> identities = this.business().organization().identity()
					.listNameWithPerson(effectivePerson.getName());
			List<String> departments = this.business().organization().department()
					.listNameWithPersonSupNested(effectivePerson.getName());
			List<String> companies = this.business().organization().company()
					.listNameWithPersonSupNested(effectivePerson.getName());
			p = cb.equal(root.get(Portal_.creatorPerson), effectivePerson.getName());
			p = cb.or(p, cb.isMember(effectivePerson.getName(), root.get(Portal_.controllerList)));
			p = cb.or(cb.and(cb.isEmpty(root.get(Portal_.availableIdentityList)),
					cb.isEmpty(root.get(Portal_.availableDepartmentList)),
					cb.isEmpty(root.get(Portal_.availableCompanyList))));
			p = cb.or(p, root.get(Portal_.availableIdentityList).in(identities));
			p = cb.or(p, root.get(Portal_.availableDepartmentList).in(departments));
			p = cb.or(p, root.get(Portal_.availableCompanyList).in(companies));
		}
		cq.select(root.get(Portal_.id)).where(p).distinct(true);
		return em.createQuery(cq).getResultList();
	}

	public boolean visible(EffectivePerson effectivePerson, Portal portal) throws Exception {
		if (effectivePerson.isManager() || this.business().organization().role().hasAny(effectivePerson.getName(),
				RoleDefinition.PortalManager)) {
			return true;
		}
		if (effectivePerson.isUser(portal.getCreatorPerson())) {
			return true;
		}
		if (effectivePerson.isUser(portal.getControllerList())) {
			return true;
		}
		if (ListTools.isEmpty(portal.getAvailableIdentityList(), portal.getAvailableDepartmentList(),
				portal.getAvailableCompanyList())) {
			return true;
		}
		List<String> identities = this.business().organization().identity()
				.listNameWithPerson(effectivePerson.getName());
		if (ListTools.containsAny(identities, portal.getAvailableIdentityList())) {
			return true;
		}
		List<String> departments = this.business().organization().department()
				.listNameWithPersonSupNested(effectivePerson.getName());
		if (ListTools.containsAny(departments, portal.getAvailableDepartmentList())) {
			return true;
		}
		List<String> companies = this.business().organization().company()
				.listNameWithPersonSupNested(effectivePerson.getName());
		if (ListTools.containsAny(companies, portal.getAvailableCompanyList())) {
			return true;
		}
		return false;
	}

	public Portal pick(String id) throws Exception {
		String cacheKey = ApplicationCache.concreteCacheKey(id);
		Element element = portalCache.get(cacheKey);
		if ((null != element) && (null != element.getObjectValue())) {
			return (Portal) element.getObjectValue();
		} else {
			Portal o = this.business().entityManagerContainer().find(id, Portal.class);
			if (null != o) {
				this.business().entityManagerContainer().get(Portal.class).detach(o);
				portalCache.put(new Element(id, o));
				return o;
			}
			return null;
		}
	}
}