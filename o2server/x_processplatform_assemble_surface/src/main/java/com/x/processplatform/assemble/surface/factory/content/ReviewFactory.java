package com.x.processplatform.assemble.surface.factory.content;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.x.base.core.project.tools.ListTools;
import com.x.processplatform.assemble.surface.AbstractFactory;
import com.x.processplatform.assemble.surface.Business;
import com.x.processplatform.core.entity.content.Review;
import com.x.processplatform.core.entity.content.Review_;
import com.x.processplatform.core.entity.content.Work;
import com.x.processplatform.core.entity.content.WorkCompleted;

public class ReviewFactory extends AbstractFactory {

	public ReviewFactory(Business abstractBusiness) throws Exception {
		super(abstractBusiness);
	}

	public List<String> listWithWork(Work work) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Review.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Review> root = cq.from(Review.class);
		Predicate p = cb.equal(root.get(Review_.work), work.getId());
		cq.select(root.get(Review_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	public List<Review> listWithWorkObject(Work work) throws Exception {
		List<String> ids = this.listWithWork(work);
		if (ListTools.isEmpty(ids)) {
			return new ArrayList<Review>();
		}
		return this.business().entityManagerContainer().list(Review.class, ids);
	}

	public List<String> listWithJob(String job) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Review.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Review> root = cq.from(Review.class);
		Predicate p = cb.equal(root.get(Review_.job), job);
		cq.select(root.get(Review_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	public List<String> listWithWorkCompleted(String workCompletedId) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Review.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Review> root = cq.from(Review.class);
		Predicate p = cb.equal(root.get(Review_.workCompleted), workCompletedId);
		cq.select(root.get(Review_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	public Review getWithPersonAndJob(String person, String job) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Review.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Review> cq = cb.createQuery(Review.class);
		Root<Review> root = cq.from(Review.class);
		Predicate p = cb.equal(root.get(Review_.job), job);
		p = cb.and(p, cb.equal(root.get(Review_.person), person));
		cq.select(root).where(p);
		List<Review> list = em.createQuery(cq).setMaxResults(1).getResultList();
		return ListTools.isEmpty(list) ? null : list.get(0);
	}

	public Long countWithPersonWithWork(String person, Work work) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Review.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Review> root = cq.from(Review.class);
		Predicate p = cb.equal(root.get(Review_.work), work.getId());
		p = cb.and(p, cb.equal(root.get(Review_.person), person));
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	public Long countWithPersonWithWorkCompleted(String person, WorkCompleted workCompleted) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Review.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Review> root = cq.from(Review.class);
		Predicate p = cb.equal(root.get(Review_.workCompleted), workCompleted.getId());
		p = cb.and(p, cb.equal(root.get(Review_.person), person));
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	public Long countWithPersonWithJob(String person, String job) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Review.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Review> root = cq.from(Review.class);
		Predicate p = cb.equal(root.get(Review_.job), job);
		p = cb.and(p, cb.equal(root.get(Review_.person), person));
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	public Long countWithPerson(String person) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Review.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Review> root = cq.from(Review.class);
		Predicate p = cb.equal(root.get(Review_.person), person);
		cq.select(cb.count(root)).where(p);
		return em.createQuery(cq).getSingleResult();
	}

	public List<String> listWithProcess(String id) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Review.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Review> root = cq.from(Review.class);
		Predicate p = cb.equal(root.get(Review_.process), id);
		cq.select(root.get(Review_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	public List<String> listWithProcessWithCompleted(String id, Boolean completed) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Review.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Review> root = cq.from(Review.class);
		Predicate p = cb.equal(root.get(Review_.process), id);
		p = cb.and(p, cb.equal(root.get(Review_.completed), completed));
		cq.select(root.get(Review_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	public List<String> listWithApplication(String id) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Review.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Review> root = cq.from(Review.class);
		Predicate p = cb.equal(root.get(Review_.application), id);
		cq.select(root.get(Review_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	public List<String> listWithApplicationWithCompleted(String id, Boolean completed) throws Exception {
		EntityManager em = this.entityManagerContainer().get(Review.class);
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<String> cq = cb.createQuery(String.class);
		Root<Review> root = cq.from(Review.class);
		Predicate p = cb.equal(root.get(Review_.application), id);
		p = cb.and(p, cb.equal(root.get(Review_.completed), completed));
		cq.select(root.get(Review_.id)).where(p);
		return em.createQuery(cq).getResultList();
	}

	public <T extends Review> List<T> sort(List<T> list) {
		list = list.stream().sorted(Comparator.comparing(Review::getCreateTime, Comparator.nullsLast(Date::compareTo)))
				.collect(Collectors.toList());
		return list;
	}

}
