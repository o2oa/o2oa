package com.x.file.assemble.control.factory;

import com.x.file.assemble.control.AbstractFactory;
import com.x.file.assemble.control.Business;
import com.x.file.core.entity.personal.Folder2;
import com.x.file.core.entity.personal.Folder2_;
import org.apache.commons.collections4.set.ListOrderedSet;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
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
		p = cb.and(p, cb.equal(root.get(Folder2_.superior), ""));
		cq.select(root.get(Folder2_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	public List<String> listWithPersonWithSuperior(String person, String superior) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Folder2.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Folder2> root = cq.from(Folder2.class);
		Predicate p = cb.equal(root.get(Folder2_.person), person);
		p = cb.and(p, cb.equal(root.get(Folder2_.superior), superior));
		cq.select(root.get(Folder2_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	public List<String> listSubNested(String id) throws Exception {
		ListOrderedSet<String> list = new ListOrderedSet<>();
		List<String> subs = this.listSubDirect(id);
		for (String str : subs) {
			if (!list.contains(str)) {
				list.add(str);
				list.addAll(this.listSubNested(str));
			}
		}
		return list.asList();
	}

	public List<String> listSubDirect(String id) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Folder2.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Folder2> root = cq.from(Folder2.class);
		Predicate p = cb.equal(root.get(Folder2_.superior), id);
		cq.select(root.get(Folder2_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	public List<Folder2> listSubDirect1(String id) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Folder2.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Folder2> cq = cb.createQuery(Folder2.class);
		Root<Folder2> root = cq.from(Folder2.class);
		Predicate p = cb.equal(root.get(Folder2_.superior), id);
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
}