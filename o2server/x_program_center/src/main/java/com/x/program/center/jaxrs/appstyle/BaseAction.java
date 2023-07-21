package com.x.program.center.jaxrs.appstyle;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.project.jaxrs.StandardJaxrsAction;
import com.x.portal.core.entity.Page;
import com.x.portal.core.entity.Page_;
import com.x.portal.core.entity.Portal;
import com.x.portal.core.entity.Portal_;
import com.x.program.center.Business;

abstract class BaseAction extends StandardJaxrsAction {

	List<Portal> listMobilePortal(Business business) throws Exception {
		List<String> pageIds = this.listMobilePage(business);
		if (!pageIds.isEmpty()) {
			EntityManager em = business.entityManagerContainer().get(Portal.class);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Portal> cq = cb.createQuery(Portal.class);
			Root<Portal> root = cq.from(Portal.class);
			Predicate p = root.get(Portal_.firstPage).in(pageIds);
			List<Portal> os = em.createQuery(cq.select(root).where(p)).getResultList();
			os = os.stream().sorted(Comparator.comparing(Portal::getUpdateTime, Comparator.nullsLast(Date::compareTo)))
					.collect(Collectors.toList());
			return os;
		} else {
			return new ArrayList<>();
		}
	}

	private List<String> listMobilePage(Business business) throws Exception {
		EntityManager em = business.entityManagerContainer().get(Page.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Page> root = cq.from(Page.class);
		Predicate p = cb.conjunction();
		return em.createQuery(cq.select(root.get(Page_.id)).where(p)).getResultList();
	}

}