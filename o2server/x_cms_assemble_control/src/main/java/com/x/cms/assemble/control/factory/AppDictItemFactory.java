package com.x.cms.assemble.control.factory;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.cms.assemble.control.AbstractFactory;
import com.x.cms.assemble.control.Business;
import com.x.cms.core.entity.element.AppDictItem;
import com.x.cms.core.entity.element.AppDictItem_;

public class AppDictItemFactory extends AbstractFactory {

	public AppDictItemFactory(Business business) throws Exception {
		super(business);
	}

	public List<String> listWithAppDict(String appDictId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(AppDictItem.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<AppDictItem> root = cq.from(AppDictItem.class);
		Predicate p = cb.equal(root.get(AppDictItem_.bundle), appDictId);
		cq.select(root.get(AppDictItem_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}
	
	public List<AppDictItem> listWithAppDictWithPath(String appDictId, String... paths) throws Exception {
		EntityManager em = this.entityManagerContainer().get(AppDictItem.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<AppDictItem> cq = cb.createQuery(AppDictItem.class);
		Root<AppDictItem> root = cq.from(AppDictItem.class);
		Predicate p = cb.equal(root.get(AppDictItem_.bundle), appDictId);
		for (int i = 0; (i < paths.length && i < 8); i++) {
			p = cb.and(p, cb.equal(root.get(("path" + i)), paths[i]));
		}
		cq.select(root).where(p);
		List<AppDictItem> list = em.createQuery(cq).getResultList();
		return list;
	}

	public List<AppDictItem> listEntityWithAppDict(String appDictId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(AppDictItem.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<AppDictItem> cq = cb.createQuery(AppDictItem.class);
		Root<AppDictItem> root = cq.from(AppDictItem.class);
		Predicate p = cb.equal(root.get(AppDictItem_.bundle), appDictId);
		cq.select(root).where(p);
		return em.createQuery(cq).getResultList();
	}
	
	public AppDictItem getWithAppDictWithPath(String appDict, String path0, String path1, String path2, String path3, String path4, String path5,
			String path6, String path7) throws Exception {
		EntityManager em = this.entityManagerContainer().get(AppDictItem.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<AppDictItem> cq = cb.createQuery(AppDictItem.class);
		Root<AppDictItem> root = cq.from(AppDictItem.class);
		Predicate p = cb.equal(root.get(AppDictItem_.bundle), appDict);
		p = cb.and(p, cb.equal(root.get("path0"), path0));
		p = cb.and(p, cb.equal(root.get("path1"), path1));
		p = cb.and(p, cb.equal(root.get("path2"), path2));
		p = cb.and(p, cb.equal(root.get("path3"), path3));
		p = cb.and(p, cb.equal(root.get("path4"), path4));
		p = cb.and(p, cb.equal(root.get("path5"), path5));
		p = cb.and(p, cb.equal(root.get("path6"), path6));
		p = cb.and(p, cb.equal(root.get("path7"), path7));
		cq.select(root).where(p);
		List<AppDictItem> list = em.createQuery(cq).getResultList();
		if (list.size() == 0) {
			return null;
		}
		if (list.size() == 1) {
			return list.get(0);
		}
		throw new Exception("error mulit appDictItem{id:" + appDict + ", path0:" + path0 + ", path1:" + path1 + ", path2:" + path2 + ", path3:" + path3
				+ ", path4:" + path4 + ", path5:" + path5 + ", path6:" + path6 + ", path7:" + path7 + "}");
	}

	public Integer getArrayLastIndexWithAppDictWithPath(String appDict, String... paths) throws Exception {
		EntityManager em = this.entityManagerContainer().get(AppDictItem.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<AppDictItem> cq = cb.createQuery(AppDictItem.class);
		Root<AppDictItem> root = cq.from(AppDictItem.class);
		Predicate p = cb.equal(root.get(AppDictItem_.bundle), appDict);
		for (int i = 0; ((i < paths.length) && (i < 8)); i++) {
			p = cb.and(p, cb.equal(root.get("path" + i), paths[i]));
		}
		for (int i = paths.length + 1; (i < 8); i++) {
			p = cb.and(p, cb.equal(root.get("path" + i), ""));
		}
		cq.select(root).where(p).orderBy(cb.desc(root.get("path" + paths.length + "Location")));
		List<AppDictItem> list = em.createQuery(cq).setMaxResults(1).getResultList();
		if (list.size() == 0) {
			return null;
		} else {
			return list.get(0).get("path" + paths.length + "Location", Integer.class);
		}
	}

	public List<AppDictItem> listWithAppDictWithPathWithAfterLocation(String appDict, Integer index, String... paths) throws Exception {
		EntityManager em = this.entityManagerContainer().get(AppDictItem.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<AppDictItem> cq = cb.createQuery(AppDictItem.class);
		Root<AppDictItem> root = cq.from(AppDictItem.class);
		Predicate p = cb.equal(root.get(AppDictItem_.bundle), appDict);
		for (int i = 0; ((i < (paths.length - 1)) && (i < 8)); i++) {
			p = cb.and(p, cb.equal(root.get("path" + i), paths[i]));
		}
		Path<Integer> locationPath = root.get("path" + (paths.length - 1) + "Location");
		p = cb.and(p, cb.greaterThan(locationPath, index));
		cq.select(root).where(p);
		List<AppDictItem> list = em.createQuery(cq).getResultList();
		return list;
	}

	public List<AppDictItem> listObjectWithAppDict(String appDictId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(AppDictItem.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<AppDictItem> cq = cb.createQuery(AppDictItem.class);
		Root<AppDictItem> root = cq.from(AppDictItem.class);
		Predicate p = cb.equal(root.get(AppDictItem_.bundle), appDictId);
		cq.select(root).where(p);
		return em.createQuery(cq).getResultList();
	}
}