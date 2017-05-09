package com.x.portal.assemble.designer.jaxrs.portalcategory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.utils.ListTools;
import com.x.portal.assemble.designer.Business;
import com.x.portal.assemble.designer.wrapout.WrapOutPortalCategory;
import com.x.portal.core.entity.Portal;
import com.x.portal.core.entity.Portal_;

class ActionList extends ActionBase {

	ActionResult<List<WrapOutPortalCategory>> execute(EffectivePerson effectivePerson) throws Exception {
		try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
			ActionResult<List<WrapOutPortalCategory>> result = new ActionResult<>();
			List<WrapOutPortalCategory> wraps = new ArrayList<>();
			Business business = new Business(emc);
			List<String> list = new ArrayList<>(this.listPortalCategory(business, effectivePerson));
			Map<String, Long> counted = list.stream()
					.collect(Collectors.groupingBy(o -> Objects.toString(o), Collectors.counting()));
			LinkedHashMap<String, Long> sorted = counted.entrySet().stream()
					.sorted(Map.Entry.<String, Long> comparingByKey()).collect(Collectors.toMap(Map.Entry::getKey,
							Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
			for (Entry<String, Long> en : sorted.entrySet()) {
				WrapOutPortalCategory wrap = new WrapOutPortalCategory();
				wrap.setProtalCategory(en.getKey());
				wrap.setCount(en.getValue());
				wraps.add(wrap);
			}
			result.setData(wraps);
			return result;
		}
	}

	private List<String> listPortalCategory(Business business, EffectivePerson effectivePerson) throws Exception {
		List<String> ids = this.listPortal(business, effectivePerson);
		List<Portal> os = business.entityManagerContainer().fetchAttribute(ids, Portal.class, "portalCategory");
		return ListTools.extractProperty(os, "portalCategory", String.class, false, false);
	}

	private List<String> listPortal(Business business, EffectivePerson effectivePerson) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Portal.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Portal> root = cq.from(Portal.class);
		Predicate p = cb.conjunction();
		if (business.isPortalManager(effectivePerson)) {
			p = cb.isMember(effectivePerson.getName(), root.get(Portal_.controllerList));
			p = cb.or(p, cb.equal(root.get(Portal_.creatorPerson), effectivePerson.getName()));
		}
		cq.where(p).select(root.get(Portal_.id)).distinct(true);
		return em.createQuery(cq).getResultList();
	}

}