package com.x.processplatform.assemble.surface.factory.element;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.x.processplatform.assemble.surface.AbstractFactory;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.element.ApplicationDictItem;
import com.x.processplatform.core.entity.element.ApplicationDictItem_;

public class ApplicationDictItemFactory extends AbstractFactory {

	public ApplicationDictItemFactory(Business abstractBusiness) throws Exception {
		super(abstractBusiness);
	}

	public List<String> listWithApplicationDict(String applicationDict) throws Exception {
		EntityManager em = this.entityManagerContainer().get(ApplicationDictItem.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<ApplicationDictItem> root = cq.from(ApplicationDictItem.class);
		Predicate p = cb.equal(root.get(ApplicationDictItem_.bundle), applicationDict);
		cq.select(root.get(ApplicationDictItem_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	public List<ApplicationDictItem> listWithApplicationDictWithPath(String applicationDict, String... paths)
			throws Exception {
		EntityManager em = this.entityManagerContainer().get(ApplicationDictItem.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ApplicationDictItem> cq = cb.createQuery(ApplicationDictItem.class);
		Root<ApplicationDictItem> root = cq.from(ApplicationDictItem.class);
		Predicate p = cb.equal(root.get(ApplicationDictItem_.bundle), applicationDict);
		for (int i = 0; (i < paths.length && i < 8); i++) {
			p = cb.and(p, cb.equal(root.get(("path" + i)), paths[i]));
		}
		cq.select(root).where(p);
		List<ApplicationDictItem> list = em.createQuery(cq).getResultList();
		return list;
	}

	public ApplicationDictItem getWithApplicationDictWithPath(String applicationDict, String path0, String path1,
			String path2, String path3, String path4, String path5, String path6, String path7) throws Exception {
		EntityManager em = this.entityManagerContainer().get(ApplicationDictItem.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ApplicationDictItem> cq = cb.createQuery(ApplicationDictItem.class);
		Root<ApplicationDictItem> root = cq.from(ApplicationDictItem.class);
		Predicate p = cb.equal(root.get(ApplicationDictItem_.bundle), applicationDict);
		if (StringUtils.isEmpty(path0)) {
			p = cb.and(p, cb.or(cb.equal(root.get(ApplicationDictItem_.path0), path0),
					cb.isNull(root.get(ApplicationDictItem_.path0))));
		} else {
			p = cb.and(p, cb.equal(root.get(ApplicationDictItem_.path0), path0));
		}
		p = cb.and(p, cb.equal(root.get("path1"), path1));
		p = cb.and(p, cb.equal(root.get("path2"), path2));
		p = cb.and(p, cb.equal(root.get("path3"), path3));
		p = cb.and(p, cb.equal(root.get("path4"), path4));
		p = cb.and(p, cb.equal(root.get("path5"), path5));
		p = cb.and(p, cb.equal(root.get("path6"), path6));
		p = cb.and(p, cb.equal(root.get("path7"), path7));
		cq.select(root).where(p);
		List<ApplicationDictItem> list = em.createQuery(cq).getResultList();
		if (list.isEmpty()) {
			return null;
		}
		if (list.size() == 1) {
			return list.get(0);
		}
		throw new Exception("error mulit applicationDictItem{id:" + applicationDict + ", path0:" + path0 + ", path1:"
				+ path1 + ", path2:" + path2 + ", path3:" + path3 + ", path4:" + path4 + ", path5:" + path5 + ", path6:"
				+ path6 + ", path7:" + path7 + "}");
	}

	public Integer getArrayLastIndexWithApplicationDictWithPath(String applicationDict, String... paths)
			throws Exception {
		EntityManager em = this.entityManagerContainer().get(ApplicationDictItem.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ApplicationDictItem> cq = cb.createQuery(ApplicationDictItem.class);
		Root<ApplicationDictItem> root = cq.from(ApplicationDictItem.class);
		Predicate p = cb.equal(root.get(ApplicationDictItem_.bundle), applicationDict);
		for (int i = 0; ((i < paths.length) && (i < 8)); i++) {
			p = cb.and(p, cb.equal(root.get("path" + i), paths[i]));
		}
		for (int i = paths.length + 1; (i < 8); i++) {
			p = cb.and(p, cb.equal(root.get("path" + i), ""));
		}
		cq.select(root).where(p).orderBy(cb.desc(root.get("path" + paths.length + "Location")));
		List<ApplicationDictItem> list = em.createQuery(cq).setMaxResults(1).getResultList();
		if (list.isEmpty()) {
			return null;
		} else {
			return list.get(0).get("path" + paths.length + "Location", Integer.class);
		}
	}

	public List<ApplicationDictItem> listWithApplicationDictWithPathWithAfterLocation(String applicationDict,
			Integer index, String... paths) throws Exception {
		EntityManager em = this.entityManagerContainer().get(ApplicationDictItem.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ApplicationDictItem> cq = cb.createQuery(ApplicationDictItem.class);
		Root<ApplicationDictItem> root = cq.from(ApplicationDictItem.class);
		Predicate p = cb.equal(root.get(ApplicationDictItem_.bundle), applicationDict);
		for (int i = 0; ((i < (paths.length - 1)) && (i < 8)); i++) {
			p = cb.and(p, cb.equal(root.get("path" + i), paths[i]));
		}
		Path<Integer> locationPath = root.get("path" + (paths.length - 1) + "Location");
		p = cb.and(p, cb.greaterThan(locationPath, index));
		cq.select(root).where(p);
		List<ApplicationDictItem> list = em.createQuery(cq).getResultList();
		return list;
	}
}