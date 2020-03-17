package com.x.file.assemble.control.factory;

import com.x.file.assemble.control.AbstractFactory;
import com.x.file.assemble.control.Business;
import com.x.file.core.entity.personal.Folder2;
import com.x.file.core.entity.personal.Folder2_;
import org.apache.commons.collections4.set.ListOrderedSet;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

public class Folder2Factory extends AbstractFactory {

	public Folder2Factory(Business business) throws Exception {
		super(business);
	}

	public List<String> listTopWithPerson(String person) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Folder2.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Folder2> root = cq.from(Folder2.class);
		Predicate p = cb.equal(root.get(Folder2_.person), person);
		p = cb.and(p, cb.equal(root.get(Folder2_.status), "正常"));
		p = cb.and(p, cb.equal(root.get(Folder2_.superior), ""));
		cq.select(root.get(Folder2_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	public List<String> listWithPersonWithSuperior(String person, String superior, String status) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Folder2.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Folder2> root = cq.from(Folder2.class);
		Predicate p = cb.equal(root.get(Folder2_.person), person);
		p = cb.and(p, cb.equal(root.get(Folder2_.superior), superior));
		if(StringUtils.isNotEmpty(status)){
			p = cb.and(p, cb.equal(root.get(Folder2_.status), status));
		}
		cq.select(root.get(Folder2_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	public List<String> listSubNested(String id, String status) throws Exception {
		ListOrderedSet<String> list = new ListOrderedSet<>();
		List<String> subs = this.listSubDirect(id, status);
		for (String str : subs) {
			if (!list.contains(str)) {
				list.add(str);
				list.addAll(this.listSubNested(str, status));
			}
		}
		return list.asList();
	}

	public List<String> listSubDirect(String id, String status) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Folder2.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Folder2> root = cq.from(Folder2.class);
		Predicate p = cb.equal(root.get(Folder2_.superior), id);
		if(StringUtils.isNotEmpty(status)){
			p = cb.and(p, cb.equal(root.get(Folder2_.status), status));
		}
		cq.select(root.get(Folder2_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	public List<Folder2> listSubNested1(String id, String status) throws Exception {
		List<Folder2> list = new ArrayList<>();
		List<Folder2> subs = this.listSubDirect1(id, status);
		for (Folder2 folder : subs) {
			list.add(folder);
			list.addAll(this.listSubNested1(folder.getId(), status));
		}
		return list;
	}

	public List<Folder2> listSubDirect1(String id, String status) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Folder2.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Folder2> cq = cb.createQuery(Folder2.class);
		Root<Folder2> root = cq.from(Folder2.class);
		Predicate p = cb.equal(root.get(Folder2_.superior), id);
		if(StringUtils.isNotEmpty(status)){
			p = cb.and(p, cb.equal(root.get(Folder2_.status), status));
		}
		return em.createQuery(cq.where(p)).getResultList();
	}

	public Long countSubDirect(String id) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Folder2.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Folder2> root = cq.from(Folder2.class);
		Predicate p = cb.equal(root.get(Folder2_.superior), id);
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	public boolean exist(String person, String name, String superior,
							String excludeId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Folder2.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Folder2> root = cq.from(Folder2.class);
		Predicate p = cb.equal(root.get(Folder2_.person), person);
		p = cb.and(p, cb.equal(root.get(Folder2_.name), name));
		p = cb.and(p, cb.equal(root.get(Folder2_.superior), StringUtils.trimToEmpty(superior)));
		p = cb.and(p, cb.equal(root.get(Folder2_.status), "正常"));
		if (StringUtils.isNotEmpty(excludeId)) {
			p = cb.and(p, cb.notEqual(root.get(Folder2_.id), excludeId));
		}
		cq.select(cb.count(root)).where(p);
		long count = em.createQuery(cq).getSingleResult();
		return count > 0;
	}
}