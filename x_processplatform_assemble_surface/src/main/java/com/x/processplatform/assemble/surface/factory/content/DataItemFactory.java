package com.x.processplatform.assemble.surface.factory.content;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.processplatform.assemble.surface.AbstractFactory;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.DataItem;
import com.x.processplatform.core.entity.content.DataItem_;

public class DataItemFactory extends AbstractFactory {

	public DataItemFactory(Business abstractBusiness) throws Exception {
		super(abstractBusiness);
	}

	public Long countWithJobWithPath(String job, String... paths) throws Exception {
		EntityManager em = this.entityManagerContainer().get(DataItem.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<DataItem> root = cq.from(DataItem.class);
		Predicate p = cb.equal(root.get(DataItem_.job), job);
		for (int i = 0; (i < paths.length && i < 8); i++) {
			p = cb.and(p, cb.equal(root.get(("path" + i)), paths[i]));
		}
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	public List<DataItem> listWithJobWithPath(String job, String... paths) throws Exception {
		EntityManager em = this.entityManagerContainer().get(DataItem.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<DataItem> cq = cb.createQuery(DataItem.class);
		Root<DataItem> root = cq.from(DataItem.class);
		Predicate p = cb.equal(root.get(DataItem_.job), job);
		for (int i = 0; (i < paths.length && i < 8); i++) {
			p = cb.and(p, cb.equal(root.get(("path" + i)), paths[i]));
		}
		cq.select(root).where(p);
		List<DataItem> list = em.createQuery(cq).getResultList();
		return list;
	}

	public DataItem getWithJobWithPath(String job, String path0, String path1, String path2, String path3, String path4,
			String path5, String path6, String path7) throws Exception {
		EntityManager em = this.entityManagerContainer().get(DataItem.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<DataItem> cq = cb.createQuery(DataItem.class);
		Root<DataItem> root = cq.from(DataItem.class);
		Predicate p = cb.equal(root.get(DataItem_.job), job);
		p = cb.and(p, cb.equal(root.get("path0"), path0));
		p = cb.and(p, cb.equal(root.get("path1"), path1));
		p = cb.and(p, cb.equal(root.get("path2"), path2));
		p = cb.and(p, cb.equal(root.get("path3"), path3));
		p = cb.and(p, cb.equal(root.get("path4"), path4));
		p = cb.and(p, cb.equal(root.get("path5"), path5));
		p = cb.and(p, cb.equal(root.get("path6"), path6));
		p = cb.and(p, cb.equal(root.get("path7"), path7));
		cq.select(root).where(p);
		List<DataItem> list = em.createQuery(cq).getResultList();
		if (list.size() == 0) {
			return null;
		}
		if (list.size() == 1) {
			return list.get(0);
		}
		throw new Exception("error mulit dataItem{job:" + job + ", path0:" + path0 + ", path1:" + path1 + ", path2:"
				+ path2 + ", path3:" + path3 + ", path4:" + path4 + ", path5:" + path5 + ", path6:" + path6 + ", path7:"
				+ path7 + "}");
	}

	public Integer getArrayLastIndexWithJobWithPath(String job, String... paths) throws Exception {
		EntityManager em = this.entityManagerContainer().get(DataItem.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<DataItem> cq = cb.createQuery(DataItem.class);
		Root<DataItem> root = cq.from(DataItem.class);
		Predicate p = cb.equal(root.get(DataItem_.job), job);
		for (int i = 0; ((i < paths.length) && (i < 8)); i++) {
			p = cb.and(p, cb.equal(root.get("path" + i), paths[i]));
		}
		for (int i = paths.length + 1; (i < 8); i++) {
			p = cb.and(p, cb.equal(root.get("path" + i), ""));
		}
		cq.select(root).where(p).orderBy(cb.desc(root.get("path" + paths.length + "Location")));
		List<DataItem> list = em.createQuery(cq).setMaxResults(1).getResultList();
		if (list.size() == 0) {
			return null;
		} else {
			return list.get(0).get("path" + paths.length + "Location", Integer.class);
		}
	}

	public List<DataItem> listWithJobWithPathWithAfterLocation(String job, Integer index, String... paths)
			throws Exception {
		EntityManager em = this.entityManagerContainer().get(DataItem.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<DataItem> cq = cb.createQuery(DataItem.class);
		Root<DataItem> root = cq.from(DataItem.class);
		Predicate p = cb.equal(root.get(DataItem_.job), job);
		for (int i = 0; ((i < (paths.length - 1)) && (i < 8)); i++) {
			p = cb.and(p, cb.equal(root.get("path" + i), paths[i]));
		}
		Path<Integer> locationPath = root.get("path" + (paths.length - 1) + "Location");
		p = cb.and(p, cb.greaterThan(locationPath, index));
		cq.select(root).where(p);
		List<DataItem> list = em.createQuery(cq).getResultList();
		return list;
	}
}