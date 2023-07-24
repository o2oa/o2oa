package com.x.file.assemble.control.factory;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.file.assemble.control.AbstractFactory;
import com.x.file.assemble.control.Business;
import com.x.file.core.entity.personal.Attachment;
import com.x.file.core.entity.personal.Attachment_;

public class AttachmentFactory extends AbstractFactory {

	public AttachmentFactory(Business business) throws Exception {
		super(business);
	}

	public List<String> listTopWithPerson(String person) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Attachment.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Attachment> root = cq.from(Attachment.class);
		Predicate p = cb.equal(root.get(Attachment_.person), person);
		p = cb.and(p, cb.or(cb.isNull(root.get(Attachment_.folder)), cb.equal(root.get(Attachment_.folder), "")));
		cq.select(root.get(Attachment_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	public List<String> listWithFolder(String folder) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Attachment.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Attachment> root = cq.from(Attachment.class);
		Predicate p = cb.equal(root.get(Attachment_.folder), folder);
		cq.select(root.get(Attachment_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	public List<String> listPersonWithShare(String person) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Attachment.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Attachment> root = cq.from(Attachment.class);
		Predicate p = cb.isMember(person, root.get(Attachment_.shareList));
		cq.select(root.get(Attachment_.person)).where(p);
		return em.createQuery(cq).getResultList().stream().distinct().collect(Collectors.toList());
	}

	public List<String> listPersonWithEditor(String person) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Attachment.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Attachment> root = cq.from(Attachment.class);
		Predicate p = cb.isMember(person, root.get(Attachment_.editorList));
		cq.select(root.get(Attachment_.person)).where(p);
		return em.createQuery(cq).getResultList().stream().distinct().collect(Collectors.toList());
	}

	public Long countWithPersonWithShare(String owner, String person) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Attachment.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Attachment> root = cq.from(Attachment.class);
		Predicate p = cb.isMember(person, root.get(Attachment_.shareList));
		p = cb.and(p, cb.equal(root.get(Attachment_.person), owner));
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	public Long countWithPersonWithEditor(String owner, String person) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Attachment.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Attachment> root = cq.from(Attachment.class);
		Predicate p = cb.isMember(person, root.get(Attachment_.editorList));
		p = cb.and(p, cb.equal(root.get(Attachment_.person), owner));
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	public List<String> listWithPersonWithShare(String owner, String person) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Attachment.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Attachment> root = cq.from(Attachment.class);
		Predicate p = cb.isMember(person, root.get(Attachment_.shareList));
		p = cb.and(p, cb.equal(root.get(Attachment_.person), owner));
		cq.select(root.get(Attachment_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	public List<String> listWithPersonWithEditor(String owner, String person) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Attachment.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Attachment> root = cq.from(Attachment.class);
		Predicate p = cb.isMember(person, root.get(Attachment_.editorList));
		p = cb.and(p, cb.equal(root.get(Attachment_.person), owner));
		cq.select(root.get(Attachment_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}
}