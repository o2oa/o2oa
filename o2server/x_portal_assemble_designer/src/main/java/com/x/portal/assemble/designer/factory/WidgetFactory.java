package com.x.portal.assemble.designer.factory;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.project.tools.ListTools;
import com.x.portal.assemble.designer.AbstractFactory;
import com.x.portal.assemble.designer.Business;
import com.x.portal.core.entity.Widget;
import com.x.portal.core.entity.Widget_;

public class WidgetFactory extends AbstractFactory {

	public WidgetFactory(Business abstractBusiness) throws Exception {
		super(abstractBusiness);
	}

	public String getWithNameWithPortal(String name, String portalId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Widget.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Widget> root = cq.from(Widget.class);
		Predicate p = cb.equal(root.get(Widget_.name), name);
		p = cb.and(p, cb.equal(root.get(Widget_.portal), portalId));
		List<String> list = em.createQuery(cq.select(root.get(Widget_.id)).where(p)).setMaxResults(1).getResultList();
		if (list.isEmpty()) {
			return null;
		} else {
			return list.get(0);
		}
	}

	public String getWithAliasWithPortal(String alias, String portalId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Widget.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Widget> root = cq.from(Widget.class);
		Predicate p = cb.equal(root.get(Widget_.alias), alias);
		p = cb.and(p, cb.equal(root.get(Widget_.portal), portalId));
		List<String> list = em.createQuery(cq.select(root.get(Widget_.id)).where(p)).setMaxResults(1).getResultList();
		if (list.isEmpty()) {
			return null;
		} else {
			return list.get(0);
		}
	}

	public List<String> listWithPortal(String portalId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Widget.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Widget> root = cq.from(Widget.class);
		Predicate p = cb.equal(root.get(Widget_.portal), portalId);
		List<String> list = em.createQuery(cq.select(root.get(Widget_.id)).where(p)).getResultList();
		return list;
	}

	public List<Widget> listObjectWithPortal(String portalId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Widget.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Widget> cq = cb.createQuery(Widget.class);
		Root<Widget> root = cq.from(Widget.class);
		Predicate p = cb.equal(root.get(Widget_.portal), portalId);
		List<Widget> list = em.createQuery(cq.select(root).where(p)).getResultList();
		return list;
	}

	public List<String> listWithPortals(List<String> portalIds) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Widget.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Widget> root = cq.from(Widget.class);
		Predicate p = cb.conjunction();
		if(ListTools.isNotEmpty(portalIds)) {
			p = cb.isMember(root.get(Widget_.portal), cb.literal(portalIds));
		}
		cq.select(root.get(Widget_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

}
