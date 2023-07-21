package com.x.processplatform.service.processing.factory;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.entity.dataitem.ItemCategory;
import com.x.processplatform.service.processing.AbstractFactory;
import com.x.processplatform.service.processing.Business;
import com.x.query.core.entity.Item;
import com.x.query.core.entity.Item_;

public class ItemFactory extends AbstractFactory {

	public ItemFactory(Business business) throws Exception {
		super(business);
	}

	public Long countWithJobWithPath(String job, String... paths) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Item.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Item> root = cq.from(Item.class);
		Predicate p = cb.equal(root.get(Item_.bundle), job);
		p = cb.and(p, cb.equal(root.get(Item_.itemCategory), ItemCategory.pp));
		for (int i = 0; (i < paths.length && i < 8); i++) {
			p = cb.and(p, cb.equal(root.get(("path" + i)), paths[i]));
		}
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	public List<Item> listWithJobWithPath(String job, String... paths) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Item.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Item> cq = cb.createQuery(Item.class);
		Root<Item> root = cq.from(Item.class);
		Predicate p = cb.equal(root.get(Item_.bundle), job);
		p = cb.and(p, cb.equal(root.get(Item_.itemCategory), ItemCategory.pp));
		for (int i = 0; (i < paths.length && i < 8); i++) {
			p = cb.and(p, cb.equal(root.get(("path" + i)), paths[i]));
		}
		cq.select(root).where(p);
		List<Item> list = em.createQuery(cq).getResultList();
		return list;
	}

	public Item getWithJobWithPath(String job, String path0, String path1, String path2, String path3, String path4,
			String path5, String path6, String path7) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Item.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Item> cq = cb.createQuery(Item.class);
		Root<Item> root = cq.from(Item.class);
		Predicate p = cb.equal(root.get(Item_.bundle), job);
		p = cb.and(p, cb.equal(root.get(Item_.itemCategory), ItemCategory.pp));
		p = cb.and(p, cb.equal(root.get(Item.path0_FIELDNAME), path0));
		p = cb.and(p, cb.equal(root.get(Item.path1_FIELDNAME), path1));
		p = cb.and(p, cb.equal(root.get(Item.path2_FIELDNAME), path2));
		p = cb.and(p, cb.equal(root.get(Item.path3_FIELDNAME), path3));
		p = cb.and(p, cb.equal(root.get(Item.path4_FIELDNAME), path4));
		p = cb.and(p, cb.equal(root.get(Item.path5_FIELDNAME), path5));
		p = cb.and(p, cb.equal(root.get(Item.path6_FIELDNAME), path6));
		p = cb.and(p, cb.equal(root.get(Item.path7_FIELDNAME), path7));
		cq.select(root).where(p);
		List<Item> list = em.createQuery(cq).getResultList();
		if (list.size() == 0) {
			return null;
		}
		if (list.size() == 1) {
			return list.get(0);
		}
		throw new Exception("error mulit Item{job:" + job + ", path0:" + path0 + ", path1:" + path1 + ", path2:" + path2
				+ ", path3:" + path3 + ", path4:" + path4 + ", path5:" + path5 + ", path6:" + path6 + ", path7:" + path7
				+ "}");
	}

	public Integer getArrayLastIndexWithJobWithPath(String job, String... paths) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Item.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Item> cq = cb.createQuery(Item.class);
		Root<Item> root = cq.from(Item.class);
		Predicate p = cb.equal(root.get(Item_.bundle), job);
		p = cb.and(p, cb.equal(root.get(Item_.itemCategory), ItemCategory.pp));
		for (int i = 0; ((i < paths.length) && (i < 8)); i++) {
			p = cb.and(p, cb.equal(root.get("path" + i), paths[i]));
		}
		for (int i = paths.length + 1; (i < 8); i++) {
			p = cb.and(p, cb.equal(root.get("path" + i), ""));
		}
		cq.select(root).where(p).orderBy(cb.desc(root.get("path" + paths.length + "Location")));
		List<Item> list = em.createQuery(cq).setMaxResults(1).getResultList();
		if (list.size() == 0) {
			return null;
		} else {
			return list.get(0).get("path" + paths.length + "Location", Integer.class);
		}
	}

	public List<Item> listWithJobWithPathWithAfterLocation(String job, Integer index, String... paths)
			throws Exception {
		EntityManager em = this.entityManagerContainer().get(Item.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Item> cq = cb.createQuery(Item.class);
		Root<Item> root = cq.from(Item.class);
		Predicate p = cb.equal(root.get(Item_.bundle), job);
		p = cb.and(p, cb.equal(root.get(Item_.itemCategory), ItemCategory.pp));
		for (int i = 0; ((i < (paths.length - 1)) && (i < 8)); i++) {
			p = cb.and(p, cb.equal(root.get("path" + i), paths[i]));
		}
		Path<Integer> locationPath = root.get("path" + (paths.length - 1) + "Location");
		p = cb.and(p, cb.greaterThan(locationPath, index));
		cq.select(root).where(p);
		List<Item> list = em.createQuery(cq).getResultList();
		return list;
	}
}