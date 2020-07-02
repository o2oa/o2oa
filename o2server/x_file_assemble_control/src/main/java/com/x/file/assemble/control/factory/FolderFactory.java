package com.x.file.assemble.control.factory;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.collections4.set.ListOrderedSet;

import com.x.file.assemble.control.AbstractFactory;
import com.x.file.assemble.control.Business;
import com.x.file.core.entity.personal.Folder;
import com.x.file.core.entity.personal.Folder_;

public class FolderFactory extends AbstractFactory {

	public FolderFactory(Business business) throws Exception {
		super(business);
	}

	public List<String> listTopWithPerson(String person) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Folder.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Folder> root = cq.from(Folder.class);
		Predicate p = cb.equal(root.get(Folder_.person), person);
		p = cb.and(p, cb.or(cb.isNull(root.get(Folder_.superior)), cb.equal(root.get(Folder_.superior), "")));
		cq.select(root.get(Folder_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	public List<String> listWithPersonWithSuperior(String person, String superior) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Folder.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Folder> root = cq.from(Folder.class);
		Predicate p = cb.equal(root.get(Folder_.person), person);
		p = cb.and(p, cb.equal(root.get(Folder_.superior), superior));
		cq.select(root.get(Folder_.id)).where(p);
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
		EntityManager em = this.entityManagerContainer().get(Folder.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Folder> root = cq.from(Folder.class);
		Predicate p = cb.equal(root.get(Folder_.superior), id);
		cq.select(root.get(Folder_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	public Long countSubDirect(String id) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Folder.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Folder> root = cq.from(Folder.class);
		Predicate p = cb.equal(root.get(Folder_.superior), id);
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}
}