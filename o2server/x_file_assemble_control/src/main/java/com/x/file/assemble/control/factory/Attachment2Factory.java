package com.x.file.assemble.control.factory;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import com.x.file.assemble.control.AbstractFactory;
import com.x.file.assemble.control.Business;
import com.x.file.core.entity.open.FileStatus;
import com.x.file.core.entity.personal.Attachment2;
import com.x.file.core.entity.personal.Attachment2_;

public class Attachment2Factory extends AbstractFactory {

	public Attachment2Factory(Business business) throws Exception {
		super(business);
	}

	public List<String> listTopWithPerson(String person) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Attachment2.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Attachment2> root = cq.from(Attachment2.class);
		Predicate p = cb.equal(root.get(Attachment2_.person), person);
		p = cb.and(p, cb.equal(root.get(Attachment2_.status), FileStatus.VALID.getName()));
		p = cb.and(p, cb.equal(root.get(Attachment2_.folder), Business.TOP_FOLD));
		cq.select(root.get(Attachment2_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	public List<String> listWithFolder(String folder, String status) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Attachment2.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Attachment2> root = cq.from(Attachment2.class);
		Predicate p = cb.equal(root.get(Attachment2_.folder), folder);
		if (StringUtils.isNotEmpty(status)) {
			p = cb.and(p, cb.equal(root.get(Attachment2_.status), status));
		}
		cq.select(root.get(Attachment2_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	public List<String> listWithName(String person, String name) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Attachment2.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Attachment2> root = cq.from(Attachment2.class);
		Predicate p = cb.equal(root.get(Attachment2_.person), person);
		p = cb.and(p, cb.equal(root.get(Attachment2_.status), FileStatus.VALID.getName()));
		p = cb.and(p, cb.like(root.get(Attachment2_.name), "%" + name + "%"));
		cq.select(root.get(Attachment2_.id)).where(p);
		return em.createQuery(cq).setMaxResults(100).getResultList();
	}

	public long getUseCapacity(String person) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Attachment2.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Attachment2> root = cq.from(Attachment2.class);
		Predicate p = cb.equal(root.get(Attachment2_.person), person);
		p = cb.and(p, cb.equal(root.get(Attachment2_.status), FileStatus.VALID.getName()));
		cq.select(cb.sum(root.get(Attachment2_.length))).where(p);
		Long sum = em.createQuery(cq).getSingleResult();
		return sum == null ? 0 : sum;
	}

	public List<Attachment2> listWithFolder2(String folder, String status) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Attachment2.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Attachment2> cq = cb.createQuery(Attachment2.class);
		Root<Attachment2> root = cq.from(Attachment2.class);
		Predicate p = cb.equal(root.get(Attachment2_.folder), folder);
		if (StringUtils.isNotEmpty(status)) {
			p = cb.and(p, cb.equal(root.get(Attachment2_.status), status));
		}
		return em.createQuery(cq.where(p)).getResultList();
	}

}