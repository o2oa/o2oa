package com.x.portal.assemble.designer.factory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.project.tools.ListTools;
import com.x.portal.assemble.designer.AbstractFactory;
import com.x.portal.assemble.designer.Business;
import com.x.portal.core.entity.Page;
import com.x.portal.core.entity.Page_;

public class PageFactory extends AbstractFactory {

	private List<String> FIRSTPAGE_NAMES = new ArrayList<>(
			Arrays.asList(new String[] { "index", "default", "首页", "起始页", "first", "homepage" }));

	public PageFactory(Business abstractBusiness) throws Exception {
		super(abstractBusiness);
	}

	public String getWithNameWithPortal(String name, String portalId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Page.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Page> root = cq.from(Page.class);
		Predicate p = cb.equal(root.get(Page_.name), name);
		p = cb.and(p, cb.equal(root.get(Page_.portal), portalId));
		List<String> list = em.createQuery(cq.select(root.get(Page_.id)).where(p)).setMaxResults(1).getResultList();
		if (list.isEmpty()) {
			return null;
		} else {
			return list.get(0);
		}
	}

	public String getWithAliasWithPortal(String alias, String portalId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Page.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Page> root = cq.from(Page.class);
		Predicate p = cb.equal(root.get(Page_.alias), alias);
		p = cb.and(p, cb.equal(root.get(Page_.portal), portalId));
		List<String> list = em.createQuery(cq.select(root.get(Page_.id)).where(p)).setMaxResults(1).getResultList();
		if (list.isEmpty()) {
			return null;
		} else {
			return list.get(0);
		}
	}

	public List<String> listWithPortal(String portalId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Page.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Page> root = cq.from(Page.class);
		Predicate p = cb.equal(root.get(Page_.portal), portalId);
		List<String> list = em.createQuery(cq.select(root.get(Page_.id)).where(p)).getResultList();
		return list;
	}

	public List<Page> listObjectWithPortal(String portalId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Page.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Page> cq = cb.createQuery(Page.class);
		Root<Page> root = cq.from(Page.class);
		Predicate p = cb.equal(root.get(Page_.portal), portalId);
		List<Page> list = em.createQuery(cq.select(root).where(p)).getResultList();
		return list;
	}

	public List<String> listWithPortals(List<String> portalIds) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Page.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Page> root = cq.from(Page.class);
		Predicate p = cb.conjunction();
		if(ListTools.isNotEmpty(portalIds)) {
			p = cb.isMember(root.get(Page_.portal), cb.literal(portalIds));
		}
		cq.select(root.get(Page_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	public boolean isFirstPage(Page page) {
		return FIRSTPAGE_NAMES.contains(page.getName());
	}

	public String findFirstPage(String portalId) throws Exception {
		List<String> ids = this.listWithPortal(portalId);
		for (Page o : this.business().entityManagerContainer().list(Page.class, ids)) {
			if (FIRSTPAGE_NAMES.contains(o.getName())) {
				return o.getId();
			}
		}
		return null;
	}

}
